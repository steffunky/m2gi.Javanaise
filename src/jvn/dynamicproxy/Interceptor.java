package jvn.dynamicproxy;

public interface Interceptor 
{
	public Object invoke(JvnInvocation invocation) throws InvocationException;
}
