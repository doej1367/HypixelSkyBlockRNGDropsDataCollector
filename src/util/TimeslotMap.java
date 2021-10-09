package util;

import java.util.HashMap;

/**
 * 
 * @author doej1367
 */
public class TimeslotMap extends HashMap<Integer, Integer> {
	private static final long serialVersionUID = 1L;
	private int days;
	private final long timeSlotStart;
	private final long skyblock_birthday = 1560211200000L;
	private final long dungeon_birthday = 1594080000000L;
	private final long floor_seven_birthday = 1605571200000L;

	/**
	 * 
	 * @param slotDurationInDays
	 * @param start              - starting time is 0 = skyblock_birthday, 1 =
	 *                           dungeon_birthday, 2 = floor_seven_birthday
	 */
	public TimeslotMap(int slotDurationInDays, int start) {
		this.days = slotDurationInDays;
		timeSlotStart = start == 0 ? skyblock_birthday : start == 1 ? dungeon_birthday : floor_seven_birthday;
		int timeSlotCount = (int) ((System.currentTimeMillis() - timeSlotStart) / (days * 86400000L));
		for (int i = 0; i < timeSlotCount; i++)
			put(i, 0);
	}

	public Integer add(long timestamp, Integer value) {
		int timeSlot = (int) ((timestamp - timeSlotStart) / (days * 86400000L));
		value += getOrDefault(timeSlot, 0);
		return put((int) timeSlot, value);
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
