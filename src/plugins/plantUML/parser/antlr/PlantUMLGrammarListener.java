// Generated from PlantUMLGrammar.g4 by ANTLR 4.7.2
package plugins.plantUML.parser.antlr;
import org.antlr.v4.runtime.tree.ParseTreeListener;

/**
 * This interface defines a complete listener for a parse tree produced by
 * {@link PlantUMLGrammarParser}.
 */
public interface PlantUMLGrammarListener extends ParseTreeListener {
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#umlFile}.
	 * @param ctx the parse tree
	 */
	void enterUmlFile(PlantUMLGrammarParser.UmlFileContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#umlFile}.
	 * @param ctx the parse tree
	 */
	void exitUmlFile(PlantUMLGrammarParser.UmlFileContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#uml}.
	 * @param ctx the parse tree
	 */
	void enterUml(PlantUMLGrammarParser.UmlContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#uml}.
	 * @param ctx the parse tree
	 */
	void exitUml(PlantUMLGrammarParser.UmlContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#embeddedUml}.
	 * @param ctx the parse tree
	 */
	void enterEmbeddedUml(PlantUMLGrammarParser.EmbeddedUmlContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#embeddedUml}.
	 * @param ctx the parse tree
	 */
	void exitEmbeddedUml(PlantUMLGrammarParser.EmbeddedUmlContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#diagram}.
	 * @param ctx the parse tree
	 */
	void enterDiagram(PlantUMLGrammarParser.DiagramContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#diagram}.
	 * @param ctx the parse tree
	 */
	void exitDiagram(PlantUMLGrammarParser.DiagramContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#class_diagram}.
	 * @param ctx the parse tree
	 */
	void enterClass_diagram(PlantUMLGrammarParser.Class_diagramContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#class_diagram}.
	 * @param ctx the parse tree
	 */
	void exitClass_diagram(PlantUMLGrammarParser.Class_diagramContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#class_diagram_noise_line}.
	 * @param ctx the parse tree
	 */
	void enterClass_diagram_noise_line(PlantUMLGrammarParser.Class_diagram_noise_lineContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#class_diagram_noise_line}.
	 * @param ctx the parse tree
	 */
	void exitClass_diagram_noise_line(PlantUMLGrammarParser.Class_diagram_noise_lineContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#class_declaration}.
	 * @param ctx the parse tree
	 */
	void enterClass_declaration(PlantUMLGrammarParser.Class_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#class_declaration}.
	 * @param ctx the parse tree
	 */
	void exitClass_declaration(PlantUMLGrammarParser.Class_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#hide_declaration}.
	 * @param ctx the parse tree
	 */
	void enterHide_declaration(PlantUMLGrammarParser.Hide_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#hide_declaration}.
	 * @param ctx the parse tree
	 */
	void exitHide_declaration(PlantUMLGrammarParser.Hide_declarationContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#attribute}.
	 * @param ctx the parse tree
	 */
	void enterAttribute(PlantUMLGrammarParser.AttributeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#attribute}.
	 * @param ctx the parse tree
	 */
	void exitAttribute(PlantUMLGrammarParser.AttributeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#method}.
	 * @param ctx the parse tree
	 */
	void enterMethod(PlantUMLGrammarParser.MethodContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#method}.
	 * @param ctx the parse tree
	 */
	void exitMethod(PlantUMLGrammarParser.MethodContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#connection_left}.
	 * @param ctx the parse tree
	 */
	void enterConnection_left(PlantUMLGrammarParser.Connection_leftContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#connection_left}.
	 * @param ctx the parse tree
	 */
	void exitConnection_left(PlantUMLGrammarParser.Connection_leftContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#connection_right}.
	 * @param ctx the parse tree
	 */
	void enterConnection_right(PlantUMLGrammarParser.Connection_rightContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#connection_right}.
	 * @param ctx the parse tree
	 */
	void exitConnection_right(PlantUMLGrammarParser.Connection_rightContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#class_name}.
	 * @param ctx the parse tree
	 */
	void enterClass_name(PlantUMLGrammarParser.Class_nameContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#class_name}.
	 * @param ctx the parse tree
	 */
	void exitClass_name(PlantUMLGrammarParser.Class_nameContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#connection}.
	 * @param ctx the parse tree
	 */
	void enterConnection(PlantUMLGrammarParser.ConnectionContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#connection}.
	 * @param ctx the parse tree
	 */
	void exitConnection(PlantUMLGrammarParser.ConnectionContext ctx);
	/**
	 * Enter a parse tree produced by the {@code visibility_public}
	 * labeled alternative in {@link PlantUMLGrammarParser#visibility}.
	 * @param ctx the parse tree
	 */
	void enterVisibility_public(PlantUMLGrammarParser.Visibility_publicContext ctx);
	/**
	 * Exit a parse tree produced by the {@code visibility_public}
	 * labeled alternative in {@link PlantUMLGrammarParser#visibility}.
	 * @param ctx the parse tree
	 */
	void exitVisibility_public(PlantUMLGrammarParser.Visibility_publicContext ctx);
	/**
	 * Enter a parse tree produced by the {@code visibility_private}
	 * labeled alternative in {@link PlantUMLGrammarParser#visibility}.
	 * @param ctx the parse tree
	 */
	void enterVisibility_private(PlantUMLGrammarParser.Visibility_privateContext ctx);
	/**
	 * Exit a parse tree produced by the {@code visibility_private}
	 * labeled alternative in {@link PlantUMLGrammarParser#visibility}.
	 * @param ctx the parse tree
	 */
	void exitVisibility_private(PlantUMLGrammarParser.Visibility_privateContext ctx);
	/**
	 * Enter a parse tree produced by the {@code visibility_protected}
	 * labeled alternative in {@link PlantUMLGrammarParser#visibility}.
	 * @param ctx the parse tree
	 */
	void enterVisibility_protected(PlantUMLGrammarParser.Visibility_protectedContext ctx);
	/**
	 * Exit a parse tree produced by the {@code visibility_protected}
	 * labeled alternative in {@link PlantUMLGrammarParser#visibility}.
	 * @param ctx the parse tree
	 */
	void exitVisibility_protected(PlantUMLGrammarParser.Visibility_protectedContext ctx);
	/**
	 * Enter a parse tree produced by the {@code visibility_package}
	 * labeled alternative in {@link PlantUMLGrammarParser#visibility}.
	 * @param ctx the parse tree
	 */
	void enterVisibility_package(PlantUMLGrammarParser.Visibility_packageContext ctx);
	/**
	 * Exit a parse tree produced by the {@code visibility_package}
	 * labeled alternative in {@link PlantUMLGrammarParser#visibility}.
	 * @param ctx the parse tree
	 */
	void exitVisibility_package(PlantUMLGrammarParser.Visibility_packageContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#function_argument}.
	 * @param ctx the parse tree
	 */
	void enterFunction_argument(PlantUMLGrammarParser.Function_argumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#function_argument}.
	 * @param ctx the parse tree
	 */
	void exitFunction_argument(PlantUMLGrammarParser.Function_argumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#function_argument_list}.
	 * @param ctx the parse tree
	 */
	void enterFunction_argument_list(PlantUMLGrammarParser.Function_argument_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#function_argument_list}.
	 * @param ctx the parse tree
	 */
	void exitFunction_argument_list(PlantUMLGrammarParser.Function_argument_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#template_argument}.
	 * @param ctx the parse tree
	 */
	void enterTemplate_argument(PlantUMLGrammarParser.Template_argumentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#template_argument}.
	 * @param ctx the parse tree
	 */
	void exitTemplate_argument(PlantUMLGrammarParser.Template_argumentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#template_argument_list}.
	 * @param ctx the parse tree
	 */
	void enterTemplate_argument_list(PlantUMLGrammarParser.Template_argument_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#template_argument_list}.
	 * @param ctx the parse tree
	 */
	void exitTemplate_argument_list(PlantUMLGrammarParser.Template_argument_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#ident}.
	 * @param ctx the parse tree
	 */
	void enterIdent(PlantUMLGrammarParser.IdentContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#ident}.
	 * @param ctx the parse tree
	 */
	void exitIdent(PlantUMLGrammarParser.IdentContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#modifiers}.
	 * @param ctx the parse tree
	 */
	void enterModifiers(PlantUMLGrammarParser.ModifiersContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#modifiers}.
	 * @param ctx the parse tree
	 */
	void exitModifiers(PlantUMLGrammarParser.ModifiersContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#stereotype}.
	 * @param ctx the parse tree
	 */
	void enterStereotype(PlantUMLGrammarParser.StereotypeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#stereotype}.
	 * @param ctx the parse tree
	 */
	void exitStereotype(PlantUMLGrammarParser.StereotypeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code template_type}
	 * labeled alternative in {@link PlantUMLGrammarParser#type_declaration}.
	 * @param ctx the parse tree
	 */
	void enterTemplate_type(PlantUMLGrammarParser.Template_typeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code template_type}
	 * labeled alternative in {@link PlantUMLGrammarParser#type_declaration}.
	 * @param ctx the parse tree
	 */
	void exitTemplate_type(PlantUMLGrammarParser.Template_typeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code list_type}
	 * labeled alternative in {@link PlantUMLGrammarParser#type_declaration}.
	 * @param ctx the parse tree
	 */
	void enterList_type(PlantUMLGrammarParser.List_typeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code list_type}
	 * labeled alternative in {@link PlantUMLGrammarParser#type_declaration}.
	 * @param ctx the parse tree
	 */
	void exitList_type(PlantUMLGrammarParser.List_typeContext ctx);
	/**
	 * Enter a parse tree produced by the {@code simple_type}
	 * labeled alternative in {@link PlantUMLGrammarParser#type_declaration}.
	 * @param ctx the parse tree
	 */
	void enterSimple_type(PlantUMLGrammarParser.Simple_typeContext ctx);
	/**
	 * Exit a parse tree produced by the {@code simple_type}
	 * labeled alternative in {@link PlantUMLGrammarParser#type_declaration}.
	 * @param ctx the parse tree
	 */
	void exitSimple_type(PlantUMLGrammarParser.Simple_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#class_type}.
	 * @param ctx the parse tree
	 */
	void enterClass_type(PlantUMLGrammarParser.Class_typeContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#class_type}.
	 * @param ctx the parse tree
	 */
	void exitClass_type(PlantUMLGrammarParser.Class_typeContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#item_list}.
	 * @param ctx the parse tree
	 */
	void enterItem_list(PlantUMLGrammarParser.Item_listContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#item_list}.
	 * @param ctx the parse tree
	 */
	void exitItem_list(PlantUMLGrammarParser.Item_listContext ctx);
	/**
	 * Enter a parse tree produced by {@link PlantUMLGrammarParser#enum_declaration}.
	 * @param ctx the parse tree
	 */
	void enterEnum_declaration(PlantUMLGrammarParser.Enum_declarationContext ctx);
	/**
	 * Exit a parse tree produced by {@link PlantUMLGrammarParser#enum_declaration}.
	 * @param ctx the parse tree
	 */
	void exitEnum_declaration(PlantUMLGrammarParser.Enum_declarationContext ctx);
}