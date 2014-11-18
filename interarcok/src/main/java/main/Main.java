package main;
import logic.MoverClass;
import communication.Communication;
import communication.Communication.EndOfGameException;

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
		catch(EndOfGameException e)
		{
			System.err.println(e.getMessage());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		finally
		{
			if( mc != null )
			{
				System.out.println( "Delivered packages : " + mc.deliver + ", gotted fee : " + mc.sumFee );
			}
			else
			{
				System.exit(1);
			}
		}
	}
}