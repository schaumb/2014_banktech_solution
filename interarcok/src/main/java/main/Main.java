package main;
import java.util.Base64;

import logic.MoverClass;
import communication.Communication;
import communication.Loggers;

public class Main
{

	public static void main(String[] args)
	{
		if( args.length < 3 )
		{
			System.out.println("Bad parameters (url user pass)");
			return;
		}

		try
		{
			new Loggers();
			Communication.domain = args[0];
			Communication.encoded = Base64.getEncoder().encodeToString((args[1]+':'+args[2]).getBytes());

			Communication.ping();

			new MoverClass().Go();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}