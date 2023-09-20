// Arav Popli
// Period 3
// Text Excel Project

import java.io.*;
import java.util.*;

/*
 * This class, Main, is the main implementation of the TextExcel program. It
 * implements the requirements established by ExcelBase. Most methods will be
 * private as the only public methods necessary are to processCommand(). This
 * class will handle certain commands that the Grid does not: help and load
 * file.
 */
public class Main extends ExcelBase {

    public static void main(String args[]) {

        // Create our Grid object and assign it to the GridBase
        // static field so that we can reference it later on.
        GridBase.grid = new Grid();
        Main engine = new Main();

        engine.runInputLoop();
    }

    /*
     * This method will parse a line that contains a command. It will delegate the
     * command to the Grid if the Grid should handle it. It will call the
     * appropriate method to handle the command. This method prints NOTHING!
     *
     * ALL Commands should be distributed to an appropriate method. Here are the
     * Checkpoint #1 commands:
     * help : provides help to the user on how to use Text Excel
     * print : returns a string of the printed grid. The grid does this for itself!
     * rows : returns the number of rows currently in the grid. The grid knows this
     * info.
     * cols : returns the number of columns currently in the grid
     * width : returns the width of an individual cell that is used when
     * displaying the grid contents.
     * rows = 5 : resizes the grid to have 5 rows. The grid contents will be
     * cleared.
     * cols = 3 : resizes the grid to have 3 columns. The grid contents will be
     * cleared.
     * width = 6 : resizes the width of a cell to be 6 characters wide
     * when printing the grid.
     * load file1.txt : opens the file specified and processes all commands in it.
     * 
     * Parameters:
     * command : The command to be processed (described above)
     * Returns:
     * The result of the command which will be printed by the infrastructure.
     */
    public String processCommand(String command) {
        String result = null;

        result = helpCommand(command);

        if (command.startsWith("load")) {
            result = this.loadFromFile(command.substring(command.indexOf(" ") + 1));
        }
        // Dispatch the command to the Grid object to see if it can handle it.
        if (result == null && GridBase.grid != null) {
            // The GridBase class has a static field, grid, of type GridBase.
            // Ask the grid object to process the command.
            result = GridBase.grid.processCommand(command);
        }

        // the command is still not handled
        if (result == null)
            result = "Unhandled";

        return result;
    }

    // This method provides the user with help commands to operate the spreadsheet
    private String helpCommand(String command) {
      String help = null;
      if(command.equals("help")) {
         help = "Type 'rows' to get the number of rows in the grid\n";
         help += "Type rows = (#) to change the number of rows in the grid\n";
         help += "You may use the same format to change col and cell width or\n";
         help += "Access col and cell width. Type 'print' in order to print the grid\n";
         help += "To set cell values do (ex. \"a1 = 5\"\n";
         help += "You may set a cell to an expression, string using \"\", set it a number\n";
         help += "Or an expression using parenthesees Ex. a1 = ( b1 )\n";
         help += "You may also do operations on cells such as a1 = ( b1 + 1 ) or a1 = ( 1 + 1 )\n";
         help += "You may also clear an also individual cell by typing in the console Ex. \"clear a1\"\n";
         help += "You can find the sum and averages over a range of cells\n";
         help += "by typing (cellcode (ex.a1)) = ( sum (cellcode) - (cellcode) )\n";
         help += "You may also find averages using (cellcode (ex.a1)) = ( avg (cellcode) - (cellcode) )\n";
         help += "You may also sort number cells in ascending and descending order using \"sorta\"\n";
         help += "For ascending and \"sortd\" for descending sorting over an interval using the format\n";
         help += "((sorta/sortd) (cellcode) - (cellcode)\n";
      }
      return help;
    }

    /*
     * Method loadFromFile.
     *
     * This will process the command: load {filename}
     *
     * Call processCommand() for every line in the file. During file processing,
     * there should be no output in the final implementation.
     * 
     * Parameter:
     * filename : The name/path to the file
     * Returns:
     * true/false: True if the file was found and the commands in the file
     * were processed by processCommand.
     */
    private String loadFromFile(String filename) {
        String result = "Load command not yet implemented";
        File file = null;
        try {
            file = new File(filename);
            Scanner reader = new Scanner(file);
            while (reader.hasNextLine()) {
                // Passes all lines from file loaded into process
                // command to be read and change the grid 
                GridBase.grid.processCommand(reader.nextLine());
            }
            // If successful, set the result to say that
            result = "File loaded successfully";
        } catch (FileNotFoundException e) {
            result = "Could not find file: " + file.getAbsolutePath();
        }
        return result;
    }
}
