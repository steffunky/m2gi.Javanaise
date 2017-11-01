package jvn.dynamicproxy;

import java.io.Serializable;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

import jvn.JvnException;
import jvn.JvnObject;
import jvn.JvnServerImpl;

public class JvnProxy implements InvocationHandler
{
	private JvnObject jvnobj;
	private Interceptor interceptor;
	
	// TODO : Multi-interceptor
	
	private JvnProxy(JvnObject obj, Interceptor interceptor)
	{
		this.jvnobj= obj;
		this.interceptor = interceptor;
	}
	
	public static Object newInstance(Object obj, String rmikey) throws JvnException 
	{
		return newInstance(obj, rmikey, false);
	} 
	
	public static Object newInstance(Object obj, String rmikey, boolean createServer) throws JvnException 
	{
		JvnServerImpl js;
		
		if(createServer)
			js = JvnServerImpl.jvnCreateServer();
		else
			js = JvnServerImpl.jvnGetServer();
		
		// TODO : Revoir s'il est raisonnable d'intégrer la gestion serveur dans le Proxy
		JvnObject o = js.jvnLookupObject(rmikey);
		if(o == null)
		{
			o = js.jvnCreateObject((Serializable) obj);
			o.jvnUnLock();
			js.jvnRegisterObject(rmikey, o);
		}
		
		return Proxy.newProxyInstance(obj.getClass().getClassLoader(), obj.getClass().getInterfaces(), new JvnProxy(o, new JvnInterceptor()));
	} 
	
	@Override
	public Object invoke(Object obj, Method method, Object[] args) throws JvnException 
	{ 
		Object res = null;
		JvnInvocation invocation = new JvnInvocation(this.jvnobj, method, args);
		
		try {
			res = interceptor.invoke(invocation);
		} catch (InvocationException e) {
			e.printStackTrace();
			throw new JvnException(e.getMessage());
		}
		
		return res;
	}
}
