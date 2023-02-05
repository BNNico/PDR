import java.rmi.*;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;

public class Server extends UnicastRemoteObject implements Server_itf{
    public static Map<String,Integer> nom_entier; 
    public static Map<Integer,ServerObject> entier_object; 
    public int last_id = 0;

    protected Server() throws RemoteException {
        super();
        nom_entier = new HashMap<String,Integer>();
        entier_object = new HashMap<Integer,ServerObject>();
    }
    
    @Override
    public int lookup(String name) throws RemoteException {
        if (!nom_entier.containsKey(name)) {
            return -1;
        }
        return nom_entier.get(name);
    }

    @Override
    public void register(String name, int id) throws RemoteException {
        nom_entier.put(name,id);
    }

    @Override
    public int create(Object o) throws RemoteException {
        last_id ++;
        
        // Création du ServerObject a partir de l'objet
        ServerObject new_o = new ServerObject(o, last_id);
        entier_object.put(last_id,new_o);
        return last_id;  
    }

    @Override
    public Object lock_read(int id, Client_itf client) throws RemoteException {
        ServerObject objet = entier_object.get(id);
        return objet.lock_read(client);
    }

    @Override
    public Object lock_write(int id, Client_itf client) throws RemoteException {
        ServerObject objet = entier_object.get(id);
        return objet.lock_write(client);
    }


    public static void main(String[] args) {
        try {
            Server server = new Server();
            Registry registry = LocateRegistry.createRegistry(4000);
            registry.bind("MonServeur", server);
        } catch (Exception e) {
            System.err.println("Server exception: " + e.toString());
            e.printStackTrace();
        }
    }

    
}
