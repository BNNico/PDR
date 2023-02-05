import java.io.*;

public class SharedObject implements Serializable, SharedObject_itf {
	private int id;
	private etat lock;
	protected Object obj;

	public SharedObject(int id, Object obj) {
		this.obj = obj;
		this.id = id;
		this.lock = etat.NL;
	}

	// invoked by the user program on the client node
	public synchronized void lock_read() {
		if (lock == etat.RLC || lock == etat.RLT) {
			lock = etat.RLT;
		} else if (lock == etat.WLC) {
			lock = etat.RLT_WLC;
		} else {
			this.obj = Client.lock_read(id);
			if (lock == etat.NL || lock == etat.RLC) {
				lock = etat.RLT;
			}
		}
	}
		

	// invoked by the user program on the client node
	public synchronized void lock_write() {
		// Conformement a l'automate du sujet
		if (lock == etat.WLC || lock == etat.WLT) {
			// on change juste le lock, pas besoin de solliciter le serveur
			// l'objet partage est deja dans le cache du client
			this.lock = etat.WLT;
		} else {
			this.obj = Client.lock_write(id);
			if (lock == etat.NL || lock == etat.RLC) {
				this.lock = etat.WLT;
			}
		}
		
	}

	// invoked by the user program on the client node
	public synchronized void unlock() {
		// Conformement a l'automate du sujet
		if (lock == etat.RLT ) {
			this.lock = etat.RLC;
		}
		if (lock == etat.WLT || lock == etat.RLT_WLC ) {
			this.lock = etat.WLC;
		}
		
		// libere, au hasard, un thread qui attendait sur cet objet  
		this.notify();
	}


	// callback invoked remotely by the server
	public synchronized Object reduce_lock() {
		// Conformement a l'automate du sujet
		if (lock == etat.RLT_WLC) {
			lock = etat.RLT;
		} else if (lock == etat.WLC || lock == etat.WLT) {
			lock = etat.RLC;
		}
		return this.obj;	
	}

	// callback invoked remotely by the server
	public synchronized void invalidate_reader() {
		// Conformement a l'automate du sujet
		if (lock == etat.RLT || lock == etat.RLC ) {
			lock = etat.NL;
			
		}	

	}

	public synchronized Object invalidate_writer() {
		// Conformement a l'automate du sujet
		if (lock == etat.WLC || lock == etat.WLT || lock == etat.RLT_WLC) {
			lock = etat.NL;
		}
		return this.obj;
	}

	public int getId() {
		return id;
	}

	public etat getLock() {
		return lock;
	}
	public void setLock(etat etat) {
		this.lock = etat;
	}
}
