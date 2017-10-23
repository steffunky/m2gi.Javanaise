package irc;

import jvn.dynamicproxy.JvnAction;
import jvn.dynamicproxy.JvnAction.ActionType;

public interface ISentence 
{
	@JvnAction(actionType=ActionType.WRITE)
	public void write(String text);
	
	@JvnAction(actionType=ActionType.READ)
	public String read();
}
