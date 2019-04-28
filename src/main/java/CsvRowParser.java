import java.util.ArrayList;
import java.util.List;

public class CsvRowParser {
    public static List<String> parseCsvRow(String row) {
        List<String> elements = new ArrayList<String>();
        StringBuilder element = new StringBuilder();
        boolean inQuotes = false; // Flag whether or not you're inside quotations
        for (int i = 0; i < row.length(); i++) {
            if (row.charAt(i) == '"') {
                inQuotes = !inQuotes;
            } else if (row.charAt(i) == ',' && !inQuotes) {
                elements.add(element.toString().trim()); // This is a missing attribute in the row
                element = new StringBuilder();
            } else {
                element.append(row.charAt(i)); // Continue reading current element
            }
        }
        return elements;
    }
}
