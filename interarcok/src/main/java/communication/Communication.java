package communication;

import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import container.Galaxy;

public class Communication
{
	private String domain;
	private String encoded;
	private Long lastSended = System.currentTimeMillis();

	public class EndOfGameException extends Error
	{
		private String message;

		public EndOfGameException(String string)
		{
			message = string;
		}

		public String getMessage()
		{
			return message;
		}

		private static final long serialVersionUID = 1L;

	}

	public Communication(String url, String auth)
	{
		this.domain = url;
		this.encoded = Base64.getEncoder().encodeToString(auth.getBytes());

		ping();
	}

	private void buildConnection(HttpURLConnection connection, String method, String ifWeWrite) throws Exception
	{
		while(System.currentTimeMillis() - lastSended < 500)
		{
			Thread.sleep(500 - System.currentTimeMillis() + lastSended);
		}

		connection.setRequestProperty  ("Authorization", "Basic " + encoded);
		connection.setRequestMethod(method);
		connection.setUseCaches(false);
		connection.setDoOutput(true);

		if( ifWeWrite != null )
		{
			byte[] postDataBytes = ifWeWrite.getBytes("UTF-8");
			connection.setDoInput(true);
			connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
			connection.getOutputStream().write(postDataBytes);
		}

		connection.connect();
		System.out.print(connection.getURL() + (ifWeWrite==null?"":" ?" + ifWeWrite));

		System.out.println(" - resp : " + connection.getResponseCode() + " " + connection.getResponseMessage());

		switch( connection.getResponseCode() )
		{
			case 503 : game_over("END OF GAME");
			case 403 : game_over("MAYBE ANOTHER?");
		}

		lastSended = System.currentTimeMillis();

	}

	private JSONObject getStuff(String url)
	{
		try
		{
			HttpURLConnection connection = (HttpURLConnection)(new URL(domain + url).openConnection());
			buildConnection(connection, "GET", null);
			return new JSONObject(new JSONTokener(new BufferedReader(new InputStreamReader(connection.getInputStream()))));
		}
		catch (JSONException e)
		{
			System.err.println("catched: " + e.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}
	private JSONObject sendStuff(String url, HashMap<String,String> params)
	{
		try
		{
			HttpURLConnection connection = (HttpURLConnection)(new URL(domain + url).openConnection());

			StringBuilder postData = new StringBuilder();
			for (Map.Entry<String,String> param : params.entrySet())
			{
				if (postData.length() != 0) postData.append('&');
				postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
				postData.append('=');
				postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
			}

			buildConnection(connection, "POST", postData.toString());

			return new JSONObject(new JSONTokener(new BufferedReader(new InputStreamReader(connection.getInputStream()))));
		}
		catch (JSONException e)
		{
			System.err.println("catched: " + e.toString());
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public void game_over(String message)
	{
		throw new EndOfGameException("Exit - " + message);
	}

	private void ping()
	{
		getStuff("/JavaChallenge1/rest/ping");
	}

	public Galaxy getGalaxy()
	{
		return new Galaxy(getStuff("/JavaChallenge1/rest/getGalaxy"));
	}

	public /*??*/ JSONObject whereIs()
	{
		/*JSONObject res =*/ return getStuff("/JavaChallenge1/rest/whereIs");
	}

	public Integer go(final String planetName)
	{
		JSONObject res = sendStuff("/JavaChallenge1/rest/go", new HashMap<String,String>(){
			private static final long serialVersionUID = 1L;
		{ put("planetName",planetName); }} );

		try
		{
			switch(res.getString("status"))
			{
			case "ALREADY_MOVING" :
			case "NOTHING_TO_DO" :
			case "UNKNOWN_PLANET" :
				game_over("Logical problem in go function - destiny : " + planetName +
						" but getted code " + res.getString("status"));
			case "MOVING" :
				return res.getInt("arriveAfterMs");
			}
		}
		catch(JSONException e)
		{
			game_over("Respond problem in go function - destiny : " + planetName +
					" but got JSONException "+ e.toString());
		}
		return -1;
	}

	public Integer pickPackage(final Integer packageId)
	{
		JSONObject res = sendStuff("/JavaChallenge1/rest/pickPackage", new HashMap<String,String>(){
			private static final long serialVersionUID = 1L;
		{ put("packageId",packageId.toString()); }} );

		try
		{
			switch(res.getString("status"))
			{
			case "NOT_FOUND" :
			case "LIMIT_EXCEEDED" :
			case "USER_NOT_ON_THE_PLANET" :
				game_over("Logical problem in pickPackage function - packageId : " + packageId +
						" but getted code " + res.getString("status"));
			case "PACKAGE_PICKED" :
				return res.getInt("remainingCapacity");
			}
		}
		catch(JSONException e)
		{
			game_over("Respond problem in pickPackage function - packageId : " + packageId +
					" but got JSONException "+ e.toString());
		}
		return 0;
	}

	public Integer dropPackage(final Integer packageId)
	{
		JSONObject res = sendStuff("/JavaChallenge1/rest/dropPackage", new HashMap<String,String>(){
			private static final long serialVersionUID = 1L;
		{ put("packageId",packageId.toString()); }} );

		try
		{
			switch(res.getString("status"))
			{
			case "NOT_WITH_USER" :
			case "NOT_AT_DESTINATION" :
				game_over("Logical problem in dropPackage function - packageId : " + packageId +
						" but getted code " + res.getString("status"));
			case "PACKAGE_DROPPED" :
				return res.getInt("scoreIncrease");
			}
		}
		catch(JSONException e)
		{
			game_over("Respond problem in dropPackage function - packageId : " + packageId +
					" but got JSONException "+ e.toString());
		}
		return 0;
	}
}