/***
 * Sentence class : used for representing the text exchanged between users
 * during a chat application
 * Contact: 
 *
 * Authors: 
 */

package irc;

import java.io.Serializable;

import jvn.dynamicproxy.JvnAction;
import jvn.dynamicproxy.JvnAction.ActionType;

public class Sentence implements ISentence, Serializable {
	private static final long serialVersionUID = 1L;
	String data;

	public Sentence() {
		data = new String("");
	}

	public void write(String text) {
		data = text;
	}
	
	public String read() {
		return data;	
	}

}