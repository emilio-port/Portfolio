import javax.swing.JOptionPane;

public class roomAreas {
    public static void main(String[] args) {

        double number;
        double totalArea;
        String input;

        rectangle kitchen = new rectangle();
        rectangle crewQuarters = new rectangle();
        rectangle starboardDeck = new rectangle();

        input = JOptionPane.showInputDialog("What is the kitchen's length?");
        number = Double.parseDouble(input);
        kitchen.setLength(number);

        input = JOptionPane.showInputDialog("What is the kitchen's width?");
        number = Double.parseDouble(input);
        kitchen.setWidth(number);

        input = JOptionPane.showInputDialog("What is the crew quarter's length?");
        number = Double.parseDouble(input);
        crewQuarters.setLength(number);

        input = JOptionPane.showInputDialog("What is the crew quarter's Width?");
        number = Double.parseDouble(input);
        crewQuarters.setWidth(number);

        input = JOptionPane.showInputDialog("What is the starboard deck's length?");
        number = Double.parseDouble(input);
        starboardDeck.setLength(number);

        input = JOptionPane.showInputDialog("What is the starboard deck's width?");
        number = Double.parseDouble(input);
        starboardDeck.setWidth(number);

        totalArea = kitchen.getArea() + crewQuarters.getArea() + starboardDeck.getArea();

        JOptionPane.showMessageDialog(null, "The total area is " + totalArea);

        System.exit(0);

    }
}