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
	private JvnCoordImpl() throws Exception 
	{
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
	public synchronized int jvnGetObjectId() throws java.rmi.RemoteException,jvn.JvnException 
	{
		return ++this.currentObjectId;
	}

	/**
	 * Associate a symbolic name with a JVN object
	 * @param jon : the JVN object name
	 * @param jo  : the JVN object
	 * @param js  : the remote reference of the JVNServer
	 * @throws java.rmi.RemoteException,JvnException
	 **/
	public synchronized void jvnRegisterObject(String jon, JvnObject jo, JvnRemoteServer js) throws java.rmi.RemoteException,jvn.JvnException
	{
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
	public synchronized JvnObject jvnLookupObject(String jon, JvnRemoteServer js) throws java.rmi.RemoteException,jvn.JvnException
	{
		JvnObject o = this.jvnObjects.get(jon);
		if (o != null) {
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
	public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js) throws java.rmi.RemoteException, JvnException
	{
		Serializable result;
		
		/** ECRITURE **/
		// Obtention du serveur en écriture sur l'objet
		JvnRemoteServer server = this.jvnWriteMode.get(joi);
		
		List<JvnRemoteServer> serversReadingObject = this.jvnReadMode.get(joi);
		
		if(serversReadingObject == null)
			serversReadingObject = new ArrayList<JvnRemoteServer>();
		
		if(server == js)
			throw new JvnException("Tentative de lock Read sur un objet déjà lock par l'objet demandeur...");
		
		// Objet en écriture sur un serveur distant
		if(server != null)
		{
			result = server.jvnInvalidateWriterForReader(joi); // Demande de libération du verrou en écriture pour lecture
			this.jvnWriteMode.remove(joi); // Objet plus en écriture au niveau du serveur distant
			String ref = this.jvnReferences.get(joi);
			this.jvnObjects.get(ref).setSerializedObject(result); // Mise à jour de l'objet dans le cache
			serversReadingObject.add(server); // Ajout du serveur anciennement en écriture, dans la liste des lecteurs
		}
		else 
		{
			String ref = this.jvnReferences.get(joi);
			result = this.jvnObjects.get(ref).jvnGetObjectState();
		}
		
		serversReadingObject.add(js); // Ajout du serveur dans la liste des serveurs en lecture sur l'objet
		this.jvnReadMode.put(joi, serversReadingObject); // Mise à jour de la liste
		
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
		Serializable result = null;
		
		/** ECRITURE **/
		// Obtention du serveur en écriture sur l'objet
		JvnRemoteServer server = this.jvnWriteMode.get(joi);
		
		if(server != null) // S'il existe un serveur en écriture sur l'objet
		{
			result = server.jvnInvalidateWriter(joi); // Demande de libération du verrou en écriture
			this.jvnWriteMode.remove(joi); // Objet plus en écriture au niveau du serveur distant
			String ref = this.jvnReferences.get(joi);
			this.jvnObjects.get(ref).setSerializedObject(result); // Mise à jour de l'objet dans le cache
			this.jvnWriteMode.put(joi, js); // Mise à jour du serveur en écriture sur l'objet
		}
		// Cas ou aucun serveur n'est actuellement en écriture sur l'objet
		
		/** READ **/
		// Invalide tous les lecteurs actuels sur l'objet
		List<JvnRemoteServer> serversReadingObject = this.jvnReadMode.get(joi);
		if(serversReadingObject != null)
		{
			for(JvnRemoteServer s : serversReadingObject)
				s.jvnInvalidateReader(joi);
			
			this.jvnReadMode.remove(joi); // On retire de la liste tous les serveurs qui étaient en lecture sur l'objet
		}
		
		// Mise en écriture de l'objet
		String ref = this.jvnReferences.get(joi);
		if(ref == null)
			throw new JvnException("La demande du vérrou en écriture a été réalisée sur un objet non pris en charge par le coordinateur");
		
		result = this.jvnObjects.get(ref).jvnGetObjectState();
		this.jvnWriteMode.put(joi, js);

		return result;
	}

	/**
	 * A JVN server terminates
	 * @param js  : the remote reference of the server
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized void jvnTerminate(JvnRemoteServer js) throws java.rmi.RemoteException, JvnException 
	{
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


