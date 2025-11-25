public class stringCompareTo {
    public static void main(String[] args) {
        String name1 ="Mark",
        name2 = "Mark";

        if(name1.compareTo(name2) < 0) {
            System.out.println(name1 + " is less than "  + name2);
        }
        else if(name1.compareTo(name2) == 0 ) {
            System.out.println(name1 + " is equal to " + name2);
        }
        else if(name1.compareTo(name2) > 0) {
            System.out.println(name1 + " is greater than "  + name2);
        }
    }
}