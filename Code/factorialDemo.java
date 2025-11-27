import javax.swing.JOptionPane;

public class factorialDemo {
    public static void main(String[] args){
        String input;
        int number;

        input = JOptionPane.showInputDialog("Enter a nonnegative integer:");
        
        number = Integer.parseInt(input);

        JOptionPane.showMessageDialog(null, number+ "! is " + factorial(number));

        System.exit(0);
    }

    private static int factorial(int n){
        if(n==0)
            return 1;
        else
            return n * factorial(n-1);
    }
}
