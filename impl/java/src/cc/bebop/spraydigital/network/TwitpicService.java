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
	private static final String user = "spamdora666";
	private static final String pass = "caralhada";

	private static TwitPic req;

	static
	{
		/*
		 * connect
		 * 
		 */
		req = new TwitPic(user, pass);
	}

	public static void send(byte[] data)
	{
		TwitPicResponse res = null;

		/*
		 * post stuff
		 *
		 */
		try
		{
			res = req.uploadAndPost(data, "EHLLO WARUDO!!!");
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

	public static void send(File fp)
	{
		TwitPicResponse res = null;

		/*
		 * post stuff
		 *
		 */
		try
		{
			res = req.uploadAndPost(fp, "EHLLO WARUDO!!!");
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