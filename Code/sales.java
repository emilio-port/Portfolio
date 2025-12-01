import javax.swing.JOptionPane;

public class sales {
    public static void main(String[] args) {
        final int ONE_WEEK = 7; // Number of elements.
        // Create an array to hold sales amounts for a week.
        double[] sales = new double[ONE_WEEK];
        // Get the week's sales figures.
        getValues(sales);
        // Create a SalesData object, initialized
        salesData week = new salesData(sales);
        // Display the total, average, highest, and lowest sales amounts for the week.
        JOptionPane.showMessageDialog(null, String.format("The total sales were $%,.2f\n" +
        "The average sales were $%,.2f\n" + "The highest sasles were $%,.2f\n" + "The lowest sales were $%,.2f",
        week.getTotal(), 
        week.getAverage(), 
        week.getHighest(), 
        week.getLowest()));
        System.exit(0);
    }
    // The getValues method asks the user to enter sales amounts for each element of an array.
    private static void getValues (double[] array) {
        String input; // To hold user input.
        // Get sales for each day of the week.
        for (int i = 0; i < array.length; i++) {
            input = JOptionPane.showInputDialog("Enter the sales for day " + (i + 1) + ".");
            array[i] = Double.parseDouble(input);
        }
    }
}