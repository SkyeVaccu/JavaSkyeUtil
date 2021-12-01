package Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Skye logger
 * singleton object
 */
public class SkyeLogger {
    //the singleton logger
    private volatile static Logger logger;

    private SkyeLogger(){}

    public static Logger getLogger() {
        if (logger == null) {
            synchronized (SkyeLogger.class) {
                if (logger == null) {
                    logger = LoggerFactory.getLogger(SkyeLogger.class);
                }
            }
        }
        return logger;
    }
}
