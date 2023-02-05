public class Creator {
    public static void main(String[] argv) {
        SharedObject[] so;
        so = new SharedObject[10];
        Client.init();
        for (int i = 0; i < 10; i++) {
            so[i] = Client.create(new IntShared());
            Client.register("IntShared" + i, so[i]);
        }
        System.out.println("Creation ok");
}
}