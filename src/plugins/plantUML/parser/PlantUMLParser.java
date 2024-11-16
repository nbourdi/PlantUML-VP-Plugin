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
        // Read all lines from the file
        List<String> lines = Files.readAllLines(file.toPath());

        // Check syntax using PlantUML SyntaxChecker
        return SyntaxChecker.checkSyntax(lines);
    }
}
