package burst;

import java.util.Random;

import irc.ISentence;
import irc.Sentence;
import jvn.JvnException;
import jvn.dynamicproxy.JvnProxy;

public class IrcWorker implements Runnable
{
	private int limit;
	private ISentence sentence;
	private String currentSentence;
	private Random rand;
	private boolean isInterrupted;
	private int index;
	
	public IrcWorker(int index, boolean isInterrupted)
	{
		super();
		this.limit = 10;
		this.index = index;
		this.isInterrupted = isInterrupted;
	}
	
	public void initialize() throws JvnException
	{
		this.sentence = (ISentence) JvnProxy.newInstance(new Sentence(), "IRC", true);
		this.rand = new Random();
		this.currentSentence = this.generateNewString();
	}
	
	@Override
	public void run() 
	{
		try 
		{
			Thread.sleep(100);
		
			while(!this.isInterrupted && --limit > 0)
			{
				Thread.sleep(100);
				if(rand.nextBoolean())
				{
					this.currentSentence = this.sentence.read();
				}
				else
				{
					this.currentSentence = this.generateNewString();
					this.sentence.write(generateNewString());
				}
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}
	
	private String generateNewString()
	{
		return String.valueOf((char)(this.rand.nextInt(255)));
	}
}
