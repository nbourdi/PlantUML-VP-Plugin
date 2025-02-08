# A Visual Paradigm plugin for PlantUML import and export

## Supported Diagram Types

The plugin currently supports conversion from and to:

- Class Diagram
- Use Case Diagram
- Sequence Diagram
- Component Diagram
- Deployment Diagram
- State (Machine) diagram
- Activity Diagram (new syntax only)

## Installation Guide

To install the plugin into Visual Paradigm:

- Download the `.zip` release
- Launch Visual Paradigm and go to the **Help** tab
- Click **Install Plugin**

- Select **Install from a zip of plugin** and click **Next**
- Choose the downloaded `.zip` folder
- Restart Visual Paradigm

Alternatively, you can manually copy the folder into the `/plugins` directory indicated by the **Install Plugin** dialog and restart the application.

## Usage Guide

After successful installation, both import and export functionalities should be available in Visual Paradigm's **Project** tab under **Import** and **Export**, extending the application's built-in conversion options.

### Exporting Visual Paradigm Diagrams

1. Navigate to the **Project** > **Export** menu and select **"PlantUML..."**.
2. A dialog will pop up prompting you to choose which diagrams to convert into PlantUML files.
3. Specify the desired output folder.
4. Click **"Export"** to convert.

For each selected diagram, the plugin should generate:
- A `.txt` file containing the PlantUML code for the diagram.
- A complementary `json` (in PlantUML format) that contains any extra semantics, if applicable.

// photo

### Importing PlantUML files

1. Navigate to the **Project** > **Import** menu and select **"PlantUML..."**.
2. A pop-up will prompt you to choose between importing a folder of diagrams or a single file.
3. Specify the desired file/folder.
4. Click **"Import"** to finish importing to your project.

## CLI
