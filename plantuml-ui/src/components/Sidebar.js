import React from "react";
import { Link } from "react-router-dom";
import { Drawer, List, ListItem, ListItemText, Typography } from "@mui/material";

const Sidebar = () => {
  return (
    <Drawer
      variant="permanent"
      anchor="left"
      sx={{
        width: 240,
        "& .MuiDrawer-paper": { width: 240, boxSizing: "border-box" },
      }}
    >
      <Typography variant="h6" sx={{ padding: "1rem" }}>
        Menu
      </Typography>
      <List>
        <ListItem button component={Link} to="/">
          <ListItemText primary="Visualize and Import into Visual Paradigm" />
        </ListItem>
        <ListItem button component={Link} to="/export">
          <ListItemText primary="Export from Visual Paradigm" />
        </ListItem>
      </List>
    </Drawer>
  );
};

export default Sidebar;
