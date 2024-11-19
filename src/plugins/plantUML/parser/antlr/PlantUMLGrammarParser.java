// Generated from PlantUMLGrammar.g4 by ANTLR 4.7.2
package plugins.plantUML.parser.antlr;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class PlantUMLGrammarParser extends Parser {
	static { RuntimeMetaData.checkVersion("4.7.2", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		LPAREN=1, RPAREN=2, LSQUARE=3, RSQUARE=4, LCURLY=5, RCURLY=6, DQUOTE=7, 
		COLON=8, SHARP=9, COMMA=10, TILDE=11, STATIC_MOD=12, ABSTRACT_MOD=13, 
		STEREO_BEGIN=14, STEREO_END=15, TEMPLATE_TYPE_BEGIN=16, TEMPLATE_TYPE_END=17, 
		STARTUML=18, INTERFACE=19, HIDE=20, ENUM=21, ENDUML=22, CLASS=23, ABSTRACT=24, 
		CONNECTOR=25, MULTIPLICITY=26, PLUS=27, MINUS=28, NEWPAGE=29, NEWLINE=30, 
		IDENT=31, BLOCK_COMMENT=32, WS=33, ANYTHING_ELSE=34;
	public static final int
		RULE_umlFile = 0, RULE_uml = 1, RULE_embeddedUml = 2, RULE_diagram = 3, 
		RULE_class_diagram = 4, RULE_class_diagram_noise_line = 5, RULE_class_declaration = 6, 
		RULE_hide_declaration = 7, RULE_attribute = 8, RULE_method = 9, RULE_connection_left = 10, 
		RULE_connection_right = 11, RULE_class_name = 12, RULE_connection = 13, 
		RULE_visibility = 14, RULE_function_argument = 15, RULE_function_argument_list = 16, 
		RULE_template_argument = 17, RULE_template_argument_list = 18, RULE_ident = 19, 
		RULE_modifiers = 20, RULE_stereotype = 21, RULE_type_declaration = 22, 
		RULE_class_type = 23, RULE_item_list = 24, RULE_enum_declaration = 25;
	private static String[] makeRuleNames() {
		return new String[] {
			"umlFile", "uml", "embeddedUml", "diagram", "class_diagram", "class_diagram_noise_line", 
			"class_declaration", "hide_declaration", "attribute", "method", "connection_left", 
			"connection_right", "class_name", "connection", "visibility", "function_argument", 
			"function_argument_list", "template_argument", "template_argument_list", 
			"ident", "modifiers", "stereotype", "type_declaration", "class_type", 
			"item_list", "enum_declaration"
		};
	}
	public static final String[] ruleNames = makeRuleNames();

	private static String[] makeLiteralNames() {
		return new String[] {
			null, "'('", "')'", "'['", "']'", "'{'", "'}'", "'\"'", "':'", "'#'", 
			"','", "'~'", "'{static}'", "'{abstract}'", "'<<'", "'>>'", "'<'", "'>'", 
			"'@startuml'", "'interface'", "'hide'", "'enum'", "'@enduml'", "'class'", 
			"'abstract'", null, null, "'+'", "'-'", "'newpage'"
		};
	}
	private static final String[] _LITERAL_NAMES = makeLiteralNames();
	private static String[] makeSymbolicNames() {
		return new String[] {
			null, "LPAREN", "RPAREN", "LSQUARE", "RSQUARE", "LCURLY", "RCURLY", "DQUOTE", 
			"COLON", "SHARP", "COMMA", "TILDE", "STATIC_MOD", "ABSTRACT_MOD", "STEREO_BEGIN", 
			"STEREO_END", "TEMPLATE_TYPE_BEGIN", "TEMPLATE_TYPE_END", "STARTUML", 
			"INTERFACE", "HIDE", "ENUM", "ENDUML", "CLASS", "ABSTRACT", "CONNECTOR", 
			"MULTIPLICITY", "PLUS", "MINUS", "NEWPAGE", "NEWLINE", "IDENT", "BLOCK_COMMENT", 
			"WS", "ANYTHING_ELSE"
		};
	}
	private static final String[] _SYMBOLIC_NAMES = makeSymbolicNames();
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}

	@Override
	public String getGrammarFileName() { return "PlantUMLGrammar.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public PlantUMLGrammarParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	public static class UmlFileContext extends ParserRuleContext {
		public Token text;
		public TerminalNode EOF() { return getToken(PlantUMLGrammarParser.EOF, 0); }
		public List<EmbeddedUmlContext> embeddedUml() {
			return getRuleContexts(EmbeddedUmlContext.class);
		}
		public EmbeddedUmlContext embeddedUml(int i) {
			return getRuleContext(EmbeddedUmlContext.class,i);
		}
		public UmlFileContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_umlFile; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterUmlFile(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitUmlFile(this);
		}
	}

	public final UmlFileContext umlFile() throws RecognitionException {
		UmlFileContext _localctx = new UmlFileContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_umlFile);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(61);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(55);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
					while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
						if ( _alt==1+1 ) {
							{
							{
							setState(52);
							((UmlFileContext)_localctx).text = matchWildcard();
							}
							} 
						}
						setState(57);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
					}
					setState(58);
					embeddedUml();
					}
					} 
				}
				setState(63);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,1,_ctx);
			}
			setState(67);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1+1 ) {
					{
					{
					setState(64);
					((UmlFileContext)_localctx).text = matchWildcard();
					}
					} 
				}
				setState(69);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,2,_ctx);
			}
			setState(70);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class UmlContext extends ParserRuleContext {
		public EmbeddedUmlContext embeddedUml() {
			return getRuleContext(EmbeddedUmlContext.class,0);
		}
		public TerminalNode EOF() { return getToken(PlantUMLGrammarParser.EOF, 0); }
		public UmlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_uml; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterUml(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitUml(this);
		}
	}

	public final UmlContext uml() throws RecognitionException {
		UmlContext _localctx = new UmlContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_uml);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(72);
			embeddedUml();
			setState(73);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class EmbeddedUmlContext extends ParserRuleContext {
		public TerminalNode STARTUML() { return getToken(PlantUMLGrammarParser.STARTUML, 0); }
		public TerminalNode ENDUML() { return getToken(PlantUMLGrammarParser.ENDUML, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(PlantUMLGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(PlantUMLGrammarParser.NEWLINE, i);
		}
		public DiagramContext diagram() {
			return getRuleContext(DiagramContext.class,0);
		}
		public EmbeddedUmlContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_embeddedUml; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterEmbeddedUml(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitEmbeddedUml(this);
		}
	}

	public final EmbeddedUmlContext embeddedUml() throws RecognitionException {
		EmbeddedUmlContext _localctx = new EmbeddedUmlContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_embeddedUml);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(75);
			match(STARTUML);
			setState(77);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CLASS) | (1L << ABSTRACT) | (1L << IDENT))) != 0)) {
				{
				setState(76);
				ident();
				}
			}

			setState(80); 
			_errHandler.sync(this);
			_alt = 1;
			do {
				switch (_alt) {
				case 1:
					{
					{
					setState(79);
					match(NEWLINE);
					}
					}
					break;
				default:
					throw new NoViableAltException(this);
				}
				setState(82); 
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,4,_ctx);
			} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
			setState(85);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,5,_ctx) ) {
			case 1:
				{
				setState(84);
				diagram();
				}
				break;
			}
			setState(87);
			match(ENDUML);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DiagramContext extends ParserRuleContext {
		public Class_diagramContext class_diagram() {
			return getRuleContext(Class_diagramContext.class,0);
		}
		public DiagramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_diagram; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterDiagram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitDiagram(this);
		}
	}

	public final DiagramContext diagram() throws RecognitionException {
		DiagramContext _localctx = new DiagramContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_diagram);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(89);
			class_diagram();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Class_diagramContext extends ParserRuleContext {
		public List<TerminalNode> NEWLINE() { return getTokens(PlantUMLGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(PlantUMLGrammarParser.NEWLINE, i);
		}
		public List<Class_declarationContext> class_declaration() {
			return getRuleContexts(Class_declarationContext.class);
		}
		public Class_declarationContext class_declaration(int i) {
			return getRuleContext(Class_declarationContext.class,i);
		}
		public List<ConnectionContext> connection() {
			return getRuleContexts(ConnectionContext.class);
		}
		public ConnectionContext connection(int i) {
			return getRuleContext(ConnectionContext.class,i);
		}
		public List<Enum_declarationContext> enum_declaration() {
			return getRuleContexts(Enum_declarationContext.class);
		}
		public Enum_declarationContext enum_declaration(int i) {
			return getRuleContext(Enum_declarationContext.class,i);
		}
		public List<Hide_declarationContext> hide_declaration() {
			return getRuleContexts(Hide_declarationContext.class);
		}
		public Hide_declarationContext hide_declaration(int i) {
			return getRuleContext(Hide_declarationContext.class,i);
		}
		public List<Class_diagram_noise_lineContext> class_diagram_noise_line() {
			return getRuleContexts(Class_diagram_noise_lineContext.class);
		}
		public Class_diagram_noise_lineContext class_diagram_noise_line(int i) {
			return getRuleContext(Class_diagram_noise_lineContext.class,i);
		}
		public Class_diagramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_class_diagram; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterClass_diagram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitClass_diagram(this);
		}
	}

	public final Class_diagramContext class_diagram() throws RecognitionException {
		Class_diagramContext _localctx = new Class_diagramContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_class_diagram);
		try {
			int _alt;
			setState(119);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,11,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(110); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(94);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
						while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
							if ( _alt==1 ) {
								{
								{
								setState(91);
								class_diagram_noise_line();
								}
								} 
							}
							setState(96);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,6,_ctx);
						}
						setState(101);
						_errHandler.sync(this);
						switch ( getInterpreter().adaptivePredict(_input,7,_ctx) ) {
						case 1:
							{
							setState(97);
							class_declaration();
							}
							break;
						case 2:
							{
							setState(98);
							connection();
							}
							break;
						case 3:
							{
							setState(99);
							enum_declaration();
							}
							break;
						case 4:
							{
							setState(100);
							hide_declaration();
							}
							break;
						}
						setState(103);
						match(NEWLINE);
						setState(107);
						_errHandler.sync(this);
						_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
						while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
							if ( _alt==1 ) {
								{
								{
								setState(104);
								class_diagram_noise_line();
								}
								} 
							}
							setState(109);
							_errHandler.sync(this);
							_alt = getInterpreter().adaptivePredict(_input,8,_ctx);
						}
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(112); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,9,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(115); 
				_errHandler.sync(this);
				_alt = 1;
				do {
					switch (_alt) {
					case 1:
						{
						{
						setState(114);
						class_diagram_noise_line();
						}
						}
						break;
					default:
						throw new NoViableAltException(this);
					}
					setState(117); 
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,10,_ctx);
				} while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER );
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Class_diagram_noise_lineContext extends ParserRuleContext {
		public List<TerminalNode> NEWLINE() { return getTokens(PlantUMLGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(PlantUMLGrammarParser.NEWLINE, i);
		}
		public TerminalNode CLASS() { return getToken(PlantUMLGrammarParser.CLASS, 0); }
		public TerminalNode ENUM() { return getToken(PlantUMLGrammarParser.ENUM, 0); }
		public TerminalNode HIDE() { return getToken(PlantUMLGrammarParser.HIDE, 0); }
		public TerminalNode CONNECTOR() { return getToken(PlantUMLGrammarParser.CONNECTOR, 0); }
		public Class_diagram_noise_lineContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_class_diagram_noise_line; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterClass_diagram_noise_line(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitClass_diagram_noise_line(this);
		}
	}

	public final Class_diagram_noise_lineContext class_diagram_noise_line() throws RecognitionException {
		Class_diagram_noise_lineContext _localctx = new Class_diagram_noise_lineContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_class_diagram_noise_line);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(128);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LPAREN) | (1L << RPAREN) | (1L << LSQUARE) | (1L << RSQUARE) | (1L << LCURLY) | (1L << RCURLY) | (1L << DQUOTE) | (1L << COLON) | (1L << SHARP) | (1L << COMMA) | (1L << TILDE) | (1L << STATIC_MOD) | (1L << ABSTRACT_MOD) | (1L << STEREO_BEGIN) | (1L << STEREO_END) | (1L << TEMPLATE_TYPE_BEGIN) | (1L << TEMPLATE_TYPE_END) | (1L << STARTUML) | (1L << INTERFACE) | (1L << ENDUML) | (1L << ABSTRACT) | (1L << MULTIPLICITY) | (1L << PLUS) | (1L << MINUS) | (1L << NEWPAGE) | (1L << IDENT) | (1L << BLOCK_COMMENT) | (1L << WS) | (1L << ANYTHING_ELSE))) != 0)) {
				{
				setState(121);
				_la = _input.LA(1);
				if ( _la <= 0 || ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << HIDE) | (1L << ENUM) | (1L << CLASS) | (1L << CONNECTOR) | (1L << NEWLINE))) != 0)) ) {
				_errHandler.recoverInline(this);
				}
				else {
					if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
					_errHandler.reportMatch(this);
					consume();
				}
				setState(125);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
				while ( _alt!=1 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
					if ( _alt==1+1 ) {
						{
						{
						setState(122);
						matchWildcard();
						}
						} 
					}
					setState(127);
					_errHandler.sync(this);
					_alt = getInterpreter().adaptivePredict(_input,12,_ctx);
				}
				}
			}

			setState(130);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Class_declarationContext extends ParserRuleContext {
		public Class_typeContext class_type() {
			return getRuleContext(Class_typeContext.class,0);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode LCURLY() { return getToken(PlantUMLGrammarParser.LCURLY, 0); }
		public TerminalNode RCURLY() { return getToken(PlantUMLGrammarParser.RCURLY, 0); }
		public List<AttributeContext> attribute() {
			return getRuleContexts(AttributeContext.class);
		}
		public AttributeContext attribute(int i) {
			return getRuleContext(AttributeContext.class,i);
		}
		public List<MethodContext> method() {
			return getRuleContexts(MethodContext.class);
		}
		public MethodContext method(int i) {
			return getRuleContext(MethodContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(PlantUMLGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(PlantUMLGrammarParser.NEWLINE, i);
		}
		public Class_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_class_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterClass_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitClass_declaration(this);
		}
	}

	public final Class_declarationContext class_declaration() throws RecognitionException {
		Class_declarationContext _localctx = new Class_declarationContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_class_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(132);
			class_type();
			setState(133);
			ident();
			setState(144);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LCURLY) {
				{
				setState(134);
				match(LCURLY);
				setState(140);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SHARP) | (1L << TILDE) | (1L << STATIC_MOD) | (1L << ABSTRACT_MOD) | (1L << CLASS) | (1L << ABSTRACT) | (1L << PLUS) | (1L << MINUS) | (1L << NEWLINE) | (1L << IDENT))) != 0)) {
					{
					setState(138);
					_errHandler.sync(this);
					switch ( getInterpreter().adaptivePredict(_input,14,_ctx) ) {
					case 1:
						{
						setState(135);
						attribute();
						}
						break;
					case 2:
						{
						setState(136);
						method();
						}
						break;
					case 3:
						{
						setState(137);
						match(NEWLINE);
						}
						break;
					}
					}
					setState(142);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				setState(143);
				match(RCURLY);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Hide_declarationContext extends ParserRuleContext {
		public TerminalNode HIDE() { return getToken(PlantUMLGrammarParser.HIDE, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Hide_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_hide_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterHide_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitHide_declaration(this);
		}
	}

	public final Hide_declarationContext hide_declaration() throws RecognitionException {
		Hide_declarationContext _localctx = new Hide_declarationContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_hide_declaration);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(146);
			match(HIDE);
			setState(147);
			ident();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class AttributeContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode NEWLINE() { return getToken(PlantUMLGrammarParser.NEWLINE, 0); }
		public VisibilityContext visibility() {
			return getRuleContext(VisibilityContext.class,0);
		}
		public ModifiersContext modifiers() {
			return getRuleContext(ModifiersContext.class,0);
		}
		public Type_declarationContext type_declaration() {
			return getRuleContext(Type_declarationContext.class,0);
		}
		public AttributeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_attribute; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterAttribute(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitAttribute(this);
		}
	}

	public final AttributeContext attribute() throws RecognitionException {
		AttributeContext _localctx = new AttributeContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_attribute);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(150);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SHARP) | (1L << TILDE) | (1L << PLUS) | (1L << MINUS))) != 0)) {
				{
				setState(149);
				visibility();
				}
			}

			setState(153);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STATIC_MOD || _la==ABSTRACT_MOD) {
				{
				setState(152);
				modifiers();
				}
			}

			setState(156);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(155);
				type_declaration();
				}
				break;
			}
			setState(158);
			ident();
			setState(159);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class MethodContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode LPAREN() { return getToken(PlantUMLGrammarParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlantUMLGrammarParser.RPAREN, 0); }
		public TerminalNode NEWLINE() { return getToken(PlantUMLGrammarParser.NEWLINE, 0); }
		public VisibilityContext visibility() {
			return getRuleContext(VisibilityContext.class,0);
		}
		public ModifiersContext modifiers() {
			return getRuleContext(ModifiersContext.class,0);
		}
		public Type_declarationContext type_declaration() {
			return getRuleContext(Type_declarationContext.class,0);
		}
		public Function_argument_listContext function_argument_list() {
			return getRuleContext(Function_argument_listContext.class,0);
		}
		public MethodContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterMethod(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitMethod(this);
		}
	}

	public final MethodContext method() throws RecognitionException {
		MethodContext _localctx = new MethodContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_method);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(162);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << SHARP) | (1L << TILDE) | (1L << PLUS) | (1L << MINUS))) != 0)) {
				{
				setState(161);
				visibility();
				}
			}

			setState(165);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==STATIC_MOD || _la==ABSTRACT_MOD) {
				{
				setState(164);
				modifiers();
				}
			}

			setState(168);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,22,_ctx) ) {
			case 1:
				{
				setState(167);
				type_declaration();
				}
				break;
			}
			setState(170);
			ident();
			setState(171);
			match(LPAREN);
			setState(173);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CLASS) | (1L << ABSTRACT) | (1L << IDENT))) != 0)) {
				{
				setState(172);
				function_argument_list();
				}
			}

			setState(175);
			match(RPAREN);
			setState(176);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Connection_leftContext extends ParserRuleContext {
		public IdentContext attrib;
		public Class_nameContext class_name() {
			return getRuleContext(Class_nameContext.class,0);
		}
		public List<TerminalNode> DQUOTE() { return getTokens(PlantUMLGrammarParser.DQUOTE); }
		public TerminalNode DQUOTE(int i) {
			return getToken(PlantUMLGrammarParser.DQUOTE, i);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode MULTIPLICITY() { return getToken(PlantUMLGrammarParser.MULTIPLICITY, 0); }
		public Connection_leftContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connection_left; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterConnection_left(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitConnection_left(this);
		}
	}

	public final Connection_leftContext connection_left() throws RecognitionException {
		Connection_leftContext _localctx = new Connection_leftContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_connection_left);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(178);
			class_name();
			setState(186);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DQUOTE) {
				{
				setState(179);
				match(DQUOTE);
				setState(180);
				((Connection_leftContext)_localctx).attrib = ident();
				setState(182);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MULTIPLICITY) {
					{
					setState(181);
					match(MULTIPLICITY);
					}
				}

				setState(184);
				match(DQUOTE);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Connection_rightContext extends ParserRuleContext {
		public IdentContext attrib;
		public Class_nameContext class_name() {
			return getRuleContext(Class_nameContext.class,0);
		}
		public List<TerminalNode> DQUOTE() { return getTokens(PlantUMLGrammarParser.DQUOTE); }
		public TerminalNode DQUOTE(int i) {
			return getToken(PlantUMLGrammarParser.DQUOTE, i);
		}
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode MULTIPLICITY() { return getToken(PlantUMLGrammarParser.MULTIPLICITY, 0); }
		public Connection_rightContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connection_right; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterConnection_right(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitConnection_right(this);
		}
	}

	public final Connection_rightContext connection_right() throws RecognitionException {
		Connection_rightContext _localctx = new Connection_rightContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_connection_right);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(195);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==DQUOTE) {
				{
				setState(188);
				match(DQUOTE);
				setState(189);
				((Connection_rightContext)_localctx).attrib = ident();
				setState(191);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if (_la==MULTIPLICITY) {
					{
					setState(190);
					match(MULTIPLICITY);
					}
				}

				setState(193);
				match(DQUOTE);
				}
			}

			setState(197);
			class_name();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Class_nameContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Class_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_class_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterClass_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitClass_name(this);
		}
	}

	public final Class_nameContext class_name() throws RecognitionException {
		Class_nameContext _localctx = new Class_nameContext(_ctx, getState());
		enterRule(_localctx, 24, RULE_class_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(199);
			ident();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ConnectionContext extends ParserRuleContext {
		public Connection_leftContext left;
		public Token connector;
		public Connection_rightContext right;
		public TerminalNode NEWLINE() { return getToken(PlantUMLGrammarParser.NEWLINE, 0); }
		public Connection_leftContext connection_left() {
			return getRuleContext(Connection_leftContext.class,0);
		}
		public Connection_rightContext connection_right() {
			return getRuleContext(Connection_rightContext.class,0);
		}
		public TerminalNode CONNECTOR() { return getToken(PlantUMLGrammarParser.CONNECTOR, 0); }
		public TerminalNode MINUS() { return getToken(PlantUMLGrammarParser.MINUS, 0); }
		public TerminalNode COLON() { return getToken(PlantUMLGrammarParser.COLON, 0); }
		public StereotypeContext stereotype() {
			return getRuleContext(StereotypeContext.class,0);
		}
		public ConnectionContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_connection; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterConnection(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitConnection(this);
		}
	}

	public final ConnectionContext connection() throws RecognitionException {
		ConnectionContext _localctx = new ConnectionContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_connection);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			((ConnectionContext)_localctx).left = connection_left();
			setState(202);
			((ConnectionContext)_localctx).connector = _input.LT(1);
			_la = _input.LA(1);
			if ( !(_la==CONNECTOR || _la==MINUS) ) {
				((ConnectionContext)_localctx).connector = (Token)_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			setState(203);
			((ConnectionContext)_localctx).right = connection_right();
			setState(206);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==COLON) {
				{
				setState(204);
				match(COLON);
				setState(205);
				stereotype();
				}
			}

			setState(208);
			match(NEWLINE);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class VisibilityContext extends ParserRuleContext {
		public VisibilityContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_visibility; }
	 
		public VisibilityContext() { }
		public void copyFrom(VisibilityContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class Visibility_publicContext extends VisibilityContext {
		public TerminalNode PLUS() { return getToken(PlantUMLGrammarParser.PLUS, 0); }
		public Visibility_publicContext(VisibilityContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterVisibility_public(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitVisibility_public(this);
		}
	}
	public static class Visibility_packageContext extends VisibilityContext {
		public TerminalNode TILDE() { return getToken(PlantUMLGrammarParser.TILDE, 0); }
		public Visibility_packageContext(VisibilityContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterVisibility_package(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitVisibility_package(this);
		}
	}
	public static class Visibility_privateContext extends VisibilityContext {
		public TerminalNode MINUS() { return getToken(PlantUMLGrammarParser.MINUS, 0); }
		public Visibility_privateContext(VisibilityContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterVisibility_private(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitVisibility_private(this);
		}
	}
	public static class Visibility_protectedContext extends VisibilityContext {
		public TerminalNode SHARP() { return getToken(PlantUMLGrammarParser.SHARP, 0); }
		public Visibility_protectedContext(VisibilityContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterVisibility_protected(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitVisibility_protected(this);
		}
	}

	public final VisibilityContext visibility() throws RecognitionException {
		VisibilityContext _localctx = new VisibilityContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_visibility);
		try {
			setState(214);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case PLUS:
				_localctx = new Visibility_publicContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(210);
				match(PLUS);
				}
				break;
			case MINUS:
				_localctx = new Visibility_privateContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(211);
				match(MINUS);
				}
				break;
			case SHARP:
				_localctx = new Visibility_protectedContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(212);
				match(SHARP);
				}
				break;
			case TILDE:
				_localctx = new Visibility_packageContext(_localctx);
				enterOuterAlt(_localctx, 4);
				{
				setState(213);
				match(TILDE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_argumentContext extends ParserRuleContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Type_declarationContext type_declaration() {
			return getRuleContext(Type_declarationContext.class,0);
		}
		public Function_argumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterFunction_argument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitFunction_argument(this);
		}
	}

	public final Function_argumentContext function_argument() throws RecognitionException {
		Function_argumentContext _localctx = new Function_argumentContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_function_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(217);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,30,_ctx) ) {
			case 1:
				{
				setState(216);
				type_declaration();
				}
				break;
			}
			setState(219);
			ident();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Function_argument_listContext extends ParserRuleContext {
		public List<Function_argumentContext> function_argument() {
			return getRuleContexts(Function_argumentContext.class);
		}
		public Function_argumentContext function_argument(int i) {
			return getRuleContext(Function_argumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PlantUMLGrammarParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PlantUMLGrammarParser.COMMA, i);
		}
		public Function_argument_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_function_argument_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterFunction_argument_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitFunction_argument_list(this);
		}
	}

	public final Function_argument_listContext function_argument_list() throws RecognitionException {
		Function_argument_listContext _localctx = new Function_argument_listContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_function_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(221);
			function_argument();
			setState(226);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(222);
				match(COMMA);
				setState(223);
				function_argument();
				}
				}
				setState(228);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Template_argumentContext extends ParserRuleContext {
		public Type_declarationContext type_declaration() {
			return getRuleContext(Type_declarationContext.class,0);
		}
		public Template_argumentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_template_argument; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterTemplate_argument(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitTemplate_argument(this);
		}
	}

	public final Template_argumentContext template_argument() throws RecognitionException {
		Template_argumentContext _localctx = new Template_argumentContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_template_argument);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(229);
			type_declaration();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Template_argument_listContext extends ParserRuleContext {
		public List<Template_argumentContext> template_argument() {
			return getRuleContexts(Template_argumentContext.class);
		}
		public Template_argumentContext template_argument(int i) {
			return getRuleContext(Template_argumentContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(PlantUMLGrammarParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(PlantUMLGrammarParser.COMMA, i);
		}
		public Template_argument_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_template_argument_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterTemplate_argument_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitTemplate_argument_list(this);
		}
	}

	public final Template_argument_listContext template_argument_list() throws RecognitionException {
		Template_argument_listContext _localctx = new Template_argument_listContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_template_argument_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(231);
			template_argument();
			setState(236);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(232);
				match(COMMA);
				setState(233);
				template_argument();
				}
				}
				setState(238);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class IdentContext extends ParserRuleContext {
		public TerminalNode IDENT() { return getToken(PlantUMLGrammarParser.IDENT, 0); }
		public TerminalNode ABSTRACT() { return getToken(PlantUMLGrammarParser.ABSTRACT, 0); }
		public TerminalNode CLASS() { return getToken(PlantUMLGrammarParser.CLASS, 0); }
		public IdentContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_ident; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterIdent(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitIdent(this);
		}
	}

	public final IdentContext ident() throws RecognitionException {
		IdentContext _localctx = new IdentContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_ident);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(239);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CLASS) | (1L << ABSTRACT) | (1L << IDENT))) != 0)) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ModifiersContext extends ParserRuleContext {
		public TerminalNode STATIC_MOD() { return getToken(PlantUMLGrammarParser.STATIC_MOD, 0); }
		public TerminalNode ABSTRACT_MOD() { return getToken(PlantUMLGrammarParser.ABSTRACT_MOD, 0); }
		public ModifiersContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_modifiers; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterModifiers(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitModifiers(this);
		}
	}

	public final ModifiersContext modifiers() throws RecognitionException {
		ModifiersContext _localctx = new ModifiersContext(_ctx, getState());
		enterRule(_localctx, 40, RULE_modifiers);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(241);
			_la = _input.LA(1);
			if ( !(_la==STATIC_MOD || _la==ABSTRACT_MOD) ) {
			_errHandler.recoverInline(this);
			}
			else {
				if ( _input.LA(1)==Token.EOF ) matchedEOF = true;
				_errHandler.reportMatch(this);
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StereotypeContext extends ParserRuleContext {
		public IdentContext name;
		public IdentContext ident;
		public List<IdentContext> args = new ArrayList<IdentContext>();
		public TerminalNode STEREO_BEGIN() { return getToken(PlantUMLGrammarParser.STEREO_BEGIN, 0); }
		public TerminalNode STEREO_END() { return getToken(PlantUMLGrammarParser.STEREO_END, 0); }
		public List<IdentContext> ident() {
			return getRuleContexts(IdentContext.class);
		}
		public IdentContext ident(int i) {
			return getRuleContext(IdentContext.class,i);
		}
		public TerminalNode LPAREN() { return getToken(PlantUMLGrammarParser.LPAREN, 0); }
		public TerminalNode RPAREN() { return getToken(PlantUMLGrammarParser.RPAREN, 0); }
		public StereotypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_stereotype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterStereotype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitStereotype(this);
		}
	}

	public final StereotypeContext stereotype() throws RecognitionException {
		StereotypeContext _localctx = new StereotypeContext(_ctx, getState());
		enterRule(_localctx, 42, RULE_stereotype);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(243);
			match(STEREO_BEGIN);
			setState(244);
			((StereotypeContext)_localctx).name = ident();
			setState(249);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LPAREN) {
				{
				setState(245);
				match(LPAREN);
				setState(246);
				((StereotypeContext)_localctx).ident = ident();
				((StereotypeContext)_localctx).args.add(((StereotypeContext)_localctx).ident);
				setState(247);
				match(RPAREN);
				}
			}

			setState(251);
			match(STEREO_END);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Type_declarationContext extends ParserRuleContext {
		public Type_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_type_declaration; }
	 
		public Type_declarationContext() { }
		public void copyFrom(Type_declarationContext ctx) {
			super.copyFrom(ctx);
		}
	}
	public static class List_typeContext extends Type_declarationContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode LSQUARE() { return getToken(PlantUMLGrammarParser.LSQUARE, 0); }
		public TerminalNode RSQUARE() { return getToken(PlantUMLGrammarParser.RSQUARE, 0); }
		public List_typeContext(Type_declarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterList_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitList_type(this);
		}
	}
	public static class Simple_typeContext extends Type_declarationContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public Simple_typeContext(Type_declarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterSimple_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitSimple_type(this);
		}
	}
	public static class Template_typeContext extends Type_declarationContext {
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode TEMPLATE_TYPE_BEGIN() { return getToken(PlantUMLGrammarParser.TEMPLATE_TYPE_BEGIN, 0); }
		public TerminalNode TEMPLATE_TYPE_END() { return getToken(PlantUMLGrammarParser.TEMPLATE_TYPE_END, 0); }
		public Template_argument_listContext template_argument_list() {
			return getRuleContext(Template_argument_listContext.class,0);
		}
		public Template_typeContext(Type_declarationContext ctx) { copyFrom(ctx); }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterTemplate_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitTemplate_type(this);
		}
	}

	public final Type_declarationContext type_declaration() throws RecognitionException {
		Type_declarationContext _localctx = new Type_declarationContext(_ctx, getState());
		enterRule(_localctx, 44, RULE_type_declaration);
		int _la;
		try {
			setState(265);
			_errHandler.sync(this);
			switch ( getInterpreter().adaptivePredict(_input,35,_ctx) ) {
			case 1:
				_localctx = new Template_typeContext(_localctx);
				enterOuterAlt(_localctx, 1);
				{
				setState(253);
				ident();
				setState(254);
				match(TEMPLATE_TYPE_BEGIN);
				setState(256);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CLASS) | (1L << ABSTRACT) | (1L << IDENT))) != 0)) {
					{
					setState(255);
					template_argument_list();
					}
				}

				setState(258);
				match(TEMPLATE_TYPE_END);
				}
				break;
			case 2:
				_localctx = new List_typeContext(_localctx);
				enterOuterAlt(_localctx, 2);
				{
				setState(260);
				ident();
				setState(261);
				match(LSQUARE);
				setState(262);
				match(RSQUARE);
				}
				break;
			case 3:
				_localctx = new Simple_typeContext(_localctx);
				enterOuterAlt(_localctx, 3);
				{
				setState(264);
				ident();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Class_typeContext extends ParserRuleContext {
		public TerminalNode ABSTRACT() { return getToken(PlantUMLGrammarParser.ABSTRACT, 0); }
		public TerminalNode CLASS() { return getToken(PlantUMLGrammarParser.CLASS, 0); }
		public TerminalNode INTERFACE() { return getToken(PlantUMLGrammarParser.INTERFACE, 0); }
		public Class_typeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_class_type; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterClass_type(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitClass_type(this);
		}
	}

	public final Class_typeContext class_type() throws RecognitionException {
		Class_typeContext _localctx = new Class_typeContext(_ctx, getState());
		enterRule(_localctx, 46, RULE_class_type);
		try {
			setState(276);
			_errHandler.sync(this);
			switch (_input.LA(1)) {
			case ABSTRACT:
				enterOuterAlt(_localctx, 1);
				{
				setState(267);
				match(ABSTRACT);
				setState(269);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,36,_ctx) ) {
				case 1:
					{
					setState(268);
					match(CLASS);
					}
					break;
				}
				}
				break;
			case CLASS:
				enterOuterAlt(_localctx, 2);
				{
				setState(271);
				match(CLASS);
				}
				break;
			case INTERFACE:
				enterOuterAlt(_localctx, 3);
				{
				setState(272);
				match(INTERFACE);
				setState(274);
				_errHandler.sync(this);
				switch ( getInterpreter().adaptivePredict(_input,37,_ctx) ) {
				case 1:
					{
					setState(273);
					match(CLASS);
					}
					break;
				}
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Item_listContext extends ParserRuleContext {
		public List<IdentContext> ident() {
			return getRuleContexts(IdentContext.class);
		}
		public IdentContext ident(int i) {
			return getRuleContext(IdentContext.class,i);
		}
		public List<TerminalNode> NEWLINE() { return getTokens(PlantUMLGrammarParser.NEWLINE); }
		public TerminalNode NEWLINE(int i) {
			return getToken(PlantUMLGrammarParser.NEWLINE, i);
		}
		public Item_listContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_item_list; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterItem_list(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitItem_list(this);
		}
	}

	public final Item_listContext item_list() throws RecognitionException {
		Item_listContext _localctx = new Item_listContext(_ctx, getState());
		enterRule(_localctx, 48, RULE_item_list);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(281); 
			_errHandler.sync(this);
			_la = _input.LA(1);
			do {
				{
				{
				setState(278);
				ident();
				setState(279);
				match(NEWLINE);
				}
				}
				setState(283); 
				_errHandler.sync(this);
				_la = _input.LA(1);
			} while ( (((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CLASS) | (1L << ABSTRACT) | (1L << IDENT))) != 0) );
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Enum_declarationContext extends ParserRuleContext {
		public TerminalNode ENUM() { return getToken(PlantUMLGrammarParser.ENUM, 0); }
		public IdentContext ident() {
			return getRuleContext(IdentContext.class,0);
		}
		public TerminalNode LCURLY() { return getToken(PlantUMLGrammarParser.LCURLY, 0); }
		public TerminalNode NEWLINE() { return getToken(PlantUMLGrammarParser.NEWLINE, 0); }
		public TerminalNode RCURLY() { return getToken(PlantUMLGrammarParser.RCURLY, 0); }
		public Item_listContext item_list() {
			return getRuleContext(Item_listContext.class,0);
		}
		public Enum_declarationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_enum_declaration; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).enterEnum_declaration(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof PlantUMLGrammarListener ) ((PlantUMLGrammarListener)listener).exitEnum_declaration(this);
		}
	}

	public final Enum_declarationContext enum_declaration() throws RecognitionException {
		Enum_declarationContext _localctx = new Enum_declarationContext(_ctx, getState());
		enterRule(_localctx, 50, RULE_enum_declaration);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(285);
			match(ENUM);
			setState(286);
			ident();
			setState(293);
			_errHandler.sync(this);
			_la = _input.LA(1);
			if (_la==LCURLY) {
				{
				setState(287);
				match(LCURLY);
				setState(288);
				match(NEWLINE);
				setState(290);
				_errHandler.sync(this);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CLASS) | (1L << ABSTRACT) | (1L << IDENT))) != 0)) {
					{
					setState(289);
					item_list();
					}
				}

				setState(292);
				match(RCURLY);
				}
			}

			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\3$\u012a\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31\t\31"+
		"\4\32\t\32\4\33\t\33\3\2\7\28\n\2\f\2\16\2;\13\2\3\2\7\2>\n\2\f\2\16\2"+
		"A\13\2\3\2\7\2D\n\2\f\2\16\2G\13\2\3\2\3\2\3\3\3\3\3\3\3\4\3\4\5\4P\n"+
		"\4\3\4\6\4S\n\4\r\4\16\4T\3\4\5\4X\n\4\3\4\3\4\3\5\3\5\3\6\7\6_\n\6\f"+
		"\6\16\6b\13\6\3\6\3\6\3\6\3\6\5\6h\n\6\3\6\3\6\7\6l\n\6\f\6\16\6o\13\6"+
		"\6\6q\n\6\r\6\16\6r\3\6\6\6v\n\6\r\6\16\6w\5\6z\n\6\3\7\3\7\7\7~\n\7\f"+
		"\7\16\7\u0081\13\7\5\7\u0083\n\7\3\7\3\7\3\b\3\b\3\b\3\b\3\b\3\b\7\b\u008d"+
		"\n\b\f\b\16\b\u0090\13\b\3\b\5\b\u0093\n\b\3\t\3\t\3\t\3\n\5\n\u0099\n"+
		"\n\3\n\5\n\u009c\n\n\3\n\5\n\u009f\n\n\3\n\3\n\3\n\3\13\5\13\u00a5\n\13"+
		"\3\13\5\13\u00a8\n\13\3\13\5\13\u00ab\n\13\3\13\3\13\3\13\5\13\u00b0\n"+
		"\13\3\13\3\13\3\13\3\f\3\f\3\f\3\f\5\f\u00b9\n\f\3\f\3\f\5\f\u00bd\n\f"+
		"\3\r\3\r\3\r\5\r\u00c2\n\r\3\r\3\r\5\r\u00c6\n\r\3\r\3\r\3\16\3\16\3\17"+
		"\3\17\3\17\3\17\3\17\5\17\u00d1\n\17\3\17\3\17\3\20\3\20\3\20\3\20\5\20"+
		"\u00d9\n\20\3\21\5\21\u00dc\n\21\3\21\3\21\3\22\3\22\3\22\7\22\u00e3\n"+
		"\22\f\22\16\22\u00e6\13\22\3\23\3\23\3\24\3\24\3\24\7\24\u00ed\n\24\f"+
		"\24\16\24\u00f0\13\24\3\25\3\25\3\26\3\26\3\27\3\27\3\27\3\27\3\27\3\27"+
		"\5\27\u00fc\n\27\3\27\3\27\3\30\3\30\3\30\5\30\u0103\n\30\3\30\3\30\3"+
		"\30\3\30\3\30\3\30\3\30\5\30\u010c\n\30\3\31\3\31\5\31\u0110\n\31\3\31"+
		"\3\31\3\31\5\31\u0115\n\31\5\31\u0117\n\31\3\32\3\32\3\32\6\32\u011c\n"+
		"\32\r\32\16\32\u011d\3\33\3\33\3\33\3\33\3\33\5\33\u0125\n\33\3\33\5\33"+
		"\u0128\n\33\3\33\59E\177\2\34\2\4\6\b\n\f\16\20\22\24\26\30\32\34\36 "+
		"\"$&(*,.\60\62\64\2\6\6\2\26\27\31\31\33\33  \4\2\33\33\36\36\4\2\31\32"+
		"!!\3\2\16\17\2\u0140\2?\3\2\2\2\4J\3\2\2\2\6M\3\2\2\2\b[\3\2\2\2\ny\3"+
		"\2\2\2\f\u0082\3\2\2\2\16\u0086\3\2\2\2\20\u0094\3\2\2\2\22\u0098\3\2"+
		"\2\2\24\u00a4\3\2\2\2\26\u00b4\3\2\2\2\30\u00c5\3\2\2\2\32\u00c9\3\2\2"+
		"\2\34\u00cb\3\2\2\2\36\u00d8\3\2\2\2 \u00db\3\2\2\2\"\u00df\3\2\2\2$\u00e7"+
		"\3\2\2\2&\u00e9\3\2\2\2(\u00f1\3\2\2\2*\u00f3\3\2\2\2,\u00f5\3\2\2\2."+
		"\u010b\3\2\2\2\60\u0116\3\2\2\2\62\u011b\3\2\2\2\64\u011f\3\2\2\2\668"+
		"\13\2\2\2\67\66\3\2\2\28;\3\2\2\29:\3\2\2\29\67\3\2\2\2:<\3\2\2\2;9\3"+
		"\2\2\2<>\5\6\4\2=9\3\2\2\2>A\3\2\2\2?=\3\2\2\2?@\3\2\2\2@E\3\2\2\2A?\3"+
		"\2\2\2BD\13\2\2\2CB\3\2\2\2DG\3\2\2\2EF\3\2\2\2EC\3\2\2\2FH\3\2\2\2GE"+
		"\3\2\2\2HI\7\2\2\3I\3\3\2\2\2JK\5\6\4\2KL\7\2\2\3L\5\3\2\2\2MO\7\24\2"+
		"\2NP\5(\25\2ON\3\2\2\2OP\3\2\2\2PR\3\2\2\2QS\7 \2\2RQ\3\2\2\2ST\3\2\2"+
		"\2TR\3\2\2\2TU\3\2\2\2UW\3\2\2\2VX\5\b\5\2WV\3\2\2\2WX\3\2\2\2XY\3\2\2"+
		"\2YZ\7\30\2\2Z\7\3\2\2\2[\\\5\n\6\2\\\t\3\2\2\2]_\5\f\7\2^]\3\2\2\2_b"+
		"\3\2\2\2`^\3\2\2\2`a\3\2\2\2ag\3\2\2\2b`\3\2\2\2ch\5\16\b\2dh\5\34\17"+
		"\2eh\5\64\33\2fh\5\20\t\2gc\3\2\2\2gd\3\2\2\2ge\3\2\2\2gf\3\2\2\2hi\3"+
		"\2\2\2im\7 \2\2jl\5\f\7\2kj\3\2\2\2lo\3\2\2\2mk\3\2\2\2mn\3\2\2\2nq\3"+
		"\2\2\2om\3\2\2\2p`\3\2\2\2qr\3\2\2\2rp\3\2\2\2rs\3\2\2\2sz\3\2\2\2tv\5"+
		"\f\7\2ut\3\2\2\2vw\3\2\2\2wu\3\2\2\2wx\3\2\2\2xz\3\2\2\2yp\3\2\2\2yu\3"+
		"\2\2\2z\13\3\2\2\2{\177\n\2\2\2|~\13\2\2\2}|\3\2\2\2~\u0081\3\2\2\2\177"+
		"\u0080\3\2\2\2\177}\3\2\2\2\u0080\u0083\3\2\2\2\u0081\177\3\2\2\2\u0082"+
		"{\3\2\2\2\u0082\u0083\3\2\2\2\u0083\u0084\3\2\2\2\u0084\u0085\7 \2\2\u0085"+
		"\r\3\2\2\2\u0086\u0087\5\60\31\2\u0087\u0092\5(\25\2\u0088\u008e\7\7\2"+
		"\2\u0089\u008d\5\22\n\2\u008a\u008d\5\24\13\2\u008b\u008d\7 \2\2\u008c"+
		"\u0089\3\2\2\2\u008c\u008a\3\2\2\2\u008c\u008b\3\2\2\2\u008d\u0090\3\2"+
		"\2\2\u008e\u008c\3\2\2\2\u008e\u008f\3\2\2\2\u008f\u0091\3\2\2\2\u0090"+
		"\u008e\3\2\2\2\u0091\u0093\7\b\2\2\u0092\u0088\3\2\2\2\u0092\u0093\3\2"+
		"\2\2\u0093\17\3\2\2\2\u0094\u0095\7\26\2\2\u0095\u0096\5(\25\2\u0096\21"+
		"\3\2\2\2\u0097\u0099\5\36\20\2\u0098\u0097\3\2\2\2\u0098\u0099\3\2\2\2"+
		"\u0099\u009b\3\2\2\2\u009a\u009c\5*\26\2\u009b\u009a\3\2\2\2\u009b\u009c"+
		"\3\2\2\2\u009c\u009e\3\2\2\2\u009d\u009f\5.\30\2\u009e\u009d\3\2\2\2\u009e"+
		"\u009f\3\2\2\2\u009f\u00a0\3\2\2\2\u00a0\u00a1\5(\25\2\u00a1\u00a2\7 "+
		"\2\2\u00a2\23\3\2\2\2\u00a3\u00a5\5\36\20\2\u00a4\u00a3\3\2\2\2\u00a4"+
		"\u00a5\3\2\2\2\u00a5\u00a7\3\2\2\2\u00a6\u00a8\5*\26\2\u00a7\u00a6\3\2"+
		"\2\2\u00a7\u00a8\3\2\2\2\u00a8\u00aa\3\2\2\2\u00a9\u00ab\5.\30\2\u00aa"+
		"\u00a9\3\2\2\2\u00aa\u00ab\3\2\2\2\u00ab\u00ac\3\2\2\2\u00ac\u00ad\5("+
		"\25\2\u00ad\u00af\7\3\2\2\u00ae\u00b0\5\"\22\2\u00af\u00ae\3\2\2\2\u00af"+
		"\u00b0\3\2\2\2\u00b0\u00b1\3\2\2\2\u00b1\u00b2\7\4\2\2\u00b2\u00b3\7 "+
		"\2\2\u00b3\25\3\2\2\2\u00b4\u00bc\5\32\16\2\u00b5\u00b6\7\t\2\2\u00b6"+
		"\u00b8\5(\25\2\u00b7\u00b9\7\34\2\2\u00b8\u00b7\3\2\2\2\u00b8\u00b9\3"+
		"\2\2\2\u00b9\u00ba\3\2\2\2\u00ba\u00bb\7\t\2\2\u00bb\u00bd\3\2\2\2\u00bc"+
		"\u00b5\3\2\2\2\u00bc\u00bd\3\2\2\2\u00bd\27\3\2\2\2\u00be\u00bf\7\t\2"+
		"\2\u00bf\u00c1\5(\25\2\u00c0\u00c2\7\34\2\2\u00c1\u00c0\3\2\2\2\u00c1"+
		"\u00c2\3\2\2\2\u00c2\u00c3\3\2\2\2\u00c3\u00c4\7\t\2\2\u00c4\u00c6\3\2"+
		"\2\2\u00c5\u00be\3\2\2\2\u00c5\u00c6\3\2\2\2\u00c6\u00c7\3\2\2\2\u00c7"+
		"\u00c8\5\32\16\2\u00c8\31\3\2\2\2\u00c9\u00ca\5(\25\2\u00ca\33\3\2\2\2"+
		"\u00cb\u00cc\5\26\f\2\u00cc\u00cd\t\3\2\2\u00cd\u00d0\5\30\r\2\u00ce\u00cf"+
		"\7\n\2\2\u00cf\u00d1\5,\27\2\u00d0\u00ce\3\2\2\2\u00d0\u00d1\3\2\2\2\u00d1"+
		"\u00d2\3\2\2\2\u00d2\u00d3\7 \2\2\u00d3\35\3\2\2\2\u00d4\u00d9\7\35\2"+
		"\2\u00d5\u00d9\7\36\2\2\u00d6\u00d9\7\13\2\2\u00d7\u00d9\7\r\2\2\u00d8"+
		"\u00d4\3\2\2\2\u00d8\u00d5\3\2\2\2\u00d8\u00d6\3\2\2\2\u00d8\u00d7\3\2"+
		"\2\2\u00d9\37\3\2\2\2\u00da\u00dc\5.\30\2\u00db\u00da\3\2\2\2\u00db\u00dc"+
		"\3\2\2\2\u00dc\u00dd\3\2\2\2\u00dd\u00de\5(\25\2\u00de!\3\2\2\2\u00df"+
		"\u00e4\5 \21\2\u00e0\u00e1\7\f\2\2\u00e1\u00e3\5 \21\2\u00e2\u00e0\3\2"+
		"\2\2\u00e3\u00e6\3\2\2\2\u00e4\u00e2\3\2\2\2\u00e4\u00e5\3\2\2\2\u00e5"+
		"#\3\2\2\2\u00e6\u00e4\3\2\2\2\u00e7\u00e8\5.\30\2\u00e8%\3\2\2\2\u00e9"+
		"\u00ee\5$\23\2\u00ea\u00eb\7\f\2\2\u00eb\u00ed\5$\23\2\u00ec\u00ea\3\2"+
		"\2\2\u00ed\u00f0\3\2\2\2\u00ee\u00ec\3\2\2\2\u00ee\u00ef\3\2\2\2\u00ef"+
		"\'\3\2\2\2\u00f0\u00ee\3\2\2\2\u00f1\u00f2\t\4\2\2\u00f2)\3\2\2\2\u00f3"+
		"\u00f4\t\5\2\2\u00f4+\3\2\2\2\u00f5\u00f6\7\20\2\2\u00f6\u00fb\5(\25\2"+
		"\u00f7\u00f8\7\3\2\2\u00f8\u00f9\5(\25\2\u00f9\u00fa\7\4\2\2\u00fa\u00fc"+
		"\3\2\2\2\u00fb\u00f7\3\2\2\2\u00fb\u00fc\3\2\2\2\u00fc\u00fd\3\2\2\2\u00fd"+
		"\u00fe\7\21\2\2\u00fe-\3\2\2\2\u00ff\u0100\5(\25\2\u0100\u0102\7\22\2"+
		"\2\u0101\u0103\5&\24\2\u0102\u0101\3\2\2\2\u0102\u0103\3\2\2\2\u0103\u0104"+
		"\3\2\2\2\u0104\u0105\7\23\2\2\u0105\u010c\3\2\2\2\u0106\u0107\5(\25\2"+
		"\u0107\u0108\7\5\2\2\u0108\u0109\7\6\2\2\u0109\u010c\3\2\2\2\u010a\u010c"+
		"\5(\25\2\u010b\u00ff\3\2\2\2\u010b\u0106\3\2\2\2\u010b\u010a\3\2\2\2\u010c"+
		"/\3\2\2\2\u010d\u010f\7\32\2\2\u010e\u0110\7\31\2\2\u010f\u010e\3\2\2"+
		"\2\u010f\u0110\3\2\2\2\u0110\u0117\3\2\2\2\u0111\u0117\7\31\2\2\u0112"+
		"\u0114\7\25\2\2\u0113\u0115\7\31\2\2\u0114\u0113\3\2\2\2\u0114\u0115\3"+
		"\2\2\2\u0115\u0117\3\2\2\2\u0116\u010d\3\2\2\2\u0116\u0111\3\2\2\2\u0116"+
		"\u0112\3\2\2\2\u0117\61\3\2\2\2\u0118\u0119\5(\25\2\u0119\u011a\7 \2\2"+
		"\u011a\u011c\3\2\2\2\u011b\u0118\3\2\2\2\u011c\u011d\3\2\2\2\u011d\u011b"+
		"\3\2\2\2\u011d\u011e\3\2\2\2\u011e\63\3\2\2\2\u011f\u0120\7\27\2\2\u0120"+
		"\u0127\5(\25\2\u0121\u0122\7\7\2\2\u0122\u0124\7 \2\2\u0123\u0125\5\62"+
		"\32\2\u0124\u0123\3\2\2\2\u0124\u0125\3\2\2\2\u0125\u0126\3\2\2\2\u0126"+
		"\u0128\7\b\2\2\u0127\u0121\3\2\2\2\u0127\u0128\3\2\2\2\u0128\65\3\2\2"+
		"\2,9?EOTW`gmrwy\177\u0082\u008c\u008e\u0092\u0098\u009b\u009e\u00a4\u00a7"+
		"\u00aa\u00af\u00b8\u00bc\u00c1\u00c5\u00d0\u00d8\u00db\u00e4\u00ee\u00fb"+
		"\u0102\u010b\u010f\u0114\u0116\u011d\u0124\u0127";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}