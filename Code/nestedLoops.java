import java.util.Scanner;

public class nestedLoops {
    public static void main(String[] args) {
        
        int i, j, rowNum;

        Scanner keyboard = new Scanner(System.in);

        System.out.print("Enter a number of rows for bigger triangle in diamond: ");
        rowNum = keyboard.nextInt();

        int space = rowNum - 1;

        for (i = 1; i <= rowNum; i++){
            for (j = 1; j <= space; j++){
                System.out.print(" ");
                }
                space--;

            for (j = 1; j <= (2*i-1); j++){
                System.out.print("*");
            }
            System.out.println();
        }

        space = 1;
        for (i = 1; i <= (rowNum - 1); i++) {
            for(j = 1; j <= space; j++) {
                System.out.print(" ");
            }
            space++;

            for (j = 1; j <= (2*(rowNum-i)-1); j++){
                System.out.print("*");
            }
            System.out.println();
        }
        keyboard.close();
    }
}
