package communication;

import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Base64;
import java.util.HashMap;
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

    public Communication(String url, String auth)
    {
        this.domain = url;
        this.encoded = Base64.getEncoder().encodeToString(auth.getBytes());

        ping();
    }

    private void buildConnection(HttpURLConnection connection, String method) throws Exception
    {
		while(System.currentTimeMillis() - lastSended < 500)
		{
		    Thread.sleep(500 - System.currentTimeMillis() + lastSended);
		}

        connection.setRequestProperty  ("Authorization", "Basic " + encoded);
        connection.setRequestMethod(method);
        connection.setUseCaches(false);
        connection.setDoOutput(true);
        connection.connect();
        System.out.println(connection.getURL());
        System.out.println(connection.getResponseCode() + " " + connection.getResponseMessage());

        switch( connection.getResponseCode() )
        {
        	case 503 : game_over("END OF GAME");
        }

        lastSended = System.currentTimeMillis();

    }

    private JSONObject getStuff(String url)
    {
		try
		{
	        HttpURLConnection connection = (HttpURLConnection)(new URL(domain + url).openConnection());
			buildConnection(connection, "GET");
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
    private JSONObject sendStuff(String url, HashMap<String,String> map)
    {
		try
		{
	        HttpURLConnection connection = (HttpURLConnection)(new URL(domain + url).openConnection());

	        for (HashMap.Entry<String, String> entry : map.entrySet())
	        {
	            connection.addRequestProperty(entry.getKey(), entry.getValue());
	        }

	        buildConnection(connection, "POST");

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
		System.out.println("Exit - " + message);
		System.exit(0);
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