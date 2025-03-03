# **Visual Paradigm Plugin for PlantUML Import and Export**

## **Supported Diagram Types**

The plugin currently supports conversion to and from:

- Class Diagram
- Use Case Diagram
- Sequence Diagram
- Component Diagram
- Deployment Diagram
- State (Machine) Diagram
- Activity Diagram (new syntax)

---

## **Installation Guide**


### **Automatic Installation**
1. **Download the `.zip` release** of the plugin.
2. **Open Visual Paradigm** and navigate to the **Help** tab.
3. Select **Install Plugin**.
4. Choose **Install from a zip of plugin** and click **Next**.
5. Browse to the downloaded `.zip` file and select it.
6. **Restart Visual Paradigm** to complete the installation.

### **Manual Installation**
1. Extract the plugin folder from the `.zip` file.
2. Copy the folder into the `/plugins` directory specified by the **Install Plugin** dialog.
3. **Restart Visual Paradigm**.

---

## **PlantUML Plugin CLI**


The CLI is packaged with the plugin and does not require a separate installation.

### To allow usage:
1. Locate the script file (`Plugin.bat` / `Plugin.sh`) in:
    ```
    VP_installation_dir/scripts
    ```
2. Copy the file to the binaries folder:
    ```
    VP_installation_dir/bin
    ```

### **Example 1: Exporting diagrams**
Export all diagrams under project:
```bash
Plugin.bat -project "C:/Demo/demo_project.vpp" -pluginid "plugins.plantUML" -pluginargs -action "export" -path "C:/Demo/output" -target "all"
```

List all diagrams and ids and export specific target:
```bash
Plugin.bat -project "C:/Demo/demo_project.vpp" -pluginid "plugins.plantUML" -pluginargs -action "export" -list
```

```bash
Plugin.bat -project "C:/Demo/demo_project.vpp" -pluginid "plugins.plantUML" -pluginargs -action "export" -path "C:/Demo/output" -target "lAJWSCmGAqACKRNG"
```

### **Example 2: Importing a folder of diagrams**
```bash
Plugin.bat -project "C:/Demo/demo_project.vpp" -pluginid "plugins.plantUML" -pluginargs -action "import" -path "C:/Demo/plant_diagrams"
```
