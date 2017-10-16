/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.io.Serializable;


public class JvnCoordImpl extends UnicastRemoteObject implements JvnRemoteCoord {
	
	private static final long serialVersionUID = 1L;
	
	private static JvnRemoteCoord jvnCoordInstance = null;
	
	private int currentObjectId;
	
	/**
	 * Objets JVN stockés par nom dans le cache du coordinateur
	 */
	private Hashtable<String, JvnObject> jvnObjects;
	
	/**
	 * Relations NOM - Identifiant des objets JVN
	 */
	private Hashtable<Integer, String> jvnReferences;
	
	/**
	 * Serveur en écriture sur les objets JVN identifiés
	 */
	private Hashtable<Integer, JvnRemoteServer> jvnWriteMode;
	
	/**
	 * Serveurs en lecture sur les objets JVN identifiés 
	 */
	private Hashtable<Integer, List<JvnRemoteServer>> jvnReadMode;

	/**
	 * Default constructor
	 * @throws JvnException
	 **/
	private JvnCoordImpl() throws Exception {
		// to be completed
		this.currentObjectId = 0;
		this.jvnObjects = new Hashtable<String, JvnObject>();
		this.jvnReferences = new Hashtable<Integer, String>();
		this.jvnWriteMode = new Hashtable<Integer, JvnRemoteServer>();
		this.jvnReadMode = new Hashtable<Integer, List<JvnRemoteServer>>();
	}
	
	/**
	 * Singleton
	 * @return Instance du coordinateur, null dans le cas d'erreur
	 */
	public static JvnRemoteCoord getInstance()
	{
		if(jvnCoordInstance == null)
		{
			try {
				jvnCoordInstance = new JvnCoordImpl();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return jvnCoordInstance;
	}

	/**
	 *  Allocate a NEW JVN object id (usually allocated to a 
	 *  newly created JVN object)
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized int jvnGetObjectId()
			throws java.rmi.RemoteException,jvn.JvnException {
		// to be completed 
		return ++this.currentObjectId;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @param js  : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js)
			throws java.rmi.RemoteException,jvn.JvnException{
		// to be completed
		
		this.jvnObjects.put(jon, jo);
		this.jvnReferences.put(jo.jvnGetObjectId(), jon);
		
		// Ajout de l'information "serveur en écriture sur l'objet"
		this.jvnWriteMode.put(jo.jvnGetObjectId(), js);
	}

	/**
	 * Get the reference of a JVN object managed by a given JVN server 
	 * @param jon : the JVN object name
	 * @param js : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js)
			throws java.rmi.RemoteException,jvn.JvnException{
		// to be completed 
		JvnObject o = this.jvnObjects.get(jon);
		if ( o != null) {
			o.jvnSetFree();
		}
		return o;
	}

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException{
		// to be completed
		Serializable result;
		
		/** ECRITURE **/
		// Obtention du serveur en écriture sur l'objet
		JvnRemoteServer server = this.jvnWriteMode.get(joi);
		
		// Objet en écriture sur un serveur distant
		if(server != null)
		{
			result = server.jvnInvalidateWriterForReader(joi); // Demande de libération du verrou en écriture pour lecture
			this.jvnWriteMode.remove(joi); // Objet plus en écriture au niveau du serveur distant
			this.jvnObjects.get(this.jvnReferences.get(joi)).setSerializedObject(result); // Mise à jour de l'objet dans le cache
		}
		else 
			result = this.jvnObjects.get(this.jvnReferences.get(joi));
		
		List<JvnRemoteServer> serversReadingObject = this.jvnReadMode.get(joi);
		if(serversReadingObject == null)
			serversReadingObject = new ArrayList<JvnRemoteServer>();
		
		serversReadingObject.add(js); // Ajout du serveur dans la liste des serveurs en lecture sur l'objet
		this.jvnReadMode.put(joi, serversReadingObject); // Mise à jour de la liste
		System.out.println("Result read : " + (result == null));
		return result;
	}

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException
	{
		// to be completed
		Serializable result = null;
		
		/** ECRITURE **/
		// Obtention du serveur en écriture sur l'objet
		JvnRemoteServer server = this.jvnWriteMode.get(joi);
		
		// Objet en écriture sur un serveur distant
		if(server != null)
		{
			result = server.jvnInvalidateWriter(joi); // Demande de libération du verrou en écriture
			this.jvnWriteMode.remove(joi); // Objet plus en écriture au niveau du serveur distant
			this.jvnObjects.get(this.jvnReferences.get(joi)).setSerializedObject(result); // Mise à jour de l'objet dans le cache
			this.jvnWriteMode.put(joi, js); // Mise à jour du serveur en écriture sur l'objet
		}
		else if(this.jvnReferences.contains(joi)) // Si l'objet est enregistré dans le cache
		{
			String ref = this.jvnReferences.get(joi);
			result = this.jvnObjects.get(ref);
			this.jvnWriteMode.put(joi, js);
		}
		else 
		{
			System.out.println("Object no cached");
		}
		
		/** LECTURE **/
		List<JvnRemoteServer> serversReadingObject = this.jvnReadMode.get(joi);
		if(serversReadingObject != null)
		{
			for(JvnRemoteServer s : serversReadingObject)
				s.jvnInvalidateReader(joi);
			
			this.jvnReadMode.remove(joi); // On retire de la liste tous les serveurs qui étaient en lecture sur l'objet
		}
		System.out.println("Result write : " + (result == null));

		return result;
	}

	/**
	 * A JVN server terminates
	 * @param js  : the remote reference of the server
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException {

		/** SUPPRESSION DE LA LISTE DE LECTURE **/
		for(List<JvnRemoteServer> servers : this.jvnReadMode.values())
		{
			if(servers.contains(js))
				servers.remove(js);
		}
		
		/** SUPPRESSION DE LA LISTE D'ECRITURE **/
		this.jvnWriteMode.values().remove(js);
	}
}


