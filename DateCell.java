// Arav Popli
// Period 3
// Text Excel 
 
import java.util.Date;
import java.text.SimpleDateFormat;

// This class returns a formatted date to be displayed inside a cell 
public class DateCell extends Cell {

    // Annotations for Date and SimpleDateFormat: 
    // https://docs.oracle.com/javase/8/docs/api/java/text/SimpleDateFormat.html
    // http://tutorials.jenkov.com/java-internationalization/simpledateformat.html 
    // https://stackoverflow.com/questions/4496359/how-to-parse-date-string-to-date 
    // https://docs.oracle.com/javase/7/docs/api/java/util/Date.html 

    // Overrides to String for a date cell by printing it in a 
    // text format rather than a number format 
    public String toString() { 
       SimpleDateFormat format = new SimpleDateFormat("MMM d, yyyy");
       return format.format(new Date(this.getExpression()));
    }
}
