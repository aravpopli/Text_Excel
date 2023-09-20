// Arav Popli
// Period 3
// Text Excel Project

import java.io.*;
import java.util.*;
import java.text.*;

/*
 * The Grid class will hold all the cells. It allows access to the cells via the
 * public methods. It will create a display String for the whole grid and process
 * many commands that update the cells. These command will include
 * sorting a range of cells and saving the grid contents to a file.
 */
public class Grid extends GridBase {

  private int colCount = 7;
  private int rowCount = 10;
  private int cellWidth = 9;
  private Cell[][] matrix;

  public Grid() {
    fillMatrix();
  }

  // This method processes a user command.
  // Parameters: command : The command to be processed.
  // Returns : The results of the command as a string to
  // be printed by the infrastructure.
  public String processCommand(String command) {
    String result = null;
    if (result == null) {
      result = sizeCommands(command);
    }
    if (result == null) {
      result = setCell(command);
    }
    if (result == null) {
      result = getCell(command);
    }
    if (command.equalsIgnoreCase("print")) {
      result = printMatrix();
    }
    if (command.startsWith("clear")) {
      // if the clear command contains a space that means
      // they're clearing a cell
      if (command.contains(" ")) {
        clearCell(command);
      } else {
        fillMatrix();
      }
      result = "cleared";
    }
    if (command.startsWith("save")) {
      result = saveGrid(command);
    }
    if (command.contains("sorta") || command.contains("sortd")) {
      result = processSort(command);
    }

    // if result equals null after being processed, it was an invalid command
    if (result == null) {
      result = "unknown or malformed command: " + command;
    }

    return result;
  }

  // This method will sort the grid over a certain range given by
  // the user, it will either sort descending if the user types sortd
  // or ascending if the user types sorta
  private String processSort(String command) {
    Scanner parser = new Scanner(command);
    parser.next();
    // parsing user command to find the cell codes
    String code1 = parser.next();
    parser.next();
    String code2 = parser.next();
    // length and width define the dimensions region to be sorted
    int length = Integer.parseInt(code1.substring(1)) - Integer.parseInt(code2.substring(1));
    length = Math.abs(length) + 1;
    int width = Math.abs((code1.charAt(0) - 96) - (code2.charAt(0) - 96)) + 1;
    // Create an a array of the wrapper class Double so I can use
    // reverse sort on the array, reverse does not work on primitive "double"
    Double[] cellNumbers = new Double[length * width];
    return sortRange(command, code1, code2, cellNumbers);
  }

  // This method sorts a range of cells from least to greatest or greatest to
  // least over a range of cells given by the user
  private String sortRange(String command, String code1, String code2, Double[] cellNumbers) {
    int rowMin = Integer.parseInt(code1.substring(1)) - 1;
    int rowMax = Integer.parseInt(code2.substring(1)) - 1;
    int index = 0;
    for (int row = rowMin; row <= rowMax; row++) {
      for (char col = code1.charAt(0); col <= code2.charAt(0); col++) {
        // Collecting the values of all cells inside the range to be sorted
        // so it can be rearranged and sorted
        cellNumbers[index] = this.matrix[row][col - 97].getValue();
        index++;
      }
    }

    Arrays.sort(cellNumbers);
    // Checking if the command requestion "sortd" in order to sort the range
    // backwards
    if (command.contains("sortd")) {
      Arrays.sort(cellNumbers, Collections.reverseOrder());
    }

    index = 0;
    // Copied sorted cells back to matrix
    for (int row = rowMin; row <= rowMax; row++) {
      for (char col = code1.charAt(0); col <= code2.charAt(0); col++) {
        setCell("" + col + "" + (row + 1) + " = " + (cellNumbers[index]));
        index++;
      }
    }
    return "";
  }

  // This methods writes the properties of the grid to a file so
  // that it can be loaded in using the load command
  private String saveGrid(String save) {
    try {
      File file = new File(save.substring(save.indexOf(" ") + 1));
      PrintStream writer = new PrintStream(file);
      // It gathers rows, columns, and cell width along with what is stored in each
      // cell
      writer.println("rows = " + this.rowCount);
      writer.println("cols = " + this.colCount);
      writer.println("width = " + this.cellWidth);
      for (int row = 0; row < matrix.length; row++) {
        for (int col = 0; col < matrix[row].length; col++) {
          if (!((this.matrix[row][col].getExpression()).equals(""))) {
            // Cast to ascii correspondent of row, and index the
            // row number to row + 1 because the array is 0 indexed but cells are not
            String cellExpression = this.matrix[row][col].getExpression();
            writer.println(((char) (col + 97)) + "" + (row + 1) + " = " + cellExpression);
          }
        }
      }
      return "File Saved Successfully";
    } catch (FileNotFoundException ex) {
      return null;
    }
  }

