package cc.bebop.spraydigital.network;

import java.io.File;
import java.io.IOException;

import com.harrison.lee.twitpic4j.TwitPic;
import com.harrison.lee.twitpic4j.TwitPicResponse;
import com.harrison.lee.twitpic4j.exception.TwitPicException;

/*
 * 
 * 
 * twitpic interface
 * 
 * 
 * 
 */
public class TwitpicService
{
	/*
	private String user;
	private String pass;
	*/
	
	private String text = "undefined text";
	
	private static TwitPic req;

	public TwitpicService(String user, String pass, String text)
	{
		/*
		 * connect
		 * 
		 */
		this.text = text;
		req = new TwitPic(user, pass);
		
		System.err.println("user = " + user);
		System.err.println("pass = " + pass);
		System.err.println("text = " + text);
		
	}

	/*
	 * 
	 * 
	 * 
	 * 
	 * getters and setters
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public String getText()
        {
        	return text;
        }

	public void setText(String text)
        {
        	this.text = text;
        }

	/*
	 * 
	 * 
	 * 
	 * 
	 * send
	 * 
	 * 
	 * 
	 * 
	 * 
	 */
	public void send(byte[] data)
	{
		TwitPicResponse res = null;

		/*
		 * post stuff
		 *
		 */
		try
		{
			res = req.uploadAndPost(data, text);
		}
		
		catch (IOException e)
		{
			e.printStackTrace();
		}

		catch (TwitPicException e)
		{
			e.printStackTrace();
		}

		/*
		 * dump response to console
		 *
		 */
		if(res != null)
			res.dumpVars();
	}

	public void send(File fp)
	{
		TwitPicResponse res = null;

		/*
		 * post stuff
		 *
		 */
		try
		{
			res = req.uploadAndPost(fp, text);
		}

		catch (IOException e)
		{
			e.printStackTrace();
		}

		catch (TwitPicException e)
		{
			e.printStackTrace();
		}

		/*
		 * dump response to console
		 *
		 */
		if(res != null)
			res.dumpVars();
	}
}