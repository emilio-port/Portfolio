import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

//The OrderCalculatorGUI class creates the GUI for the Brandi's Bagel House application.
public class OrderCalculatorGUI extends JFrame {
    public BagelPanel bagels; // Bagel panel
    public ToppingPanel toppings; // Topping panel
    public CoffeePanel coffee; // Coffee panel
    public GreetingPanel banner; // To display a greeting
    public JPanel buttonPanel; // To hold the buttons
    public JButton calcButton; // To calculate the cost
    public JButton exitButton; // To exit the application
    public final double TAX_RATE = 0.06; // Sales tax rate

    //Constructor

    public OrderCalculatorGUI(){
        //Display a title.
        setTitle("Order Calculator");

        //Specify an action for the close button.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create a BorderLayout manager.
        setLayout(new BorderLayout());

        //Create the custom panel.
        banner = new GreetingPanel();
        bagels = new BagelPanel();
        toppings = new ToppingPanel();
        coffee = new CoffeePanel();

        //Create the button panel.
        buildButtonPanel();

        //Add the components to the content pane.
        add(banner, BorderLayout.NORTH);
        add(bagels, BorderLayout.WEST);
        add(toppings, BorderLayout.CENTER);
        add(coffee, BorderLayout.EAST);
        add(buttonPanel, BorderLayout.SOUTH);

        //Pack the contents of the window and display it.
        pack();
        setVisible(true);
    }

    //The buildbuttonPanel method builds the button panel.

    private void buildButtonPanel(){
        //Create a panel for the buttons.
        buttonPanel = new JPanel();

        //Create the buttons.
        calcButton = new JButton("Calculate");
        exitButton = new JButton("Exit");

        //Register the action listeners.
        calcButton.addActionListener(new CalcButtonListener());
        exitButton.addActionListener(new ExitButtonListener());

        //Add the buttons to the button panel.
        buttonPanel.add(calcButton);
        buttonPanel.add(exitButton);
    }

    //Private inner class the handles the event when the user clicks the Calculate button.
    private class CalcButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            //Variables to hold the subtotal, tax, and total
            double subtotal, tax, total;

            //Calculate the subtotal.
            subtotal = bagels.getBagelCost() + 
                    toppings.getToppingCost() + 
                    coffee.getCoffeeCost();

            //Calculate the sales tax.
            tax = subtotal * TAX_RATE;

            //Calculate the total.
            total = subtotal + tax;

            //Display the charges.
            JOptionPane.showMessageDialog(null, String.format("Subtotal: $%,.2f\n"
                                                                + "Tax: $%,.2f\n" 
                                                                + "Total: $%,.2f", 
                                                                subtotal, tax, total));
        }
    }
    //Private inner class that handles the event when the user clicks the Exit button.
    private class ExitButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e){
            System.exit(0);
        }
    }
    //Main method
    public static void main(String[] args){
        new OrderCalculatorGUI();
    }
}