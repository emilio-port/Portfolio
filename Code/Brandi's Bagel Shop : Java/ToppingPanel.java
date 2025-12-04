import java.awt.*;
import javax.swing.*;

//The ToppingPanel class allows the user to select the toppings for the bagel.

public class ToppingPanel extends JPanel {
    //The following constants are used to indicate the cost of toppings.
    public final double CREAM_CHEESE = 0.50;
    public final double BUTTER = 0.25;
    public final double PEACH_JELLY = 0.75;
    public final double BLUEBERRY_JAM = 0.75;

    private final JCheckBox creamCheese; // To select cream cheese
    private final JCheckBox butter; // To select butter
    private final JCheckBox peachJelly; // To select peach jelly
    private final JCheckBox blueberryJam; // To select blueberry jam

    //Constructor

    public ToppingPanel(){
        //Create a GridLayout manager with four rows and one column.
        setLayout(new GridLayout(4,1));

        //Create the check boxes.
        creamCheese = new JCheckBox("Cream cheese");
        butter = new JCheckBox("butter");
        peachJelly = new JCheckBox("Peach jelly");
        blueberryJam = new JCheckBox("Blueberry jam");

        //Add a border around the panel.

        setBorder(BorderFactory.createTitledBorder("Toppings"));

        //Add the check boxes to the panel
        add(creamCheese);
        add(butter);
        add(peachJelly);
        add(blueberryJam);
    }
    //getToppingCost method @return The cost of the selected toppings.
    public double getToppingCost(){
        double toppingCost = 0.0;

        if(creamCheese.isSelected())
            toppingCost += CREAM_CHEESE;
        if(butter.isSelected())
            toppingCost += BUTTER;
        if(peachJelly.isSelected())
            toppingCost += PEACH_JELLY;
        if(blueberryJam.isSelected())
            toppingCost += BLUEBERRY_JAM;
        
        return toppingCost;
    }
}