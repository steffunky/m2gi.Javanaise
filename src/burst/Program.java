package burst;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import jvn.JvnException;

public class Program {

	private static final int MAX_CLIENTS = 100;
	private static boolean isInterrupted;
	
	public static void main(String[] args) throws JvnException, InterruptedException 
	{
		isInterrupted = false;
		
		ExecutorService executor = Executors.newFixedThreadPool(10);
		
		for(int i = 0; i < MAX_CLIENTS; i++)
		{
			IrcWorker worker = new IrcWorker(i, isInterrupted);
			worker.initialize();
			
			executor.execute(worker);
		}
		
		executor.shutdown();
		executor.awaitTermination(1, TimeUnit.DAYS);

	    System.exit(0);
	}
}
