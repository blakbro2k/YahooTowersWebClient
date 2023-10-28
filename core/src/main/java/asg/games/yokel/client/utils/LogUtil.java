package asg.games.yokel.client.utils;

import com.badlogic.gdx.Application;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.utils.GdxRuntimeException;
import com.github.czyzby.kiwi.log.Logger;
import com.github.czyzby.kiwi.log.LoggerService;

import asg.games.yokel.client.factories.Log4LibGDXLogger;
import asg.games.yokel.utils.YokelUtilities;

public class LogUtil {
    private static final String LOG_PROPERTIES_FILENAME = "Log4LibGDX.properties";
    private static final String DEFAULT_LOG_PROPERTIES_PATH = "config/" + LOG_PROPERTIES_FILENAME;
    private static final String LOGGER_PROP_TAG = "logger";
    private static final String LOG_PROP_NAME_TAG = "name";
    private static final String LOG_PROP_LEVEL_TAG = "level";
    private static String logPropertiesPath = DEFAULT_LOG_PROPERTIES_PATH;

    //Do not instantiate
    private LogUtil() {
    }

    public static Log4LibGDXLogger getLogger(final LoggerService loggerService, final Class<?> aClass) {
        loggerService.setFactory(Log4LibGDXLogger.getFactory());

        Logger logger = loggerService.getLoggerForClass(aClass);
        if (logger instanceof Log4LibGDXLogger) {
            Log4LibGDXLogger libGDXLogger = ((Log4LibGDXLogger) logger);
            loadLogLevelProperties(libGDXLogger, aClass);
            return libGDXLogger;
        }
        throw new GdxRuntimeException("Error getting Log4LibGDXLogger, was LoggerService Factory set to Log4LibGDXLogger?");
    }

    static void setLogPropertiesPath(String path) {
        logPropertiesPath = path;
    }

    static int getClassLogLevel(final Log4LibGDXLogger logger, final Class<?> clazz) {
        int localLogLevel = 0;
        if (logger != null) {
            final String packageName = clazz.getCanonicalName();
            localLogLevel = logger.getPackageClassLogLevel(packageName);
            if (localLogLevel < 0) {
                localLogLevel = getLogLevelFromPropertiesFile(packageName);
                logger.setPackageClassLogLevel(packageName, localLogLevel);
            }
        }
        return localLogLevel;
    }

    private static int getLogLevelFromPropertiesFile(String packageName) {
        FileHandle fileHandle = Gdx.files.internal(logPropertiesPath);

        int logLevel = 0;
        if (fileHandle != null) {
            String fileContents = fileHandle.readString();

            if (fileContents != null) {
                boolean getLevel = false;
                for (String contentLine : YokelUtilities.safeIterable(fileContents.split("\n"))) {
                    if (getLevel) {
                        String[] level = contentLine.split("=");
                        String levelString = level[1];

                        if (levelString.equals("debug")) {
                            logLevel = 3;
                        } else if (levelString.equals("info")) {
                            logLevel = 2;
                        } else if (levelString.equals("error")) {
                            logLevel = 1;
                        }
                        getLevel = false;
                        //break;
                    }
                    if (contentLine.contains(packageName)) {
                        getLevel = true;
                    }
                }
            }
        }
        return logLevel;
    }

    static void loadLogLevelProperties(final Log4LibGDXLogger libGDXLogger, final Class<?> aClass) {
        setLoggerLevel(libGDXLogger, getClassLogLevel(libGDXLogger, aClass));
    }

    static void setLoggerLevel(final Log4LibGDXLogger logger, int logLevel) {
        if (logger != null) {
            if (logLevel <= Application.LOG_NONE) {
                logger.setNone();
            } else if (logLevel == 1) {
                logger.setError();
            } else if (logLevel == 2) {
                logger.setInfo();
            } else {
                logger.setDebug();
            }
        }
    }
}
