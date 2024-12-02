import React from "react";
import Editor from "./Editor";
import FileDropzone from "./FileDropzone";
import DiagramViewer from "./DiagramViewer";
import { Box, Button, Typography } from "@mui/material";

const VisualizeImport = ({ plantUmlText, setPlantUmlText, generateDiagram, imageSrc, error }) => {
  return (
    <Box sx={{ padding: "2rem", paddingLeft: "130px" }}> {/* Adjust paddingLeft */}
      <Typography variant="h4" gutterBottom>
        Visualize and Import into Visual Paradigm
      </Typography>

      <Box sx={{ marginBottom: "1.5rem" }}>
        <Editor value={plantUmlText} onChange={setPlantUmlText} />
      </Box>

      <Box sx={{ marginBottom: "1.5rem" }}>
        <FileDropzone onFileUpload={setPlantUmlText} />
      </Box>

      <Button 
        variant="contained" 
        color="primary" 
        onClick={generateDiagram} 
        sx={{ marginBottom: "1.5rem" }}
      >
        Generate Diagram
      </Button>

      {error && (
        <Typography variant="body1" color="error" sx={{ marginBottom: "1.5rem" }}>
          {error}
        </Typography>
      )}

      <DiagramViewer imageSrc={imageSrc} />
    </Box>
  );
};

export default VisualizeImport;
