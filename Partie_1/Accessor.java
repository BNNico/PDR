import java.util.Random;

public class Accessor {
    public static void modifier(SharedObject s,SharedObject s0, int n) {
        s.lock_write();
        ((IntShared)s.obj).write(n);
        s.unlock();
        s0.lock_write();
        ((IntShared)s0.obj).write(n);
        s0.unlock();
    }

    public static void afficher(){
        int somme = 0;
        for (int i = 1; i<10; i++){
            SharedObject s = Client.lookup("IntShared" + i);
            s.lock_read();
            somme += ((IntShared)s.obj).read();
            s.unlock();
        }
        SharedObject s0 = Client.lookup("IntShared" + 0);
        s0.lock_read();
        System.out.println(((IntShared)s0.obj).read() + " -> " + somme + " ");    
        s0.unlock();
    }
    
    public static void main(String[] argv){

        Random rand = new Random();
        Client.init();
        SharedObject[] s_tab = new SharedObject[10];   
        for (int i = 0; i < 10; i++) {
            s_tab[i] = Client.lookup("IntShared" + i);
        }
        for (int i = 0; i < 5; i++) {
            int n = rand.nextInt(1000);
            int r = rand.nextInt(9)+1;

            modifier(s_tab[r], s_tab[0], n);
        }
        afficher();
    }
    
}
