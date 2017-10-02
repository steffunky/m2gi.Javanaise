/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.io.*;



public class JvnServerImpl 	
extends UnicastRemoteObject 
implements JvnLocalServer, JvnRemoteServer{

	private static final long serialVersionUID = 1L;
	private static final String coordNetworkURL = "//localhost:2001";
	
	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	
	private Hashtable<Integer, JvnObject> jvnObjects;

	// Remote object
	private JvnRemoteCoord jc;

	/**
	 * Default constructor
	 * @throws JvnException
	 **/
	private JvnServerImpl() throws Exception {
		super();
		// to be completed
		this.jvnObjects = new Hashtable<Integer, JvnObject>();
		this.jc = (JvnRemoteCoord) Naming.lookup(String.join("/", coordNetworkURL, JvnRemoteCoord.jvnCoordRemoteIdentfier));
	}

	/**
	 * Static method allowing an application to get a reference to 
	 * a JVN server instance
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() {
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				return null;
			}
		}
		return js;
	}

	/**
	 * The JVN service is not used anymore
	 * @throws JvnException
	 **/
	public void jvnTerminate() throws jvn.JvnException {
		// to be completed 
		try {
			this.jc.jvnTerminate(this);
			this.jvnObjects.clear();
			this.jvnObjects = null;
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new JvnException("Erreur lors de la tentative de terminaison du coordinateur distant");
		}

	} 

	/**
	 * creation of a JVN object
	 * @param o : the JVN object state
	 * @throws JvnException
	 **/
	public JvnObject jvnCreateObject(Serializable o) throws jvn.JvnException 
	{ 
		try {
			int id = this.jc.jvnGetObjectId();
			JvnObject obj = new JvnObjectImpl(o, id);
			obj.jvnLockWrite(); // Mise en place du verrou en écriture sur l'objet
			return obj;
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new JvnException("Erreur lors de l'obtention d'un nouvel identifiant auprès du coordinateur distant");
		}
		
	}

	/**
	 *  Associate a symbolic name with a JVN object
	 * @param jon : the JVN object name
	 * @param jo : the JVN object 
	 * @throws JvnException
	 **/
	public  void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException {
		// to be completed 
		try {
			this.jc.jvnRegisterObject(jon, jo, this);
			this.jvnObjects.put(jo.jvnGetObjectId(), jo);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new JvnException("Erreur lors de la tentative d'enregistrement de l'objet dans le coordinateur distant");
		}
	}

	/**
	 * Provide the reference of a JVN object beeing given its symbolic name
	 * @param jon : the JVN object name
	 * @return the JVN object 
	 * @throws JvnException
	 **/
	public JvnObject jvnLookupObject(String jon) throws jvn.JvnException {
		// to be completed 
		try {
			JvnObject obj = this.jc.jvnLookupObject(jon, this);
			this.jvnObjects.put(obj.jvnGetObjectId(), obj);
			return obj;
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new JvnException("Erreur lors de la tentative d'obtention de l'objet disponible dans le coordinateur distant");
		}
	}	

	/**
	 * Get a Read lock on a JVN object 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws  JvnException
	 **/
	public Serializable jvnLockRead(int joi) throws JvnException {
		// to be completed 
		try {
			return this.jc.jvnLockRead(joi, this);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new JvnException("Erreur lors de la tentative de vérouillage de l'objet en lecture auprès du coordinateur distant");
		}

	}	
	/**
	 * Get a Write lock on a JVN object 
	 * @param joi : the JVN object identification
	 * @return the current JVN object state
	 * @throws  JvnException
	 **/
	public Serializable jvnLockWrite(int joi) throws JvnException {
		// to be completed 
		try {
			return this.jc.jvnLockWrite(joi, this);
		} catch (RemoteException e) {
			e.printStackTrace();
			throw new JvnException("Erreur lors de la tentative de vérouillage de l'objet en écriture auprès du coordinateur distant");
		}
	}	


	/**
	 * Invalidate the Read lock of the JVN object identified by id 
	 * called by the JvnCoord
	 * @param joi : the JVN object id
	 * @return void
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized void jvnInvalidateReader(int joi)
			throws java.rmi.RemoteException,jvn.JvnException {
		// to be completed 
		
	};

	/**
	 * Invalidate the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized Serializable jvnInvalidateWriter(int joi)
			throws java.rmi.RemoteException,jvn.JvnException { 
		// to be completed 
		return null;
	};

	/**
	 * Reduce the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized Serializable jvnInvalidateWriterForReader(int joi)
			throws java.rmi.RemoteException,jvn.JvnException { 
		// to be completed 
		return null;
	};

}


