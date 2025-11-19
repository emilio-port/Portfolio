import java.util.Scanner;
import java.util.ArrayList;
import java.util.List;
/*A program that utilizes Lames Th return three digit pairs that 
share the same number of steps to calc GCD*/
/*For the full three-digit range (100-999):
    *WARNING*
    1 step: ~80,000-100,000 pairs

    2 steps: ~60,000-80,000 pairs

    3 steps: ~40,000-60,000 pairs

    4 steps: ~30,000-40,000 pairs

    5 steps: ~20,000-30,000 pairs 

    6 steps: ~10,000-15,000 pairs

    7 steps: ~5,000-8,000 pairs

    8+ steps: Fewer pairs */

public class lamesTheorem2 {
    
    // Method to compute GCD and count the steps.
    public static int gcdSteps(int a, int b) {
        int steps = 0;
        int originalA = a;
        int originalB = b;

        // Loop for the Euclidean Algorithm.
        while(b != 0) {
            int temp = a % b;
            a = b;
            b = temp;
            steps++;
        }
        return steps;
    }

    // Method to find all three-digit pairs with given number of steps
    public static List<int[]> findPairsWithSteps(int targetSteps) {
        List<int[]> pairs = new ArrayList<>();
        
        // Check all three-digit pairs (100-999)
        for (int a = 100; a <= 999; a++) {
            for (int b = a + 1; b <= 999; b++) { // b > a to avoid duplicates
                int steps = gcdSteps(a, b);
                if (steps == targetSteps) {
                    pairs.add(new int[]{a, b});
                }
            }
        }
        return pairs;
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Enter the number of steps in Euclidean algorithm: ");
        int targetSteps = scanner.nextInt();

        if (targetSteps < 1) {
            System.out.println("Number of steps must be at least 1.");
        } else {
            System.out.println("Finding all three-digit pairs that require " + targetSteps + " steps...");
            
            List<int[]> pairs = findPairsWithSteps(targetSteps);
            
            if (pairs.isEmpty()) {
                System.out.println("No three-digit pairs found that require exactly " + targetSteps + " steps.");
            } else {
                System.out.println("Three-digit pairs requiring " + targetSteps + " steps:");
                System.out.println("Total pairs found: " + pairs.size());
                
                // Display pairs (you might want to limit this for large outputs)
                int count = 0;
                for (int[] pair : pairs) {
                    System.out.printf("(%d, %d)", pair[0], pair[1]);
                    count++;
                    
                    // Add some formatting for readability
                    if (count % 5 == 0) {
                        System.out.println();
                    } else if (count < pairs.size()) {
                        System.out.print(", ");
                    }
                }
                if (count % 5 != 0) System.out.println();
            }
        }

        scanner.close();
    }
}