package HelloWorld;

public class Main {
    public static void main(String []args) {
        for(int x = 10; x < 1000; x = x+1) {
            try {
                Thread.sleep(1000);
                Main.sayhello();
            } catch (Exception e) {
                System.out.println("Got an exception!" + e);
            }
        }
    }

    public static void sayhello() throws Exception {
        try {
            System.out.println("Hello World");
        } catch (Exception e) { 
            throw e ;
        }
    }
}
