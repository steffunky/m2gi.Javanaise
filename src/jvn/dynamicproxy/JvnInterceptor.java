package jvn.dynamicproxy;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import jvn.JvnException;
import jvn.JvnObject;
import jvn.dynamicproxy.JvnAction.ActionType;

public class JvnInterceptor implements Interceptor 
{
	@Override
	public Object invoke(JvnInvocation invocation) throws InvocationException 
	{
		try {
			Method m = invocation.getMethod();
			if(m.isAnnotationPresent(JvnAction.class))
			{
				ActionType actionType = m.getAnnotation(JvnAction.class).actionType();
				JvnObject jvnObj = invocation.getJvnObj();
				
				switch(actionType)
				{
					case READ:
						jvnObj.jvnLockRead();
						break;
					case WRITE:
						jvnObj.jvnLockWrite();
						break;
					default:
						throw new InvocationException("L'annotation de la méthode dispose d'un type d'action non pris en charge");
				}
				
				Object result = m.invoke(jvnObj.jvnGetObjectState(), invocation.getArgs());
				jvnObj.jvnUnLock();
				return result;
			}
			else throw new InvocationException("Aucune annotation trouvée sur la méthode");
		
		} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | JvnException e) {
			e.printStackTrace();
			throw new InvocationException("Erreur lors de la tentative d'invocation");
		}
	}
}
