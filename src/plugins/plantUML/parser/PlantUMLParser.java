package plugins.plantUML.parser;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import net.sourceforge.plantuml.syntax.SyntaxChecker;
import net.sourceforge.plantuml.syntax.SyntaxResult;

public class PlantUMLParser {
    private File file;

    public PlantUMLParser(File file) {
        this.file = file;
    }

    public SyntaxResult parse() throws IOException {
        List<String> lines = Files.readAllLines(file.toPath());
        // checks if valid syntax
        return SyntaxChecker.checkSyntax(lines);
    }
}
