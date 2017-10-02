/***
 * JAVANAISE Implementation
 * JvnServerImpl class
 * Contact: 
 *
 * Authors: 
 */

package jvn;

import java.rmi.server.UnicastRemoteObject;
import java.util.Hashtable;
import java.io.Serializable;


public class JvnCoordImpl 	
extends UnicastRemoteObject 
implements JvnRemoteCoord {
	
	private static final long serialVersionUID = 1L;
	
	private static JvnRemoteCoord jvnCoordInstance = null;
	
	private int currentObjectId;
	private Hashtable<String, JvnObject> jvnObjects;

	/**
	 * Default constructor
	 * @throws JvnException
	 **/
	private JvnCoordImpl() throws Exception {
		// to be completed
		this.currentObjectId = 0;
		this.jvnObjects = new Hashtable<String, JvnObject>();
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
		return this.jvnObjects.get(jon);
	}

	/**
	 * Get a Read lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized Serializable jvnLockRead(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException{
		// to be completed
		js.jvnInvalidateReader(joi);
		return null;
	}

	/**
	 * Get a Write lock on a JVN object managed by a given JVN server 
	 * @param joi : the JVN object identification
	 * @param js  : the remote reference of the server
	 * @return the current JVN object state
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized Serializable jvnLockWrite(int joi, JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException{
		// to be completed
		return js.jvnInvalidateWriter(joi);
	}

	/**
	 * A JVN server terminates
	 * @param js  : the remote reference of the server
	 * @throws java.rmi.RemoteException, JvnException
	 **/
	public synchronized void jvnTerminate(JvnRemoteServer js)
			throws java.rmi.RemoteException, JvnException {
		// to be completed
		this.jvnObjects.clear();
		this.jvnObjects = null;
	}
}


