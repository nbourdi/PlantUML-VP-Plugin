import React from "react";
import { useDropzone } from "react-dropzone";

const FileDropzone = ({ onFileUpload }) => {
  const onDrop = (acceptedFiles) => {
    if (acceptedFiles.length > 0) {
      const file = acceptedFiles[0];
      const reader = new FileReader();
      reader.onload = () => {
        onFileUpload(reader.result);
      };
      reader.readAsText(file);
    }
  };

  const { getRootProps, getInputProps } = useDropzone({ onDrop });

  return (
    <div
      {...getRootProps()}
      style={{
        border: "2px dashed #ccc",
        padding: "1rem",
        textAlign: "center",
        cursor: "pointer",
      }}
    >
      <input {...getInputProps()} />
      <p>Drag & drop a file here, or click to select a file</p>
    </div>
  );
};

export default FileDropzone;
