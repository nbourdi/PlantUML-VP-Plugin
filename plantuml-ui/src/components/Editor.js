import React from "react";
import CodeMirror from "@uiw/react-codemirror";
import { javascript } from "@codemirror/lang-javascript";
import { markdown } from "@codemirror/lang-markdown";

const Editor = ({ value, onChange }) => {
  return (
    <CodeMirror
      value={value}
      extensions={[markdown()]} // Use `javascript()` or any other syntax highlighting as needed
      onChange={(value) => onChange(value)}
      theme="light"
      height="200px"
    />
  );
};

export default Editor;
