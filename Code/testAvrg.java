import java.util.Scanner;

public class testAvrg {
    public static void main(String[] args) {
        int score1, score2, score3;
        double average;
        char repeat;
        String input;

        System.out.println("Thie program calculates the avg of three test scores. ");

        Scanner keyboard = new Scanner(System.in);

        do { 
            //Get 1st score
            System.out.print("Enter score #1: ");
            score1 = keyboard.nextInt();

            //Get 2nd score
            System.out.print("Enter score #2: ");
            score2 = keyboard.nextInt();

            //Get 3rd score
            System.out.print("Enter score #3: ");
            score3 = keyboard.nextInt();

            //Consume the remaining newline.
            keyboard.nextLine();
            
            //Calc avg.
            average = ((score1 + score2 + score3) / 3.0);
            System.out.println("The average is " + average);
            System.out.println();
            
            //Final input text.
            System.out.println("Would you like the average of another set? ");
            System.out.println("Enter Y for yes of N for no: ");
            
            //Input prompt.
            input = keyboard.nextLine();
            repeat = input.charAt(0);

            //The condition for the loop to end.

        } while (repeat == 'Y' || repeat == 'y');

    }

}
