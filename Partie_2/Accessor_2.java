import java.util.Random;

public class Accessor_2 {
    public static void modifier(IntShared_itf s,IntShared_itf s0, int n) {
        (s).lock_write();
        ((IntShared_itf) s).write(-n);
        (s).unlock();
        (s0).lock_write();
        ((IntShared_itf) s0).write(n);
        (s0).unlock();
    }

    public static void afficher(){
        int somme = 0;
        for (int i = 0; i<10; i++){
            IntShared_itf s = (IntShared_itf) Client.lookup("IntShared" + i);
            (s).lock_read();
            somme += ((IntShared_stub)s).read();
            (s).unlock();
        }
        System.out.println("La somme = " + somme);    
    }
    
    public static void main(String[] argv){

        Random rand = new Random();
        Client.init();
        IntShared_itf[] s_tab = new IntShared_itf[10];   
        for (int i = 0; i < 10; i++) {
            s_tab[i] = (IntShared_itf) Client.lookup("IntShared" + i);
        }
        for (int i = 0; i < 5; i++) {
            int n = rand.nextInt(1000);
            int r = rand.nextInt(10);
            int p = rand.nextInt(10);
            modifier(s_tab[r], s_tab[p], n);
        }
        afficher();
    }
    
}
