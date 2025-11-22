public class invalidSubscript {
    public static void main(String[] args) {
        int[] values = new int[3];

        System.out.println("I will attempt to store four " + 
            "numbers in as three element array.");
        
            for(int index = 0; index < 4; index++) {
                System.out.println("Now processing element " + index);
                values[index] = 10;
            }
    }
}
