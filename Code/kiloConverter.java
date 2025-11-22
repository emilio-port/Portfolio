//GUI : Stands for Graphical User Interface
//JFC : Java Foundation Classes (Libraries) - Swing, Random, Scanner
//AWT : Abstract Windowing Toolkit - Communicates with a layer of software to perform actions, uses heavyweight components
//Swing Classes : uses lightweight components 
import java.awt.event.*; // Needed for Swing classes
import javax.swing.*; // Needed for ActionListener Interface

public class kiloConverter extends JFrame {
    private JPanel panel; 
    private JLabel messageLabel;
    private JTextField kiloTextField;
    private JButton calcButton;
    private final int WINDOW_WIDTH = 310;
    private final int WINDOW_HEIGHT = 100;
    //Constructor
    public kiloConverter(){
        //Set the title
        setTitle("Kilometer Converter");
        //Set the size of the window
        setSize(WINDOW_WIDTH, WINDOW_HEIGHT);
        //Specify what happens when the close button is clicked.
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        //Build the panel and add it to the frame
        buildPanel();
        //Add the panel to the frame's content pane
        add(panel);
        //Display the window
        setVisible(true);
    }
    private void buildPanel(){
        //Create a label to display instructions
        messageLabel = new JLabel("Enter a distance in kilometers");
        //Create a text field 10 characters wide
        kiloTextField = new JTextField(10);
        //Create a button with the caption "Calculate"
        calcButton = new JButton("Calculate X");
        //Add an action listener to the button
        calcButton.addActionListener(new CalcButtonListener());
        //Create a JPanel object and let the panel field refernce it.
        panel = new JPanel();

        //Add the label, text field, and button components to the panel.
        panel.add(messageLabel);
        panel.add(kiloTextField);
        panel.add(calcButton);
    }

    private class CalcButtonListener implements ActionListener{
        @Override
        public void actionPerformed(ActionEvent e) {
            final double CONVERSION = 0.6214;
            String input;
            double miles;

            input = kiloTextField.getText();

            System.out.println("Reading " + input + " from the text field.");
            System.out.println("Converted value: " + Double.valueOf(input));
            miles = Double.parseDouble(input) * CONVERSION;
            JOptionPane.showMessageDialog(null, input + " kilometers is " + miles + " miles.");
            System.out.println("Ready for the next input.");
        }
    }
    public static void main(String[] args){
        new kiloConverter();
    }
}
