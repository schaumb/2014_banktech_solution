package main;
import java.util.Base64;

import logic.MoverClass;
import communication.Communication;

public class Main
{

	public static void main(String[] args)
	{
		if( args.length < 3 )
		{
			System.out.println("Bad parameters (url user pass)");
			return;
		}
		MoverClass mc = null;
		try
		{
			Communication.domain = args[0];
			Communication.encoded = Base64.getEncoder().encodeToString((args[1]+':'+args[2]).getBytes());

			Communication.ping();

			mc = new MoverClass();
			mc.Go();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}