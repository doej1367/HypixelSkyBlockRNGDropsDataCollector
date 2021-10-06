package util;

/**
 * 
 * @author doej1367
 */
public class MinecraftLogLine {
	private long creationTime;
	private String text;

	public MinecraftLogLine(long creationTime, String text) {
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
