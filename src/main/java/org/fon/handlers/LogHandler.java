package org.fon.handlers;

import javafx.application.Platform;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class LogHandler {

    private static LogHandler mLogHandler = null;
    private PrintStream stream = null;

    private final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd/MMMM/yyyy hh:mm:ss");
    private static final String fileName = "logs" + DateTimeFormatter.ofPattern("_ddMMyy_HHmmss").format(LocalDateTime.now()) + ".txt";

    private static LogHandler getInstance() {
        if (mLogHandler == null) mLogHandler = new LogHandler();
        return mLogHandler;
    }

    private LogHandler() {
        File logFile = new File(fileName);
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                LogHandler.printStackTrace(e);
                Platform.exit();
            }
        }

        try {
            stream = new PrintStream(fileName, StandardCharsets.UTF_8);
        } catch (IOException ignored) {

        }
    }

    public static void printStackTrace(Throwable e) {
        LogHandler logHandler = LogHandler.getInstance();

        logHandler.stream.print(logHandler.dtf.format(LocalDateTime.now()) + ": ");
        e.printStackTrace(logHandler.stream);
    }
}
