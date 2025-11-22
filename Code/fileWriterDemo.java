import java.io.*;
import java.util.Scanner;

public class fileWriterDemo {
    public static void main(String[] args) throws IOException
    {
        String fileName;
        String friendName;
        int numFriends;

        Scanner keyboard = new Scanner(System.in);
        System.out.print("How many friends do you have? ");
        numFriends = keyboard.nextInt();

        keyboard.nextLine();

        System.out.print("Enter the file name: ");
        fileName = keyboard.nextLine();


        PrintWriter outputFile = new PrintWriter(fileName);

        for (int i = 1; i <= numFriends; i++) {
            System.out.print("Enter the name of friend " + "Number" + i + ": ");
            friendName = keyboard.nextLine();

            outputFile.println(friendName);
        }

        outputFile.close();
        outputFile.println("Data written to the file. ");
    }
}
