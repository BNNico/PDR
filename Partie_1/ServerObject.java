import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;

public class ServerObject {
    private Object o;
    private int id;
    private etat lock;
    private List<Client_itf> list_reader;
    private Client_itf writer;

    public ServerObject(Object o, int id) {
        this.o = o;
        this.list_reader = new ArrayList<Client_itf>();
        this.writer = null;
        this.id = id;
        this.lock = etat.NL;
    }

    public Object geto() {
        return this.o;
    }

    public synchronized Object lock_read(Client_itf client) {
        // Si il existe un ecrivain, on doit reduire ses droits 
        if (lock == etat.WL) {
            try {
                this.o = writer.reduce_lock(id);
                // l'ecrivaint devient un potentiel lecteur
                list_reader.add(writer);
            } catch (RemoteException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } 
        
        // On ajoute le client à la liste des lecteurs (si il n'y est pas dejà)
        if (!(list_reader.contains(client))) {
            list_reader.add(client);
        }

        // Mise à jour de l'etat et de l'ecrivain 
        this.lock = etat.RL;
        writer = null;
        return this.o;
    }

    public synchronized Object lock_write(Client_itf client) {
        // Si le client est dejà un lecteur, on le supprime de la liste des lecteurs
        // Sinon risque de famine sur le client et blocage de l'objet partage (impossible d'ecrire et de lire)
        if (list_reader.contains(client)) {
            list_reader.remove(client);
        }
        
        switch (lock) {
            case RL:
                // On doit invalider les tous les lecteurs    
                for (Client_itf c : list_reader) {
                        try {
                            c.invalidate_reader(id);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                
                // la liste des lecteurs devient alors vide
                list_reader.clear();
                break;
            case WL:               
                // On doit invalider l'ecrivain
                try {
                    this.o = writer.invalidate_writer(id);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            default:
                break;
        }
        
        // Mise a jour de l'etat et de l'ecrivain
        this.lock = etat.WL;
        writer = client;
        return this.o;
    }
    
}
