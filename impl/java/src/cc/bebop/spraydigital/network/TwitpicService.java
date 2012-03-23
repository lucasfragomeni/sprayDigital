package cc.bebop.spraydigital.network;

import java.io.File;
import java.util.LinkedList;

import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
import twitter4j.media.ImageUpload;
import twitter4j.media.ImageUploadFactory;
import twitter4j.media.MediaProvider;

/**
 * Act as interface to Twitpic service.
 * 
 * @author IGOrrrr
 * 
 */
public class TwitpicService implements Runnable {
	private static TwitpicService instance; // we are singleton
	private static Thread thread; // service thread
	private static boolean done; // done?

	Configuration twitConf;
	Twitter twitter;
	TwitterFactory twitFactory;
	ImageUploadFactory twitUpFactory;
	ImageUpload twitImgUp;
	private LinkedList<Object> queue; // queue

	// private String user; // username
	// private String pass; // password

	private String caption; // post caption
	private long delay;

	/////////////////
	// Constructor //
	/////////////////

	/**
	 * Constructor
	 * 
	 * @param consumerKey
	 * @param consumerSecret
	 * @param accessToken
	 * @param accessTokenSecret
	 * @param mediaProviderAPIKey
	 * @param caption
	 * @param delay
	 */
	private TwitpicService(
			String consumerKey,
			String consumerSecret,
			String accessToken,
			String accessTokenSecret,
			String mediaProviderAPIKey,
			String caption,
			long delay
	)
	{
		twitConf = new ConfigurationBuilder()
				.setMediaProviderAPIKey(mediaProviderAPIKey)
				.setOAuthConsumerKey(consumerKey)
				.setOAuthConsumerSecret(consumerSecret)
				.setOAuthAccessToken(accessToken)
				.setOAuthAccessTokenSecret(accessTokenSecret).build();
		
		twitFactory = new TwitterFactory(twitConf);
		twitUpFactory = new ImageUploadFactory(twitConf);

		twitter = twitFactory.getInstance();
		twitImgUp = twitUpFactory.getInstance(MediaProvider.TWITPIC);

		this.caption = caption;
		this.delay = delay;

		queue = new LinkedList<Object>();
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
	 * @param consumerKey
	 * @param consumerSecret
	 * @param accessToken
	 * @param accessTokenSecret
	 * @param mediaProviderAPIKey
	 * @param caption
	 * @param delay
	 */
	public static void init(
			String consumerKey,
			String consumerSecret,
			String accessToken,
			String accessTokenSecret,
			String mediaProviderAPIKey,
			String caption,
			long delay
	)
	{
		instance = new TwitpicService(consumerKey, consumerSecret, accessToken,
				accessTokenSecret, mediaProviderAPIKey, caption, delay);

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
	 * @param pic
	 * @throws TwitterException
	 */
	private synchronized void twit(Object pic) throws TwitterException
	{
		String url;
		Status status;

		System.err.println("twitin");

		// NOTE: this should absolutely be true
		assert (pic instanceof byte[] || pic instanceof File);

		if (pic instanceof File) {
			
			// twitpic
			while (true) {
				try {
					url = twitImgUp.upload((File) pic, caption);
				}
				
				catch (TwitterException e) {
					e.printStackTrace();
					System.out.println("Failed to twitImgUp the image: "
							+ e.getMessage());
					continue;
					// retry forever
				}
				
				System.out.println("Successfully twitpiced at " + url);
				break;
			}
			
			// twit
			while (true) {
				try {
					status = twitter.updateStatus(caption + " " + url);
				}
				
				catch (TwitterException e) {
					e.printStackTrace();
					System.out.println("Failed to twitImgUp the image: "
							+ e.getMessage());
					continue;
					// retry forever
				}
				
				System.out.println("Successfully twited at " +
						status.getSource());
				break;
			}
		}

		else {
			System.err.println("EXPLODE");
			System.exit(-1);
		}
	}

	/**
	 * Twit pic.
	 * 
	 * @param pic
	 * @throws TwitterException
	 */
	public void twit(byte[] pic) throws TwitterException
	{
		twit((Object) pic);
	}

	/**
	 * Twit pic.
	 * 
	 * @param pic
	 * @throws TwitterException
	 */
	public void twit(File pic) throws TwitterException
	{
		twit((Object) pic);
	}

	///////////
	// Queue //
	///////////

	/**
	 * Queue pic to be twitted by thread.
	 * 
	 * @param pic
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
	 * @param pic
	 */
	public void queue(byte[] pic)
	{
		queue((Object) pic);
	}

	/**
	 * Queue pic to be twitted by thread.
	 * 
	 * @param pic
	 */
	public void queue(File pic)
	{
		queue((Object) pic);
	}

	//////////////
	// Runnable //
	//////////////

	/**
	 * Thread routine.
	 */
	@Override
	public void run()
	{
		while (!done) {
			Object pic;

			synchronized (queue) {
				pic = queue.peek();
			}

			if (pic != null) {
				try {
					twit(pic);
				}

				catch (TwitterException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				synchronized (queue) {
					queue.poll();
				}
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