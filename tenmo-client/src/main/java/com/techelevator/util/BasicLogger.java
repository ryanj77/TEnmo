package com.techelevator.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class BasicLogger {

	private static String LOGS_SUBFOLDER = "logs";
	private static String APPLICATION_PROJECT_SUBFOLDER = "tenmo-client";
	private static PrintWriter pw = null;
	
	public static void log(String message) {
		try {
			if (pw == null) {
				// Kludge workaround for issue where logger can't find the logs subfolder when application project is in
				// a subfolder of the root folder loaded into IntelliJ: Check to see if logs folder can be found in root
				// IntelliJ folder... if not than assume it is in application project subfolder.
				File folder = new File(LOGS_SUBFOLDER);
				String logFolderPath = folder.exists() ? LOGS_SUBFOLDER : APPLICATION_PROJECT_SUBFOLDER
						+ File.separator + LOGS_SUBFOLDER;

				String logFilePath = logFolderPath + File.separator
						+ LocalDate.now().format(DateTimeFormatter.ISO_DATE) + ".log";

				pw = new PrintWriter(new FileOutputStream(logFilePath, true));
			}
			pw.println(LocalDateTime.now().format(DateTimeFormatter.ISO_DATE_TIME) + " " + message);
			pw.flush();
		}
		catch (FileNotFoundException e) {
			throw new BasicLoggerException(e.getMessage());
		}
	}

}