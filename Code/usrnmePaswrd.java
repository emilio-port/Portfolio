import javax.swing.JOptionPane;

public class usrnmePaswrd {
    public static void main(String[] args) {

        String inputStringun;
        String inputStringpw;
        String username = "123";
        String password = "456";
        
        inputStringun = JOptionPane.showInputDialog("Enter your username: ");

        inputStringpw = JOptionPane.showInputDialog("Enter your password: ");

        if(inputStringun.equals(username) && inputStringpw.equals(password)){
            JOptionPane.showMessageDialog(null, "Welcome back user.");
        }

        else if(!inputStringun.equals(username) || !inputStringpw.equals(password)) {
        JOptionPane.showMessageDialog(null, "Incorrect username or password. ");
        JOptionPane.showMessageDialog(null, "Please reenter. ");
        JOptionPane.showInputDialog("Enter your username: ");
        JOptionPane.showInputDialog("Enter your password: ");
        }

        System.exit(0);

    }
    
}