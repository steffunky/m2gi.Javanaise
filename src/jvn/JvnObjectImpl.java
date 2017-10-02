package jvn;

import java.io.Serializable;

public class JvnObjectImpl implements JvnObject {

	private static final long serialVersionUID = 1L;
	private int id;
	private Serializable serializedObject;
	
	@SuppressWarnings("unused")
	private LockState state;

	protected JvnObjectImpl(Serializable object, Integer id)
	{
		this.id = id; 
		this.serializedObject = object;
		this.state = LockState.NoLock;
	}
	
	@Override
	public void jvnLockRead() throws JvnException {
		if(this.state == LockState.Write || this.state == LockState.WriteCached)
			this.state = LockState.ReadWriteCached;
		else this.state = LockState.Read;
	}

	@Override
	public void jvnLockWrite() throws JvnException {
		
		this.state = LockState.Write;
		
	}

	@Override
	public void jvnUnLock() throws JvnException {
		this.state = LockState.NoLock;
		
	}

	@Override
	public int jvnGetObjectId() throws JvnException {
		// TODO Auto-generated method stub
		return this.id;
	}

	@Override
	public Serializable jvnGetObjectState() throws JvnException {
		// TODO Auto-generated method stub
		return this.serializedObject;
	}

	@Override
	public void jvnInvalidateReader() throws JvnException {
		this.state = LockState.ReadCached;	
	}

	@Override
	public Serializable jvnInvalidateWriter() throws JvnException {
		this.state = LockState.WriteCached;
		return this.jvnGetObjectState();
	}

	@Override
	public Serializable jvnInvalidateWriterForReader() throws JvnException {
		this.state = LockState.ReadWriteCached;
		return this.jvnGetObjectState();
	}

}
