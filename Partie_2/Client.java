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
			// TODO Auto-generated catch block
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
			
			// Si l'objet n'existe pas dans le serveur
			if (id == -1) {
				return null;
			}
			
			// on veut avoir l'objet sans pour autant le lock_read ou lock_write
			// avant (etape 1) on remplacait o par null mais ce n'est plus possible 
			// car on a besoin de la classe de l'objet pour créer le stub 
			if (id_SharedObject.containsKey(id)) {
				obj =  id_SharedObject.get(id);
			} else {
				Object o = serveur.donneObjet(id);
				obj = createStub(id,o);
			}
		} catch (RemoteException e) {
			e.printStackTrace();
		}
		return obj;
	}		
	
	// binding in the name server
	public static void register(String name, Object so) {
		try {
			// On utilise l'identifiant sauvegardé dans create
			serveur.register(name,((SharedObject) so).getId());
		} catch (RemoteException e) {
			e.printStackTrace();
		}

	}

	// creation of a shared object
	public static SharedObject create(Object o) {
		SharedObject objet = null ;
		try {
			// on récupère l'id de l'objet partagé créé dans le serveur
			// et on sauvegarde dans identifiant			
			int identifiant = serveur.create(o);
			objet = createStub(identifiant,o);
		} catch (RemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return objet;
	}

	// creation of a stub
	public static SharedObject createStub(int id, Object o) {
		SharedObject objet = null ;
		try {
			// on prend le nom de la classe de l'objet o	
			Class<?> classe = Class.forName(o.getClass().getName() + "_stub");

			// on récupère le constructeur de la classe
			java.lang.reflect.Constructor<?> constructeur = classe.getConstructor(new Class[]{int.class, Object.class});

			// on crée une instance de l'objet partagée
			objet = (SharedObject) constructeur.newInstance(new Object[]{id, o});

			id_SharedObject.put(objet.getId(), objet);
			
		} catch (Exception e) {
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
