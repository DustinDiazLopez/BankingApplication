package utils;

import models.User;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class Log {
    private static final int FILE_SIZE = 1_024;
    private static final Logger logger = Logger.getLogger("logger-kun");

    static {
        File folder = new File("logs");
        File log = new File(String.format("logs/%d.log", LocalDate.now().toEpochDay()));
        try {
            if (!folder.exists() && folder.mkdirs()) {}
            if (!log.exists() && log.createNewFile()) {}

            final FileHandler fh = new FileHandler(log.getAbsolutePath(), FILE_SIZE, 1, true);
            logger.addHandler(fh);
            final SimpleFormatter formatter = new SimpleFormatter();
            fh.setFormatter(formatter);
        } catch (SecurityException | IOException e) {
            e.printStackTrace();
        }
    }

    public static void forbidden(String msg) {
        System.err.println("403: This intrusion has been reported.");
        logger.severe(msg);
    }

    public static void main(String[] args) {
        logger.setLevel(Level.FINER);
        logger.fine("mighty fine");
        logger.info("hi-hi");
        logger.warning("show me what you got");
        logger.severe("Stop! You've violated the law!");
        logger.info("bye-bye!");
    }
}
