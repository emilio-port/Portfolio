public class rectangleDemo {
    public static void main(String[] args) {
        
        rectangle box = new rectangle();

        box.setLength(10.0);
        box.setWidth(20.0);

        System.out.println("The box's length is " + box.getLength());

        System.out.println("The box's width is " + box.getWidth());

        System.out.println("The box's Area is " + box.getArea());
    }
}