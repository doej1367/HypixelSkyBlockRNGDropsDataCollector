package main;

/**
 * 
 * @author doej1367
 */
public class LogLine {
	private long creationTime;
	private String text;

	public LogLine(long creationTime, String text) {
		this.creationTime = creationTime;
		this.text = text;
	}

	public long getCreationTime() {
		return creationTime;
	}

	public String getText() {
		return text;
	}
}
