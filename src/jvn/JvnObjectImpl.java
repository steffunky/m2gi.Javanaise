package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {

	private static final long serialVersionUID = 1L;
	private int id;
	private Serializable serializedObject;
	private LockState state;

	protected JvnObjectImpl(Serializable object, Integer id)
	{
		this.id = id; 
		this.serializedObject = object;
		this.state = LockState.Write;
	}
	
	@Override
	public void setSerializedObject(Serializable serializedObject) {
		this.serializedObject = serializedObject;
	}

	@Override
	public void jvnLockRead() throws JvnException 
	{
		switch(this.state)
		{
			case Write:
				// Nothing
				break;
			case WriteCached:
				this.state = LockState.ReadWriteCached;
				break;
			case ReadWriteCached:
				// Nothing
				break;
			case Read:
				// Nothing
				break;
			case ReadCached:
				this.state = LockState.Read;
				break;
			case NoLock:
				JvnLocalServer server = JvnServerImpl.jvnGetServer(); // Obtention de la ref serveur
				this.setSerializedObject(server.jvnLockRead(this.jvnGetObjectId()));
				this.state = LockState.Read;
				break;
			default:
				throw new JvnException("Etat non valide \"" + this.state + "\"");
		}
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		
		// Obtention du serveur parent
		switch(this.state)
		{
			case Write:
				// Nothing
				break;
			case WriteCached:
			case ReadWriteCached:
				this.state = LockState.Write;
				break;
			case Read:
			case ReadCached:
			case NoLock:
				JvnLocalServer server = JvnServerImpl.jvnGetServer(); // Obtention de la ref serveur
				int joi = this.jvnGetObjectId();
				this.setSerializedObject(server.jvnLockWrite(joi));
				this.state = LockState.Write;
				break;
			default:
				throw new JvnException("Etat non valide \"" + this.state + "\"");
		}
	}

	@Override
	public synchronized void jvnUnLock() throws JvnException 
	{
		switch(this.state)
		{
			case Write:
				this.state = LockState.WriteCached;
				break;
			case WriteCached:
				// Nothing
				break;
			case ReadWriteCached:
				this.state = LockState.WriteCached; // Libération du read seulement
				break;
			case Read:
				this.state = LockState.ReadCached;
			case ReadCached:
				// Nothing
				break;
			case NoLock:
				// Nothing
				break;
			default:
				throw new JvnException("Etat non valide \"" + this.state + "\"");
		}
		this.notify();
		
	}

	@Override
	public int jvnGetObjectId() throws JvnException {
		return this.id;
	}

	@Override
	public Serializable jvnGetObjectState() throws JvnException {
		return this.serializedObject;
	}

	@Override
	public void jvnInvalidateReader() throws JvnException 
	{
		try 
		{
			switch(this.state)
			{
				case Read:
					this.wait(); // Mise en attente de notification
					this.state = LockState.NoLock;
					break;
				case Write:
					throw new JvnException("Cas invalide");
				case ReadCached:
					this.state = LockState.NoLock;
					break;
				case WriteCached:
					throw new JvnException("Cas invalide");
				case ReadWriteCached:
					this.wait();
					this.state = LockState.NoLock;
					break;
				case NoLock:
					// Nothing
					break;
				default:
					throw new JvnException("Etat non valide \"" + this.state + "\"");
				
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			throw new JvnException(e.getMessage());
		}
	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException 
	{
		System.out.println("InvalidateWriter current state : " + this.state);
		try 
		{
			switch(this.state)
			{
				case Read:
					// Nothing
					break;
				case Write:
					this.wait(); // Mise en attente de notification
					this.state = LockState.NoLock;
					break;
				case ReadCached:
					// Nothing
					break;
				case WriteCached:
					this.state = LockState.NoLock;
					break;
				case ReadWriteCached:
					this.state = LockState.NoLock;
					break;
				case NoLock:
					// Nothing
					break;
				default:
					throw new JvnException("Etat non valide \"" + this.state + "\"");
				
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			throw new JvnException(e.getMessage());
		}
		System.out.println("InvalidateWriter final state : " + this.state);
		return this.jvnGetObjectState();
	}

	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException 
	{
		try 
		{
			switch(this.state)
			{
				case Read:
					// Nothing
					break;
				case Write:
					this.wait(); // En attente de notification
					this.state = LockState.ReadCached;
					break;
				case ReadCached:
					this.state = LockState.ReadCached;
					break;
				case WriteCached:
					this.state = LockState.ReadCached;
					break;
				case ReadWriteCached:
					// Nothing
					break;
				case NoLock:
					this.state = LockState.ReadCached;
					break;
				default:
					throw new JvnException("Etat non valide \"" + this.state + "\"");
				
			}
		}
		catch(InterruptedException e)
		{
			e.printStackTrace();
			throw new JvnException(e.getMessage());
		}
		
		return this.jvnGetObjectState();
	}

	@Override
	public void jvnSetFree() {
		this.state = LockState.NoLock;
	}

}
