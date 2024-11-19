package plugins.plantUML.parser.antlr;

import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.tree.*;

public class PlantUMLParserTest {
    public static void main(String[] args) {
        String plantUMLSource = "@startuml\n"
                + "class Class1 \n"
                + "Class1 --> Class2 : Association\n"
                + "@enduml\n"; // Ensure newline at the end

        // Create a lexer and parser
        PlantUMLGrammarLexer lexer = new PlantUMLGrammarLexer(CharStreams.fromString(plantUMLSource));
        CommonTokenStream tokens = new CommonTokenStream(lexer);

        // Debugging: Print all tokens
        tokens.fill();
        for (Token token : tokens.getTokens()) {
            System.out.println("Token: " + token.getText());
        }

        PlantUMLGrammarParser parser = new PlantUMLGrammarParser(tokens);

        // Enable debugging for the parser
        parser.addErrorListener(new DiagnosticErrorListener());
        parser.setTrace(true);

        // Parse the input
        ParseTree tree = parser.diagram();

        // Print the parse tree
        System.out.println("Parse Tree: " + tree.toStringTree(parser));
    }
}
