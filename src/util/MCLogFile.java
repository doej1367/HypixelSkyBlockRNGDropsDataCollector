package util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.zip.GZIPInputStream;

public class MCLogFile {
	private long creationTime;
	private String playerName;
	private Stream<String> linesStream;

	public MCLogFile(File logFile) throws FileNotFoundException, IOException {
		creationTime = getCreationTime(logFile);
		InputStream inputStream = new FileInputStream(logFile);
		if (logFile.getName().endsWith(".gz"))
			inputStream = new GZIPInputStream(inputStream);
		@SuppressWarnings("resource")
		BufferedReader br = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
		final String userSettingLine = "\\[[0-9:]{8}\\] \\[Client thread/INFO\\]: Setting user: ";
		linesStream = br.lines().filter(a -> {
			if (playerName == null && a.matches(userSettingLine + ".*"))
				playerName = a.replaceAll(userSettingLine, "");
			return a.contains("[CHAT]");
		});
	}

	public List<MCLogLine> filterLines(String logLineFilterRegex) {
		List<MCLogLine> filteredlogLines = linesStream
				.map(a -> a.replaceAll("\\[[0-9:]{8}\\] \\[Client thread/INFO\\]: \\[CHAT\\] ", "").trim())
				.filter(a -> a.matches(logLineFilterRegex)).map(a -> new MCLogLine(creationTime, a))
				.collect(Collectors.toList());
		linesStream.close();
		return filteredlogLines;
	}

	public String getPlayerName() {
		return playerName;
	}

	private long getCreationTime(File file) {
		try {
			return Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toMillis();
		} catch (IOException ignored) {
		}
		return 0;
	}
}