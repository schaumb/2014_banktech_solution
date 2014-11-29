package communication;

import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.io.InputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;
import org.json.JSONTokener;
import org.json.JSONException;
import container.Galaxy;
import container.MySpaceShips;
import container.TheySpaceShips;

public class Communication
{
	private String domain;
	private String encoded;
	private Long lastSended = System.currentTimeMillis();
	private Integer minWait = 101;

	public Communication(String url, String auth)
	{
		this.domain = url;
		this.encoded = Base64.getEncoder().encodeToString(auth.getBytes());

		ping();
	}

	private JSONObject buildConnection(String url, String method, HashMap<String,String> params) throws JSONException
	{
		try
		{
			while(System.currentTimeMillis() - lastSended < minWait)
			{
				Thread.sleep(minWait - System.currentTimeMillis() + lastSended);
			}
			String outParams = paramsToString(params);
			HttpURLConnection connection = (HttpURLConnection)(new URL(domain + url).openConnection());

			System.out.print(connection.getURL() + (outParams==null?"":" ?" + outParams));

			connection.setRequestProperty  ("Authorization", "Basic " + encoded);
			connection.setRequestMethod(method);
			connection.setUseCaches(false);
			connection.setDoOutput(true);

			if( outParams != null )
			{
				byte[] postDataBytes = outParams.getBytes("UTF-8");
				connection.setDoInput(true);
				connection.setRequestProperty("Content-Length", String.valueOf(postDataBytes.length));
				connection.getOutputStream().write(postDataBytes);
			}

			connection.connect();

			System.out.println(" - resp : " + connection.getResponseCode() + " " + connection.getResponseMessage());

			switch( connection.getResponseCode() )
			{
				case 503 : System.out.println("503 - end of game"); System.exit(0);
				case 403 : System.out.println("403 - quick sending"); break;
				case 423 : System.out.println("423 - stucked :("); break;
			}

			InputStream result = connection.getInputStream();

			lastSended = System.currentTimeMillis();

			return new JSONObject(
					new JSONTokener(
							new BufferedReader(
									new InputStreamReader(result))));
		}
		catch(ConnectException ex)
		{
			lastSended = System.currentTimeMillis();
			System.err.println("Catched ConnectException (let's try again) : " + ex.toString() );
			return buildConnection( url , method , params );
		}
		catch(IOException ex)
		{
			++minWait;
			lastSended = System.currentTimeMillis();
			System.err.println("Catched IOException (let's try again after "+minWait+" ms) : " + ex.toString() );
			return buildConnection( url , method , params );
		}
		catch (InterruptedException ex)
		{
			System.err.println("Catched InterruptedException (let's try again) : " + ex.toString() );
			return buildConnection( url , method , params );
		}
	}

	private String paramsToString(HashMap<String,String> params) throws UnsupportedEncodingException
	{
		if(params == null) return null;

		StringBuilder postData = new StringBuilder();
		for (Map.Entry<String,String> param : params.entrySet())
		{
			if (postData.length() != 0) postData.append('&');
			postData.append(URLEncoder.encode(param.getKey(), "UTF-8"));
			postData.append('=');
			postData.append(URLEncoder.encode(String.valueOf(param.getValue()), "UTF-8"));
		}
		return postData.toString();
	}

	private void ping()
	{
		try
		{
			buildConnection("/JavaChallenge2/rest/ping", "GET", null);
		}
		catch (JSONException e)
		{
			// that's ok, we are expecting this
		}
	}

