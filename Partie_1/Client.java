import java.rmi.*;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.net.*;

public class Client extends UnicastRemoteObject implements Client_itf {
	private static HashMap<Integer,SharedObject> id_SharedObject;
	private static Server_itf serveur;
	private static Client client;

	public Client() throws RemoteException {
		super();
		id_SharedObject = new HashMap<Integer,SharedObject>();
		try {
			serveur = (Server_itf) Naming.lookup("rmi://localhost:4000/MonServeur");
		} catch (MalformedURLException | NotBoundException e) {
			e.printStackTrace();
		}
	}


///////////////////////////////////////////////////
//         Interface to be used by applications
///////////////////////////////////////////////////

	// initialization of the client layer
	public static void init() {
		try {
			client = new Client();
		} catch (RemoteException e) {
			e.printStackTrace();
		}
	}
	
	// lookup in the name server
	public static SharedObject lookup(String name) {
		SharedObject obj = null;
		try {
			int id =  serveur.lookup(name);
			
			// Si l'objet partagé n'existe pas dans le serveur
			if (id == -1) {
				return null;
			}
			
			// On initialise l'objet partagé à null mais avec le bon id
			// car un lock_read ou un lock_write va amener la bonne valeur du serveur
			obj = new SharedObject(id, null);
		
			// On ajoute l'objet partagé dans le cache si il n'y est pas déjà
			if (id_SharedObject.get(id) == null) {
				id_SharedObject.put(id, obj);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return obj;
	}		
	
	// binding in the name server
	public static void register(String name, SharedObject_itf so) {
		try {
			// On récupère l'id de l'objet partagé
			int n = ((SharedObject) so).getId();

			// on l'enregistre dans le serveur de nom
			serveur.register(name,n);
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		SharedObject objet = null;
		try {
			// on récupère l'id de l'objet partagé créé dans le serveur
			int n = serveur.create(o);
			
			//on créé l'objet partagé puis on le met dans le cache
			 objet = new SharedObject(n,o);
			id_SharedObject.put(n,objet);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return objet;
	}
	
/////////////////////////////////////////////////////////////
//    Interface to be used by the consistency protocol
////////////////////////////////////////////////////////////

	// request a read lock from the server
	public static Object lock_read(int id) {
		Object o = null;
		try {
			o = serveur.lock_read(id, client);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return o;

	}

	// request a write lock from the server
	public  static Object lock_write (int id) {
		Object o = null;
		try {
			 o = serveur.lock_write(id, client);
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return o;
	}

	// receive a lock reduction request from the server
	public Object reduce_lock(int id) throws java.rmi.RemoteException {
		SharedObject objet = id_SharedObject.get(id);
		
		synchronized(objet) {
			// On attend que l'ecrivain ait fini de modifier l'objet 
			while (objet.getLock() == etat.WLT) {
				try {
					objet.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			// On met à jour l'objet partagé
			objet.obj = objet.reduce_lock();
		}

		return objet.obj;
	}


	// receive a reader invalidation request from the server
	public void invalidate_reader(int id) throws java.rmi.RemoteException {
		SharedObject objet = id_SharedObject.get(id);
		
		synchronized(objet) {
			// On attend que le lecteur ait fini de lire l'objet
			while (objet.getLock() == etat.RLT) {
				try {
					objet.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			} 
			// On met à jour l'objet partagé
			objet.invalidate_reader();
		}
	}


	// receive a writer invalidation request from the server
	public Object invalidate_writer(int id) throws java.rmi.RemoteException {
		SharedObject objet = id_SharedObject.get(id);
		synchronized(objet) {
			// On attend que l'ecrivain ait fini de modifier l'objet
			while (objet.getLock() == etat.WLT) {
				try {
					objet.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
		// On met à jour l'objet partagé
		objet.obj = objet.invalidate_writer();
		return objet.obj;
	}
}
