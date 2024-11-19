//Taken from https://github.com/jgoppert/pumlg/ BSD license
  //Copyright (c) 2018, James Goppert
  //All rights reserved.
  //
  //Redistribution and use in source and binary forms, with or without
  //modification, are permitted provided that the following conditions are met:
  //
  //* Redistributions of source code must retain the above copyright notice, this
  //  list of conditions and the following disclaimer.
  //
  //* Redistributions in binary form must reproduce the above copyright notice,
  //  this list of conditions and the following disclaimer in the documentation
  //  and/or other materials provided with the distribution.
  //
  //* Neither the name of pymoca nor the names of its
  //  contributors may be used to endorse or promote products derived from
  //  this software without specific prior written permission.
  //
  //THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
  //AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
  //IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
  //DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE
  //FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL
  //DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
  //SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER
  //CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY,
  //OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
  //OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.

grammar PlantUMLGrammar;

umlFile: (text=.*? embeddedUml)* text=.*? EOF;
uml: embeddedUml EOF;

embeddedUml: STARTUML ident? NEWLINE+ diagram? ENDUML;

diagram: class_diagram;

class_diagram:
    (class_diagram_noise_line*
     (class_declaration | connection | enum_declaration | hide_declaration) NEWLINE
     class_diagram_noise_line*)+ |
     class_diagram_noise_line+;
class_diagram_noise_line: (~(CLASS | ENUM | HIDE | CONNECTOR | NEWLINE) .*?)? NEWLINE;

class_declaration:
    class_type ident (LCURLY
    (attribute | method | NEWLINE)*
    RCURLY )?
    ;

hide_declaration: HIDE ident;

attribute:
    visibility?
    modifiers?
    type_declaration?
    ident
    NEWLINE
    ;

method:
    visibility?
    modifiers?
    type_declaration?
    ident
    LPAREN function_argument_list? RPAREN
    NEWLINE
    ;

connection_left: class_name (DQUOTE attrib=ident MULTIPLICITY? DQUOTE)?;
connection_right: (DQUOTE attrib=ident MULTIPLICITY? DQUOTE)? class_name;

class_name: ident;

connection:
    left=connection_left
    connector=(CONNECTOR | MINUS)
    right=connection_right
    (COLON stereotype)?
    NEWLINE
    ;

visibility:
    PLUS      # visibility_public
    |MINUS    # visibility_private
    |SHARP    # visibility_protected
    |TILDE    # visibility_package
    ;

function_argument:
    type_declaration? ident
    ;

function_argument_list:
    function_argument (COMMA function_argument)*
    ;


template_argument:
    type_declaration
    ;

template_argument_list:
    template_argument (COMMA template_argument)*
    ;

ident:
    IDENT | ABSTRACT | CLASS
    ;

modifiers: STATIC_MOD | ABSTRACT_MOD;

stereotype:
    STEREO_BEGIN name=ident(LPAREN args+=ident RPAREN)? STEREO_END
    ;

type_declaration:
    ident TEMPLATE_TYPE_BEGIN template_argument_list? TEMPLATE_TYPE_END # template_type
    | ident LSQUARE RSQUARE                                             # list_type
    | ident                                                             # simple_type
    ;

class_type:
    ABSTRACT CLASS?
    | CLASS
    | INTERFACE CLASS?
    ;

item_list:
    (ident NEWLINE)+
    ;

enum_declaration:
    ENUM ident (LCURLY NEWLINE
    item_list?
    RCURLY )?
    ;

LPAREN: '(';
RPAREN: ')';
LSQUARE: '[';
RSQUARE: ']';
LCURLY: '{';
RCURLY: '}';
DQUOTE: '"';
COLON: ':';
SHARP: '#';
COMMA: ',';
TILDE: '~';
STATIC_MOD: '{static}';
ABSTRACT_MOD: '{abstract}';
STEREO_BEGIN: '<<';
STEREO_END: '>>';
TEMPLATE_TYPE_BEGIN: '<';
TEMPLATE_TYPE_END: '>';

STARTUML: '@startuml';
INTERFACE: 'interface';
HIDE: 'hide';
ENUM: 'enum';
ENDUML: '@enduml';
CLASS: 'class';
ABSTRACT: 'abstract';

CONNECTOR:
    '--'
    | '..'
    | '-->'
    | '<--'
    | '--*'
    | '*--'
    | '--o'
    | 'o--'
    | '<|--'
    | '--|>'
    | '..|>'
    | '<|..'
    | '*-->'
    | '<--*'
    | 'o-->'
    | '<--o'
    | '.'
    | '->'
    | '<-'
    | '-*'
    | '*-'
    | '-o'
    | 'o-'
    | '<|-'
    | '-|>'
    | '.|>'
    | '<|.'
    | '*->'
    | '<-*'
    | 'o->'
    | '<-o'
    ;

MULTIPLICITY: ('*' | '0..1' '0..*' | '1..*' | '1');
PLUS: '+';
MINUS: '-';

NEWPAGE: 'newpage' -> channel(HIDDEN);

NEWLINE: [\r\n];

IDENT: NONDIGIT ( DIGIT | NONDIGIT )*;
LINE_COMMENT: ('/' '/' .*? '\n') -> type(NEWLINE);
BLOCK_COMMENT: ('/*' .*? '*/') -> channel(HIDDEN);

WS: [ ]+ -> channel(HIDDEN);

ANYTHING_ELSE: .;

//=========================================================
// Fragments
//=========================================================
fragment NONDIGIT : [_a-zA-Z];
fragment DIGIT :  [0-9];
fragment UNSIGNED_INTEGER : DIGIT+;