  // This method checks if the user is attempting to
  // access the expression at the cell or display/value at a cell
  private String getCell(String command) {
    // parses the command to just hold cell code ex. token = "a1"
    String token = command.substring(command.indexOf(" ") + 1);
    int[] cellVal = storeCell(token);
    // Checks if the cellCode requested is valid in the dimensions 
    if (valid(token)) {
      if ((command.startsWith("expr"))) {
        // Returns this objects expression
        return this.matrix[cellVal[0]][cellVal[1]].getExpression();
      } else if (command.startsWith("display")) {
        // Returns how a Cell in given location will be displayed in the grid
        return this.matrix[cellVal[0]][cellVal[1]].toString();
      } else if (command.startsWith("value")) {
        // Returns the value for this cellCode by computing the expression
        return "" + this.matrix[cellVal[0]][cellVal[1]].getValue();
      } else {
        return this.matrix[cellVal[0]][cellVal[1]].getExpression();
      }
    }
    return null;
  }

  // This method sets a cell expression if the user 
  // provides valid input or else it returns null
  private String setCell(String command) {
    boolean isValid = valid(command);
    // Check if the cell code trying to be set is a valid code
    // in the current dimensions, if it is not then return false
    if (!isValid) {
      return null;
    }
    if (command.contains("=")) {
      int[] cellVal = storeCell(command);
      // I check if a command contains a quote because that
      // automatically means I can set the cell to a text cell
      if (command.contains("\"")) {
        this.matrix[cellVal[0]][cellVal[1]] = new TextCell();
        String expression = command.substring(command.indexOf("\""));
        this.matrix[cellVal[0]][cellVal[1]].setExpression(expression);
        // Checks if I have a valid date as user input by
        // using the date object and trying to parse the user command
      } else if (isDate(command)) {
        this.matrix[cellVal[0]][cellVal[1]] = new DateCell();
        String date = command.substring(command.indexOf("=") + 2);
        this.matrix[cellVal[0]][cellVal[1]].setExpression(date);
      } else {
        String number = command.substring(command.indexOf("=") + 2);
        this.matrix[cellVal[0]][cellVal[1]] = new NumberCell();
        this.matrix[cellVal[0]][cellVal[1]].setExpression(number);
      }
      return "";
    }
    return null;
  }

  // This method uses the DateFormat class parse method in order
  // to see if the command passed in is a valid date so then the
  // cell code that equals the
  private boolean isDate(String text) {
    try {
      DateFormat datef = new SimpleDateFormat("MM/dd/yyyy");
      // If it is parseable by the dateformat object then
      // the command is a date other wise it catch the exception
      // and return false
      datef.parse(text.substring(text.indexOf("=") + 2));
    } catch (ParseException e) {
      return false;
    }
    return true;
  }

  // Method checks if the user input is a valid command ex (a1) vs fhslfh
  // a1 will return true because it is a valid location in the cell matrix
  // and the other will return false
  private boolean valid(String command) {
    int[] cellVal = storeCell(command);
    if (cellVal == null) {
      return false;
    }
    if (cellVal[0] < this.rowCount && cellVal[1] < this.colCount) {
      return true;
    }
    return false;
  }

  // Returns a 2 length array which holds the row and column value 
  // for a cell code, it is a public and static method because it is 
  // accessed in the number cell class statically
  public static int[] storeCell(String cellCode) {
    // not all commands passed into method are codes so it may need
    // to be parsed to just hold the code
    if (cellCode.contains(" ")) {
      cellCode = cellCode.substring(0, cellCode.indexOf(" "));
    }
    int[] cellVal = new int[2];
    // Using try-catch to make sure cellCode is correctly formatted
    try {
      cellVal[0] = Integer.parseInt(cellCode.substring(1)) - 1;
      cellVal[1] = (int) cellCode.charAt(0) - 97;
    } catch (NumberFormatException ex) {
      return null;
    }
    return cellVal;
  }

  // This method will check if the user is trying to access or mutate the properties 
  // of the grid such as rows, columns, and cell width and pass on the command to be
  // processed accordingly
  private String sizeCommands(String command) {
    if (command.contains("rows")) {
      return rowCommands(command);
    } else if (command.contains("cols")) {
      return colCommands(command);
    } else if (command.contains("width")) {
      return widthCommands(command);
    }

    // Returns null because that means the user not accessing or mutating the grid size
    return null;
  }

  // This method will process commands related setting
  // the cell width or getting the cell width
  private String widthCommands(String command) {
    if (command.contains("=")) {
      int cellWidth = Integer.parseInt(command.substring((command.indexOf("=") + 2)));
      if (cellWidth < 3 || cellWidth > 29) {
        return "Illegal input, cell width must be between 3-29";
      }
      this.cellWidth = cellWidth;
      return "" + this.cellWidth;
    }
    return "" + this.cellWidth;
  }

