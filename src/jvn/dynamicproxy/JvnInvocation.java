package jvn.dynamicproxy;

import java.lang.reflect.Method;

import jvn.JvnObject;

public class JvnInvocation 
{
	private JvnObject jvnobj;
	private Method method;
	private Object[] args;
	
	public JvnInvocation(JvnObject obj, Method method, Object[] args)
	{
		this.jvnobj = obj;
		this.method = method;
		this.args = args;
	}
	
	public JvnObject getJvnObj() {
		return jvnobj;
	}

	public Method getMethod() {
		return method;
	}

	public Object[] getArgs() {
		return args;
	}
}
