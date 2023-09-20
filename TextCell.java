// Arav Popli
// Period 3
// Text Excel 

// This class displays how a Text Cell should be displayed in a grid 
public class TextCell extends Cell {

    // This method overrides the cell toString method in 
    // order to show how the value of the text cell would be 
    // displayed on the grid (text inputed without quotations)
    public String toString() {
       return getExpression().replaceAll("\"", "");
    }
}
