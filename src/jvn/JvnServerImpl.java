/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.io.*;
import java.net.MalformedURLException;



public class JvnServerImpl 	
extends UnicastRemoteObject 
implements JvnLocalServer, JvnRemoteServer{

	private static final long serialVersionUID = 1L;
	private static final String coordNetworkURL = "//localhost:1099";
	
	// A JVN server is managed as a singleton 
	private static JvnServerImpl js = null;
	
	private Hashtable<Integer, JvnObject> jvnObjectsCache;

	// Remote object
	private JvnRemoteCoord jc;

	/**
	 * Default constructor
	 * @throws JvnException
	 * @throws NotBoundException 
	 * @throws RemoteException 
	 * @throws MalformedURLException 
	 **/
	private JvnServerImpl() throws JvnException, MalformedURLException, RemoteException, NotBoundException 
	{
		super();
		this.jvnObjectsCache = new Hashtable<Integer, JvnObject>();
		this.jc = (JvnRemoteCoord) Naming.lookup(String.join("/", coordNetworkURL, JvnRemoteCoord.jvnCoordRemoteIdentfier));
	}

	/**
	 * Static method allowing an application to get a reference to 
	 * a JVN server instance
	 * @throws JvnException
	 **/
	public static JvnServerImpl jvnGetServer() 
	{
		if (js == null){
			try {
				js = new JvnServerImpl();
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		}
		return js;
	}
	
	public static JvnServerImpl jvnCreateServer()
	{
		JvnServerImpl server;
		try {
			server = new JvnServerImpl();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		
		return server;
	}

	/**
	 * The JVN service is not used anymore
	 * @throws JvnException
	 **/
	public void jvnTerminate() throws jvn.JvnException 
	{ 
		try {
			this.jc.jvnTerminate(this);
			this.jvnObjectsCache.clear();
			this.jvnObjectsCache = null;
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
			int id = this.jc.jvnGetObjectId(); // Obtention de l'identifiant du nouvel objet jvn
			JvnObject obj = new JvnObjectImpl(o, id); // Objet en Lock Write
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
	public  void jvnRegisterObject(String jon, JvnObject jo) throws jvn.JvnException 
	{
		try {
			this.jc.jvnRegisterObject(jon, jo, this); // Enregistre l'objet jvn dans le coordinateur distant
			this.jvnObjectsCache.put(jo.jvnGetObjectId(), jo); // Enregistre l'objet jvn dans le cache du serveur
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
	public JvnObject jvnLookupObject(String jon) throws jvn.JvnException 
	{
		try {
			JvnObject obj = this.jc.jvnLookupObject(jon, this); // Obtient l'objet jvn distant
			
			if(obj != null)
				this.jvnObjectsCache.put(obj.jvnGetObjectId(), obj); // Enregistre ou mets à jour l'objet jvn dans le cache serveur
			
			return obj;
		} catch (RemoteException | NullPointerException e) {
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
	public Serializable jvnLockRead(int joi) throws JvnException 
	{
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
	public Serializable jvnLockWrite(int joi) throws JvnException 
	{
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
	public synchronized void jvnInvalidateReader(int joi) throws java.rmi.RemoteException,jvn.JvnException 
	{
		JvnObject obj = this.jvnObjectsCache.get(joi);
		if(obj == null)
			throw new JvnException("Objet JVN inexistant dans le cache serveur");
		obj.jvnInvalidateReader();
	};

	/**
	 * Invalidate the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized Serializable jvnInvalidateWriter(int joi) throws java.rmi.RemoteException,jvn.JvnException 
	{
		JvnObject obj = this.jvnObjectsCache.get(joi);
		if(obj == null)
			throw new JvnException("Objet JVN inexistant dans le cache serveur");
		Serializable o = obj.jvnInvalidateWriter();
		this.jvnObjectsCache.get(joi).setSerializedObject(o);
		return o;
	};

	/**
	 * Reduce the Write lock of the JVN object identified by id 
	 * @param joi : the JVN object id
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized Serializable jvnInvalidateWriterForReader(int joi) throws java.rmi.RemoteException,jvn.JvnException 
	{
		JvnObject obj = this.jvnObjectsCache.get(joi);
		if(obj == null)
			throw new JvnException("Objet JVN inexistant dans le cache serveur");
		Serializable o = obj.jvnInvalidateWriterForReader();
		this.jvnObjectsCache.get(joi).setSerializedObject(o);
		return o;
	};

}