	public Galaxy getGalaxy()
	{
		try
		{
			return new Galaxy(buildConnection("/JavaChallenge2/rest/getGalaxy", "GET", null));
		}
		catch (JSONException e)
		{
			System.out.println("JsonBug getGalaxy");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public MySpaceShips whereIs()
	{
		try
		{
			return new MySpaceShips(buildConnection("/JavaChallenge2/rest/whereIs", "GET", null));
		}
		catch (JSONException e)
		{
			System.out.println("JsonBug whereIs");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public TheySpaceShips whereAre()
	{
		try
		{
			return new TheySpaceShips(buildConnection("/JavaChallenge2/rest/whereAre", "GET", null));
		}
		catch (JSONException e)
		{
			System.out.println("JsonBug whereAre");
			e.printStackTrace();
			System.exit(1);
		}
		return null;
	}

	public Integer go(final String planetName, final Integer shipNum)
	{
		try
		{
			JSONObject res = buildConnection("/JavaChallenge2/rest/go", "POST",
					new HashMap<String,String>(){
				private static final long serialVersionUID = 1L;
					{ put("planetName",planetName);
					  put("shipNum",shipNum.toString());
					}} );

			switch(res.getString("status"))
			{
			case "MOVING" :
				return res.getInt("arriveAfterMs") + 1;
			default:
				System.out.println("Logical problem in go function -" +
						" destiny : " + planetName +
						" shipNum : " + shipNum +
						" but got code " + res.getString("status"));
				break;
			}
		}
		catch(JSONException e)
		{
			System.out.println("Respond problem in go function -" +
						" destiny : " + planetName +
						" shipNum : " + shipNum +
						" but got JSONException "+ e.toString());
		}
		return -1; // exception - we are expecting bomb
	}

	public Integer pickPackage(final Integer packageId, final Integer shipNum)
	{
		try
		{
			JSONObject res = buildConnection("/JavaChallenge2/rest/pickPackage", "POST",
					new HashMap<String,String>(){
				private static final long serialVersionUID = 1L;
					{ put("packageId",packageId.toString());
					  put("shipNum",shipNum.toString());
					}} );

			switch(res.getString("status"))
			{
			case "PACKAGE_PICKED" :
				return res.getInt("remainingCapacity");
			default:
				System.out.println("Logical problem in pickPackage function -" +
						" packageId : " + packageId +
						" shipNum : " + shipNum +
						" but got code " + res.getString("status"));
				break;
			}
		}
		catch(JSONException e)
		{
			System.out.println("Respond problem in pickPackage function -" +
					" packageId : " + packageId +
					" shipNum : " + shipNum +
					" but got JSONException "+ e.toString());
		}
		return -1;
	}

	public Integer dropPackage(final Integer shipNum)
	{

		try
		{
			JSONObject res = buildConnection("/JavaChallenge2/rest/dropPackage", "POST",
					new HashMap<String,String>(){
				private static final long serialVersionUID = 1L;
					{ put("shipNum",shipNum.toString()); }} );

			switch(res.getString("status"))
			{
			case "PACKAGE_DROPPED" :
				return res.getInt("scoreIncrease");
			default:
				System.out.println("Logical problem in dropPackage function -" +
						" shipNum : " + shipNum +
						" but got code " + res.getString("status"));
				break;
			}
		}
		catch(JSONException e)
		{
			System.out.println("Respond problem in dropPackage function -" +
					" shipNum : " + shipNum +
					" but got JSONException "+ e.toString());
		}
		return 0;
	}

	public Integer installMine(final String planetName, final Integer shipNum)
	{
		try
		{
			JSONObject res = buildConnection("/JavaChallenge2/rest/installMine", "POST",
					new HashMap<String,String>(){
				private static final long serialVersionUID = 1L;
					{ put("planetName",planetName);
					  put("shipNum",shipNum.toString());
					}} );

			switch(res.getString("status"))
			{
			case "INSTALLED" :
				return res.getInt("remaining");
			default:
				System.out.println("Logical problem in installMine function -" +
						" destiny : " + planetName +
						" shipNum : " + shipNum +
						" but got code " + res.getString("status"));
				break;
			}
		}
		catch(JSONException e)
		{
			System.out.println("Respond problem in installMine function -" +
						" destiny : " + planetName +
						" shipNum : " + shipNum +
						" but got JSONException "+ e.toString());
		}
		return -1; // exception - we are expecting bomb
	}

}
