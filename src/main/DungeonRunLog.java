package main;

import java.util.HashMap;

/**
 * 
 * @author doej1367
 */
public class DungeonRunLog extends HashMap<Integer, Integer> {
	private static final long serialVersionUID = 1L;
	private final long floor_seven_birthday = 1605571200000L;

	public DungeonRunLog() {
		// weeks_fSBb -> weeks from SkyBlock birthday
		int weeks_fSBb = (int) ((System.currentTimeMillis() - floor_seven_birthday) / 604800000);
		for (int i = 0; i < weeks_fSBb; i++)
			put(i, 0);
	}

	public Integer add(long timestamp, Integer value) {
		int weeks_fSBb = (int) ((timestamp - floor_seven_birthday) / 604800000);
		value += getOrDefault(weeks_fSBb, 0);
		return put((int) weeks_fSBb, value);
	}

	public Integer getSum() {
		int res = 0;
		for (int v : values())
			res += v;
		return res;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for (int v : values()) {
			sb.append(v);
			sb.append(",");
		}
		sb.deleteCharAt(sb.length() - 1);
		return sb.toString();
	}
}
