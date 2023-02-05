public class Creator_2 {
    public static void main(String[] argv) {
        IntShared_itf so = null;
        Client.init();
        for (int i = 0; i < 10; i++) {
            so = (IntShared_itf) Client.create(new IntShared());
            Client.register("IntShared" + i, so);
            so = null;
        }
        System.out.println("Creation ok");
    }
}