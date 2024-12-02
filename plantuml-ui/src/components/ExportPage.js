import React from "react";
import { Box, Typography, Button, TextField } from "@mui/material";

const ExportPage = () => {
  return (
    <Box sx={{ padding: "2rem", paddingLeft: "130px" }}> {/* Adjust paddingLeft */}
      <Typography variant="h4" gutterBottom>
        Export from Visual Paradigm
      </Typography>

      <Typography variant="body1" gutterBottom>
        Use this page to export diagrams from Visual Paradigm in compatible formats.
      </Typography>

      <TextField
        label="Enter Diagram Details"
        fullWidth
        multiline
        rows={4}
        sx={{ marginBottom: "1.5rem" }}
      />

      <Button variant="contained" color="primary">
        Export
      </Button>
    </Box>
  );
};

export default ExportPage;
