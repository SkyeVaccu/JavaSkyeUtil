package log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @Description 日志对象
 * @Author Skye
 * @Date 2022/11/25 10:58
 */
public class SkyeLogger {

    // the singleton logger
    private static volatile Logger logger;

    private SkyeLogger() {
    }

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
