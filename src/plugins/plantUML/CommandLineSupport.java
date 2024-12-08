package plugins.plantUML;

import java.io.File;

import com.vp.plugin.VPPluginCommandLineSupport;

import plugins.plantUML.imports.importers.DiagramImportPipeline;

public class CommandLineSupport implements VPPluginCommandLineSupport {

    @Override
    public void invoke(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: -action <import|export> -path <file_or_folder_path>");
            return;
        }

        String action = null;
        String path = null;

        // Parse arguments
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-action":
                    if (i + 1 < args.length) {
                        action = args[++i];
                    } else {
                        System.out.println("Error: Missing value for -action");
                        return;
                    }
                    break;

                case "-path":
                    if (i + 1 < args.length) {
                        path = args[++i];
                    } else {
                        System.out.println("Error: Missing value for -path");
                        return;
                    }
                    break;

                default:
                    System.out.println("Unknown argument: " + args[i]);
            }
        }

        if (action == null || path == null) {
            System.out.println("Error: Missing required arguments.");
            return;
        }

        // Perform action
        switch (action.toLowerCase()) {
            case "import":
                performImport(path);
                break;

            case "export":
                performExport(path);
                break;

            default:
                System.out.println("Error: Invalid action specified. Use 'import' or 'export'.");
        }
    }

    private void performImport(String path) {
        System.out.println("Importing from path: " + path);
        File file = new File(path);
        DiagramImportPipeline pipeline = new DiagramImportPipeline();
        pipeline.importFromSource(file);
    }

    private void performExport(String path) {
        // Implement your export logic here
        System.out.println("Exporting to path: " + path);
        // TODO: Add actual export functionality
    }
}
