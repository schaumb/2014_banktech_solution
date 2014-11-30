package communication;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Loggers
{
    public static Logger comLogger = Logger.getLogger("CommunicationLog");
    public static Logger logLogger = Logger.getLogger("LogicLog");

    static
    {
    	try {
            // This block configure the logger with handler and formatter
    		FileHandler fh = new FileHandler("Communication.log");
            comLogger.addHandler(fh);
            comLogger.setUseParentHandlers(false);
            fh.setFormatter(new SimpleFormatter());

            comLogger.info("Communications");


    		fh = new FileHandler("Logic.log");
    		logLogger.addHandler(fh);
    		logLogger.setUseParentHandlers(false);
            fh.setFormatter(new SimpleFormatter());

            logLogger.info("Logic");

    	} catch (SecurityException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
