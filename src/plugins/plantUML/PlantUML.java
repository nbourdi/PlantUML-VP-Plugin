package plugins.plantUML;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.vp.plugin.*;

import com.vp.plugin.diagram.IDiagramUIModel;
import plugins.plantUML.export.DiagramExportPipeline;
import plugins.plantUML.imports.importers.DiagramImportPipeline;

public class PlantUML implements VPPlugin, VPPluginCommandLineSupport {

    @Override
    public void loaded(VPPluginInfo pluginInfo) {
    }

    @Override
    public void unloaded() {
    }

    @Override
    public void invoke(String[] args) {
        if (args == null || args.length < 2) {
            System.out.println("Usage: -action <import|export> -path <file_or_folder_path>");
            return;
        }

        String action = null;
        String path = null;
        String target = null;
        boolean listDiagrams = false;

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

                case "-target":
                    if (i + 1 < args.length) {
                        target = args[++i];
                    } else {
                        System.out.println("Error: Missing value for -target");
                        return;
                    }
                    break;

                case "-list":
                    listDiagrams = true;
                    break;

                default:
                    System.out.println("Unknown argument: " + args[i]);
            }
        }

        if (action == null) {
            System.out.println("Error: Missing required argument -action.");
            return;
        }

        switch (action.toLowerCase()) {
            case "import":
                if (path == null) {
                    System.out.println("Error: Missing required argument -path for import.");
                    return;
                }
                performImport(path);
                break;

            case "export":
                if (listDiagrams) {
                    listAvailableDiagrams();
                    return;
                }

                if (target == null || path == null) {
                    System.out.println("Error: Missing required arguments for export. Use -target and -path.");
                    return;
                }

                performExport(target, path);
                break;

            default:
                System.out.println("Error: Invalid action specified. Use 'import' or 'export'.");
        }
    }

    private void performImport(String path) {
        System.out.println("Importing from path: " + path);
        File file = new File(path);
        DiagramImportPipeline pipeline = new DiagramImportPipeline();

        if (file.isDirectory()) {
            File[] files = file.listFiles((dir, name) ->
                    name.toLowerCase().endsWith(".txt") ||
                            name.toLowerCase().endsWith(".puml") ||
                            name.toLowerCase().endsWith(".plantuml")
            );

            if (files != null && files.length > 0) {
                List<File> fileList = Arrays.asList(files);
                pipeline.importMultipleFiles(fileList);
            } else {
                System.out.println("Error: No valid .txt, .puml, or .plantuml files found in directory.");
            }
        } else {
            if (file.getName().toLowerCase().endsWith(".txt") ||
                    file.getName().toLowerCase().endsWith(".puml") ||
                    file.getName().toLowerCase().endsWith(".plantuml")) {
                pipeline.importFromSource(file);
            } else {
                System.out.println("Error: Unsupported file type. Only .txt, .puml, and .plantuml are allowed.");
            }
        }
        ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
        projectManager.saveProject();
    }

    private void performExport(String target, String path) {
        File exportLocation = new File(path);

        // Check if the given path exists and is a directory
        if (!exportLocation.exists()) {
            boolean created = exportLocation.mkdirs();
            if (!created) {
                System.out.println("Error: Could not create the specified directory.");
                return;
            }
        }


        if (!exportLocation.isDirectory()) {
            System.out.println("Error: The specified path is not a directory.");
            return;
        }

        System.out.println("Exporting diagram(s): " + target + " to path: " + path);
        DiagramExportPipeline pipeline = new DiagramExportPipeline(exportLocation);

        if (target.equalsIgnoreCase("all")) {
            pipeline.exportAllDiagrams();
        } else {
            try {
                pipeline.exportSpecificDiagram(target);
            } catch (IOException e) {
                System.out.println("IO Error: Couldn't create file.");
            }
        }
    }


    private void listAvailableDiagrams() {
        System.out.println("Listing available diagrams in the project:");
        ProjectManager projectManager = ApplicationManager.instance().getProjectManager();
        IDiagramUIModel[] allDiagrams = projectManager.getProject().toDiagramArray();
        for (IDiagramUIModel diagram : allDiagrams) {
            System.out.println(diagram.getName() +" | id: " + diagram.getId());
        }
    }
}
