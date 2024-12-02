import React from "react";

const DiagramViewer = ({ imageSrc }) => {
  return (
    <div style={{ textAlign: "center" }}>
      {imageSrc ? (
        <img
          src={imageSrc}
          alt="Generated Diagram"
          style={{ maxWidth: "100%", maxHeight: "100%" }}
        />
      ) : (
        <p>No diagram to display</p>
      )}
    </div>
  );
};

export default DiagramViewer;