  // This method will process commands related setting the number of columns in 
  // the grid or getting the cols in the grid
  private String colCommands(String command) {
    if (command.contains("=")) {
      int colCount = Integer.parseInt(command.substring((command.indexOf("=") + 2)));
      // Validating column count requested by user, if it is not valid then
      // column length will not be changed
      if (colCount < 1 || colCount > 26) {
        return "Illegal input, col count must be between 1-26";
      }
      this.colCount = colCount;
      this.fillMatrix();
      return "" + this.colCount;
    }
    return "" + this.colCount;
  }

  // This method proceeses commands related to getting
  // the number of rows in the grid or setting the number
  // of rows in the grid
  private String rowCommands(String command) {
    if (command.contains("=")) {
      // Create an integer for the row count requested by the user
      int rowCount = Integer.parseInt(command.substring((command.indexOf("=") + 2)));
      if (rowCount < 1 || rowCount > 49) {
        return "Illegal input, row count must be between 1-49";
      }
      this.rowCount = rowCount;
      // Fills matrix with empty cells because grid property has been altered
      // in the previous line
      this.fillMatrix();
      return "" + this.rowCount;
    }
    // If the command didn't contains "=" the method will return the rows in the
    // current grid because those are the only two commands that include rows
    return "" + this.rowCount;
  }

  // This method calls helper methods to help print the matrix in the console
  private String printMatrix() {
    String matrice = this.heading();
    matrice += this.seperator();
    // Loops through the create row and create seperator methods
    // to geenerate proper sized grid
    for (int row = 1; row <= this.rowCount; row++) {
      matrice += this.row(row);
      matrice += this.seperator();
    }
    return matrice;
  }

  // This method creates the heading (| A | B |)
  // of the spreadsheet that is displayed in the console
  private String heading() {
    String heading = "    |";
    for (int cells = 65; cells < this.colCount + 65; cells++) {
      int spaces = 1;
      // Uused condition cellWidth/2 because half way through
      // the cell width the column letter must be added
      while (spaces <= this.cellWidth / 2) {
        heading += " ";
        spaces++;
      }
      heading += (char) cells;
      while (spaces < this.cellWidth) {
        heading += " ";
        spaces++;
      }
      // "|" is added because the currect number of spaces and the character
      // for the width of one column header has been made
      heading += "|";
    }
    return heading + "\n";
  }

  // This methods creates the seperator with in the grid displayed in the console (----+----+)
  private String seperator() {
    // Created fencepost alogirthm because the seperator size that contains
    // the row number is different from the seperator size of the cells
    String seperator = "----+";
    for (int cells = 0; cells < this.colCount; cells++) {
      for (int dash = 0; dash < this.cellWidth; dash++) {
        seperator += "-";
      }
      seperator += "+";
    }
    return seperator + "\n";
  }

  // This method prints each row of the grid by checking
  // if any value exists in the cell that is to be printed
  // and generating the row numbers for each row created
  private String row(int rowNumber) {
    String row = String.format("%3d |", rowNumber);
    for (int cells = 0; cells < this.colCount; cells++) {
      int displayLength = 0;
      if (this.matrix[rowNumber - 1][cells] != null) {
        displayLength = this.matrix[rowNumber - 1][cells].toString().length();
      }
      for (int dash = 0; dash < this.cellWidth - displayLength; dash++) {
        row += " ";
      }
      if (this.matrix[rowNumber - 1][cells].toString().length() > this.cellWidth) {
        // I use rowNumber-1 because the array is 0 indexed but row numbers aren't
        row += (this.matrix[rowNumber - 1][cells].toString()).substring(0, this.cellWidth) + "|";
      } else {
        row += this.matrix[rowNumber - 1][cells].toString() + "|";
      }
    }
    return row + "\n";
  }

  // This method clears a specific cells properties
  // by setting it to a default cell
  private void clearCell(String command) {
    command = command.substring(command.indexOf(" ") + 1);
    // Sets a specific cell inputed by the user to its default
    int row = Integer.parseInt(command.substring(1)) - 1;
    int col = (int) command.charAt(0) - 97;
    this.matrix[row][col] = new Cell();
  }

  // This method resets the cell matrix to a default cell
  // by setting into a new cell object which terminates
  // an existing fields such as a number/date cell
  private void fillMatrix() {
    this.matrix = new Cell[this.rowCount][this.colCount];
    for (int row = 0; row < matrix.length; row++) {
      for (int col = 0; col < matrix[row].length; col++) {
        // Setting each element in the matrix to a cell,
        // so it resets the fields to defaults
        matrix[row][col] = new Cell();
      }
    }
  }
}
