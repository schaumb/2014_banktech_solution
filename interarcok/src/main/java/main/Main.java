package main;
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
			mc = new MoverClass(
					new Communication(args[0], args[1]+':'+args[2]) );
			mc.Go();

		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}