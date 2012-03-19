package cc.bebop.spraydigital.network;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import com.harrison.lee.twitpic4j.TwitPic;
import com.harrison.lee.twitpic4j.TwitPicResponse;
import com.harrison.lee.twitpic4j.exception.TwitPicException;

/**
 * Act as interface to Twitpic service.
 * 
 * @author IGOrrrr
 *
 */
public class TwitpicService implements Runnable
{
	private static TwitpicService instance; // we are singleton
	private static Thread thread; // service thread
	private static boolean done; // done?
	
	private TwitPic tp; // Twitpic
	private LinkedList<Object> queue; // queue
	
//	private String user; // username
//	private String pass; // password
	
	private String text; // post text
	private long delay;
	
	/////////////////
	// Constructor //
	/////////////////

	/**
	 * @param user
	 * @param pass
	 * @param text to send along teh picture
	 * @param delay between uploads
	 */
	private TwitpicService(String user, String pass, String text, long delay)
	{
//		this.user = user;
//		this.pass = pass;
		this.text = text;
		this.delay = delay;

		queue = new LinkedList<Object>();
		tp = new TwitPic(user, pass);
		
		System.err.println("user = " + user);
		System.err.println("pass = " + pass);
		System.err.println("text = " + text);
	}
	
	/**
	 * @return instance
	 */
	public static TwitpicService getInstance()
	{
		return instance;
	}
	
	/////////////////////////////////////
	// Initialization and finalization //
	/////////////////////////////////////
	
	/**
	 * Initialize teh service.
	 * 
	 * @param user
	 * @param pass
	 * @param text to send along teh picture
	 * @param delay between uploads
	 */
	public static void init(String user, String pass, String text, long delay)
	{
		instance = new TwitpicService(user, pass, text, delay);
		thread = new Thread(instance);
		thread.start();
	}
	
	/**
	 * Finalize teh service.
	 * 
	 * @throws InterruptedException
	 */
	public void fini() throws InterruptedException
	{
		done = true;
		
		thread.interrupt();
		
		thread.join();
		instance = null;
	}
	
	//////////
	// Twit //
	//////////

	/**
	 * Twit pic.
	 * 
	 * @param pic to be twitted.
	 * @throws IOException
	 * @throws TwitPicException
	 */
	private synchronized void twit(Object pic) throws IOException, TwitPicException
	{
		TwitPicResponse res;
		
		System.err.println("twitin");
		
		// NOTE: this should absolutely be true
		assert (pic instanceof byte[] || pic instanceof File);

		res = null;

		if (pic instanceof byte[])
			res = tp.uploadAndPost((byte[]) pic, text);

		else if (pic instanceof File)
			res = tp.uploadAndPost((File) pic, text);

		if (res != null)
			res.dumpVars();
	}

	/**
	 * Twit pic.
	 * 
	 * @param pic to be twitted.
	 * @throws IOException
	 * @throws TwitPicException
	 */
	public void twit(byte[] pic) throws IOException, TwitPicException
	{
		twit((Object) pic);
	}
	
	/**
	 * Twit pic.
	 * 
	 * @param pic to be twitted.
	 * @throws IOException
	 * @throws TwitPicException
	 */
	public void twit(File pic) throws IOException, TwitPicException
	{
		twit((Object) pic);
	}
	
	///////////
	// Queue //
	///////////
	
	/**
	 * Queue pic to be twitted by thread.
	 * 
	 * @param pic to be twitted.
	 */
	private void queue(Object pic)
	{
		synchronized (this) {
			queue.add(pic);
		}
	}
	
	/**
	 * Queue pic to be twitted by thread.
	 * 
	 * @param pic to be twitted.
	 */	
	public void queue(byte[] pic)
	{
		queue((Object) pic);
	}
	
	/**
	 * Queue pic to be twitted by thread.
	 * 
	 * @param pic to be twitted.
	 */
	public void queue(File pic)
	{
		queue((Object) pic);
	}

	//////////////
	// Runnable //
	//////////////
	
	@Override
	public void run()
	{
		while (!done) {
			Object pic;
			
			synchronized (queue) {
				pic = queue.peek();
			}
			
			try {
				
				if (pic != null) {
					twit(pic);
				
					synchronized (queue) {
						queue.poll();
					}
				}
			}
			
			catch (IOException e) {
				// keep retrying
			}
			
			catch (TwitPicException e) {
				// keep retrying
			}
			
			// avoid hurting teh server
			try {
				Thread.sleep(delay);
			}
			
			catch (InterruptedException e) {
				// this will simply cancel sleeping
			}
		}
	}
}