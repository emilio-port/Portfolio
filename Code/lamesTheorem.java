import java.util.Scanner;

public class lamesTheorem {
    
    //Method to compute GCD and count the steps.
    public static int gcdSteps(int a, int b) {
        int steps = 0;

        //Loop for the Euclidean Algorithm.
        while(b != 0) {
            int temp = a % b;
            a = b;
            b = temp;
            steps++;
        }

        //Print GCD and return steps.
        System.out.println("GCD is: " + a);
        return steps;
    }
    @SuppressWarnings("ConvertToTryWithResources")
    public static void main(String[] args) {
        
        //Initialize scanner.
        Scanner scanner = new Scanner(System.in);

        //Input two 3-digit numbers.
        System.out.print("Enter the first 3-digit number: ");
        int a = scanner.nextInt();
        System.out.print("Enter the second 3-digit number: ");
        int b = scanner.nextInt();

        //Validate inputs and call GCD Method.
        if(a < 100 || a > 999 || b < 100 || b > 999) {
            System.out.println("Both numbers must be 3-digit integers. ");
        } else {
            int steps = gcdSteps(a,b);
            System.out.println("Number of steps in Euclidean algorithm: " + steps);
        }

        //Close scanner.
        scanner.close();
    }
}
