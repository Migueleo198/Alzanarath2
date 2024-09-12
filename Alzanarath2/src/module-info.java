module Alzanarath2 {
    // Define the modules you need to use here, for example:
    requires java.sql; // if your project uses JDBC for database access
    requires java.desktop;
    // Define packages you want to expose to other modules:
    exports main; // replace with your actual package names
   
    exports Database;
    exports Entity;
      
    exports Inputs;
  
    exports Monster;
  
  
    exports Networking;
   
    exports Tile;
   
    exports UI;
    
}