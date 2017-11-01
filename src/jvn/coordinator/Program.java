package jvn.coordinator;

import java.net.MalformedURLException;
import java.rmi.AlreadyBoundException;
import java.rmi.Naming;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;

import jvn.JvnCoordImpl;
import jvn.JvnRemoteCoord;

public class Program 
{
	public static void main(String[] args) throws MalformedURLException, RemoteException, AlreadyBoundException, InterruptedException
	{
		java.rmi.registry.LocateRegistry.createRegistry(1099);
		JvnRemoteCoord coordinator = JvnCoordImpl.getInstance();
		Naming.rebind(JvnRemoteCoord.jvnCoordRemoteIdentfier, coordinator);
	}
}
