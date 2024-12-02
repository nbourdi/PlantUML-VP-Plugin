import React, { useState } from "react";
import { BrowserRouter as Router, Route, Routes } from "react-router-dom";
import Sidebar from "./components/Sidebar";
import VisualizeImport from "./components/VisualizeImport";
import ExportPage from "./components/ExportPage";
import plantumlEncoder from "plantuml-encoder";
import axios from "axios";

const App = () => {
  const [plantUmlText, setPlantUmlText] = useState("");
  const [imageSrc, setImageSrc] = useState("");
  const [error, setError] = useState("");

  const generateDiagram = async () => {
    try {
      const encodedText = plantumlEncoder.encode(plantUmlText);
      const response = await axios.get(
        `http://localhost:8080/png/${encodedText}`,
        { responseType: "arraybuffer" }
      );
      const base64Image = btoa(
        new Uint8Array(response.data).reduce((data, byte) => data + String.fromCharCode(byte), "")
      );
      setImageSrc(`data:image/png;base64,${base64Image}`);
      setError("");
    } catch (err) {
      setError("Error generating diagram. Check syntax or server connection.");
      console.error(err);
    }
  };

  return (
    <Router>
      <div style={{ display: "flex", height: "100vh" }}>
        <Sidebar />
        <Routes>
          <Route
            path="/"
            element={
              <VisualizeImport
                plantUmlText={plantUmlText}
                setPlantUmlText={setPlantUmlText}
                generateDiagram={generateDiagram}
                imageSrc={imageSrc}
                error={error}
              />
            }
          />
          <Route path="/export" element={<ExportPage />} />
        </Routes>
      </div>
    </Router>
  );
};

export default App;
