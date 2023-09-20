// Arav Popli
// Text Excel 

/*
 * The NumberCell class will compute and manipulate numbers in a cell. 
 * It will return how a string should be displayed inside a cell.
 * It will calculate sum and averages over a range of cells and 
 * it can do special computations like sqrt,log, and power.
 * It processes expressions and finds the value of those expressions for cells.
 */
public class NumberCell extends Cell {

  // The NumberCell toString overrides the Cell toString
  // to return the value of the cell because the value of the cell
  // is the value that should be displayed in the grid
  public String toString() {
    return getValue() + "";
  }

  // This method returns the value of cells over a range (sum/avg)
  // or the value at a cell 
  public double getValue() {

    // Splits expression into smartly split tokens
    String expr = getExpression();
    String[] exprTokenized = GridBase.smartSplit(expr);
    if (exprTokenized.length == 1) {
      return Double.parseDouble(expr);
    }
    if (expr.contains("sqrt") || expr.contains("^") || expr.contains("log")) {
      return specialCompute(expr);
    }
    if (expr.contains("sum") || expr.contains("avg")) {
      return sumAndAverage(expr);
    }
    return compute(exprTokenized);
  }

  // This method processes special computations in an expression
  // such as sqrt, ^(power), or log
  private double specialCompute(String expr) {
    String[] tokens = GridBase.smartSplit(expr);
    if (expr.contains("sqrt")) {
      return Math.sqrt(Double.parseDouble(tokens[2]));
    } else if (expr.contains("log")) {
      return Math.log(Double.parseDouble(tokens[2]));
    }
    return Math.pow(Double.parseDouble(tokens[1]), Double.parseDouble(tokens[3]));
  }

  // This method computes cell expressions by replacing cell codes with their cell
  // values and computing the values of the cells in order from left to right
  private double compute(String[] expr) {
    for (int token = 0; token < expr.length; token++) {
      // Checks if a token is a cell code so it can replaced with the cell value at the code
      if (Grid.storeCell(expr[token]) != null && !isNumber(expr[token])) {
        expr[token] = GridBase.grid.processCommand("value " + expr[token]);
      }
    }
    for (int op = 0; op < expr.length; op++) {
      if (expr[op].equals("+")) {
        // Assigning the operand after operator to the computation of the operators before and after the operand
        expr[op + 1] = Double.parseDouble(expr[op - 1]) + Double.parseDouble(expr[op + 1]) + "";
      } else if (expr[op].equals("-")) {
        expr[op + 1] = Double.parseDouble(expr[op - 1]) - Double.parseDouble(expr[op + 1]) + "";
      } else if (expr[op].equals("*")) {
        expr[op + 1] = Double.parseDouble(expr[op - 1]) * Double.parseDouble(expr[op + 1]) + "";
      } else if (expr[op].equals("/")) {
        expr[op + 1] = Double.parseDouble(expr[op - 1]) / Double.parseDouble(expr[op + 1]) + "";
      }
    }
    // Returns the final computation of all the operators which is the final operand ([token + 1])
    return Double.parseDouble(expr[expr.length - 2]);
  }

  // This method returns true or false depending on
  // if a string can be converted to a number
  private boolean isNumber(String command) {
    try {
      Double.parseDouble(command);
      return true;
    } catch (NumberFormatException ex) {
      return false;
    }
  }

  // This methods finds the sum and average of the values of the cells over a
  // range provided by the user
  private double sumAndAverage(String expression) {
    String[] tokens = GridBase.smartSplit(expression);
    String code1 = tokens[2];
    String code2 = tokens[4];
    double sum = 0;
    double counter = 0;
    // Parsing Cell Codes to declare smallest and greatest row to traverse 
    int minRow = Integer.parseInt(code1.substring(1)) - 1;
    int maxRow = Integer.parseInt(code2.substring(1)) - 1;
    for (int row = minRow; row <= maxRow; row++) {
      for (char col = code1.charAt(0); col <= code2.charAt(0); col++) {
        sum += Double.parseDouble(GridBase.grid.processCommand("value " + col + (row + 1)));
        // Use a counter variable to keep track of the number of cells traversed in order
        // to find an avg value if requested by the user
        counter++;
      }
    }

    if (expression.contains("avg")) {
      return (sum / counter);
    }
    return sum;
  }
}
