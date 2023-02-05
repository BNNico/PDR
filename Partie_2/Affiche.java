public class Affiche {
    public static void main(String[] argv){
        Client.init();
        SharedObject[] s_tab = new SharedObject[10];   
        for (int i = 0; i < 10; i++) {
            s_tab[i] = Client.lookup("IntShared" + i);
        }
        System.out.print("La liste = [ ");
        for (int i = 0; i < 10; i++) {
            s_tab[i].lock_read();
            System.out.print(((IntShared)s_tab[i].obj).read()  + " ");
            s_tab[i].unlock();
        }
        System.out.println("]");
    }
}
