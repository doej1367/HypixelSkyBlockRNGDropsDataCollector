package util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogRecords extends TreeMap<String, TimeslotMap> {
	private static final long serialVersionUID = 1L;
	private String allLines;
	private final String[] dungeonMainLines = { "The Catacombs - Floor [VI]+", "Master Mode Catacombs - Floor [VI]+" };
	private final String dungeonScoreLine = "Team Score: [0-9]+ [\\(][SABCD][+]?[\\)].*";
	private final String[] dungeonLootLines = { "Necron's Handle", "Wither Chestplate",
			"RARE REWARD! Recombobulator 3000", "Enchanted Book \\(One For All I\\)", "Auto Recombobulator",
			"Implosion", "Wither Shield", "Shadow Warp", "Wither Catalyst", "Enchanted Book \\(Soul Eater I\\)",
			"Precursor Gear", "Necromancer Lord Chestplate", "Precursor Eye", "Giant's Sword", "Summoning Ring",
			"Shadow Assassin Chestplate", "Livid Dagger", "RARE REWARD! Fuming Potato Book", "Spirit Bow",
			"Spirit Wing", "Spirit Boots", "Spirit Bone", "Scarf's Studies", "Bonzo's Staff", "Bonzo's Mask",
			"Adaptive Blade", "Adaptive Chestplate", "Spirit Pet", "Enchanted Book \\(Rend [I]+\\)", "Dark Orb",
			"Enchanted Book \\(Overload I\\)" };

	private final String[] slayerLines = { ".5... .7Slay ..[0-9,]+ Combat XP .7worth of [A-Z][a-z]+.7.",
			"[A-Z][a-z]+ Slayer LVL [0-9]+ - Next LVL in [0-9,]+ XP!" };
	private String startedSlayer = null;
	private String[] slayerLootLines = { "[A-Z ]+ DROP![ ]{1,2}[\\(]?item[\\)]?( [\\(].+[\\)])?" };
	private final String[] slayerItems = { "Beheaded Horror", "Scythe Blade", "Shard of the Shredded", "Warden Heart",
			"Fly Swatter", "Tarantula Talisman", "Digested Mosquito", "Red Claw Egg", "Couture Rune I",
			"Overflux Capacitor", "Grizzly Bait", "Pocket Espresso Machine", "Judgement Core", "Handy Blood Chalice",
			"Exceedingly Rare Ender Artifact Upgrader", "Void Conqueror Enderman Skin" };

	private String[] inventoryChangeLines = { "You [a-z]+ item.+", "You collected [0-9,]+ coins from selling item .+",
			"Market You bought [0-9]+ x item .+", "You bought back item x[0-9,]+ .+",
			"Bazaar! Claimed [0-9]+x item worth [0-9,]+ coins bought .+", "[+-] [0-9]+x item" };
	private final String[] monitoredItems = { "Kismet Feather" };

	public LogRecords() {
		// prepare TimeslotMap with 7 day slot duration
		put("extra.d.f7.S+", new TimeslotMap(7, 2));
		put("extra.d.f7.S+.Necron's Handle", new TimeslotMap(7, 2));
		put("extra.i.Kismet Feather", new TimeslotMap(7, 2));
		// prepare slayerLootLines
		ArrayList<String> slayerLootLines_tmp = new ArrayList<>();
		for (String item : slayerItems)
			for (String regex : slayerLootLines)
				slayerLootLines_tmp.add(regex.replace("item", item));
		slayerLootLines = slayerLootLines_tmp.toArray(new String[0]);
		// prepare inventoryChangeLines
		ArrayList<String> inventoryChangeLines_tmp = new ArrayList<>();
		for (String item : monitoredItems)
			for (String regex : inventoryChangeLines)
				inventoryChangeLines_tmp.add(regex.replace("item", item));
		inventoryChangeLines = inventoryChangeLines_tmp.toArray(new String[0]);
		// prepare allLines
		ArrayList<String> allLines_tmp = new ArrayList<>();
		allLines_tmp.addAll(Arrays.asList(dungeonMainLines));
		allLines_tmp.add(dungeonScoreLine);
		allLines_tmp.addAll(Arrays.asList(dungeonLootLines));
		allLines_tmp.addAll(Arrays.asList(slayerLines));
		allLines_tmp.addAll(Arrays.asList(slayerLootLines));
		allLines_tmp.addAll(Arrays.asList(inventoryChangeLines));
		StringBuilder sb = new StringBuilder();
		boolean isFirstElement = true;
		for (String s : allLines_tmp) {
			if (isFirstElement) {
				sb.append("(");
				isFirstElement = false;
			} else
				sb.append("|(");
			sb.append(s);
			sb.append(")");
		}
		allLines = sb.toString();
	}

	/**
	 * Starts parsing a recorded activity like a dungeon run that starts at a
	 * specific log line and returns the line after the record ends and the record
	 * was added to a list
	 * 
	 * @param recordStartLine - start line
	 * @param logLines        - the list with all the MinecraftLogLine objects
	 * @return - the line where the parsing should continue
	 */
	public int add(int recordStartLine, List<MCLogLine> logLines) {
		int lineIndex = recordStartLine;
		MCLogLine line = logLines.get(lineIndex);
		if (line.matches(dungeonMainLines)) {
			// dungeon run
			char mode = line.getText().startsWith("M") ? 'm' : 'f';
			int floor = parseRomanNumeral(mode == 'f' ? line.getText().substring(22) : line.getText().substring(30));
			String score = null;
			lineIndex = (lineIndex < logLines.size() - 1) ? (lineIndex + 1) : lineIndex;
			line = logLines.get(lineIndex);
			if (line.getText().matches(dungeonScoreLine)) {
				score = line.getText().replaceAll("Team Score: [0-9]* [\\(]", "").replaceAll("[\\)].*", "");
				if (score.matches("[BCD]"))
					score = "BCD";
			} else
				return --lineIndex;
			if (mode == 'f' && floor == 7 && score.contains("S+"))
				get("extra.d." + mode + floor + "." + score).add(logLines.get(lineIndex).getCreationTime(), 1);
			get("d." + mode + floor + "." + score).add(logLines.get(lineIndex).getCreationTime(), 1);
			lineIndex = (lineIndex < logLines.size() - 1) ? (lineIndex + 1) : lineIndex;
			line = logLines.get(lineIndex);
			while (line.matches(dungeonLootLines) && lineIndex < logLines.size() - 1) {
				String itemName = line.getText().replaceAll("RARE REWARD! ", "").replaceAll("Enchanted Book \\(", "")
						.replaceAll("\\)", "");
				if (mode == 'f' && floor == 7 && score.contains("S+") && itemName.contains("Necron's Handle"))
					get("extra.d." + mode + floor + "." + score + "." + itemName).add(line.getCreationTime(), 1);
				get("d." + mode + floor + "." + score + "." + itemName).add(line.getCreationTime(), 1);
				lineIndex++;
				line = logLines.get(lineIndex);
			}
			return --lineIndex;
		} else if (line.matches(inventoryChangeLines)) {
			// monitored items
			if (line.getText().startsWith("- ") || line.getText().startsWith("+ ")) {
				int sign = line.getText().startsWith("- ") ? -1 : 1;
				int count = Integer.parseInt(line.getText().substring(2).split("x ")[0]);
				get("i." + line.getText().replaceAll("[+-] [0-9]+x ", "")).add(line.getCreationTime(), sign * count);
			} else {
				String itemName = "";
				for (String item : monitoredItems) {
					if (line.getText().contains(item)) {
						itemName = item;
						break;
					}
				}
				String text = line.getText();
				final String[] patterns = { " x[0-9]+ ", " [0-9]+x ", " [0-9]+ x " };
				int itemCount = 1;
				Matcher m = null;
				for (String pattern : patterns) {
					m = Pattern.compile(pattern).matcher(text);
					if (m.find()) {
						itemCount = Integer.parseInt(text.substring(m.start(), m.end()).replaceAll("[ x]+", ""));
						break;
					}
				}
				if (text.contains("bought") || text.contains("claimed"))
					;
				else if (text.contains("sold") || text.contains("collected"))
					itemCount = -itemCount;
				if (itemName.contains("Kismet Feather"))
					get("extra.i." + itemName).add(line.getCreationTime(), itemCount);
				get("i." + itemName).add(line.getCreationTime(), itemCount);
			}
		} else if (line.matches(slayerLines)) {
			if (line.getText().matches(slayerLines[0])) {
				startedSlayer = line.getText();
			} else if (startedSlayer != null) {
				String type = line.getText().split(" ")[0].substring(0, 1).toLowerCase();
				int xpToSpawn = Integer.parseInt(startedSlayer.substring(15).split(" ")[0].replaceAll(",", ""));
				String formatedXpToSpawn = String.format("%05d", xpToSpawn);
				get("s." + type + "." + formatedXpToSpawn).add(logLines.get(lineIndex).getCreationTime(), 1);
				lineIndex = (lineIndex < logLines.size() - 1) ? (lineIndex + 1) : lineIndex;
				line = logLines.get(lineIndex);
				if (line.getText().matches(slayerLines[0]))
					startedSlayer = line.getText();
				lineIndex = (lineIndex < logLines.size() - 1) ? (lineIndex + 1) : lineIndex;
				line = logLines.get(lineIndex);
				while (line.matches(slayerLootLines) && lineIndex < logLines.size() - 1) {
					String name = "";
					for (String item : slayerItems)
						if (line.getText().contains(item)) {
							name = item;
							break;
						}
					get("s." + type + "." + formatedXpToSpawn + "." + name).add(line.getCreationTime(), 1);
					int mf = 0;
					String magicFindText = line.getText().split(name + "[ \\(\\+\\)]+")[1];
					if (magicFindText.contains("% Magic Find!)"))
						mf = Integer.parseInt(magicFindText.split("%")[0]);
					get("s." + type + "." + formatedXpToSpawn + "." + name + ".wmf").add(line.getCreationTime(),
							10000 / (100 + mf));
					lineIndex++;
					line = logLines.get(lineIndex);
				}
				return --lineIndex;
			}
		}
		return lineIndex;
	}

	private int parseRomanNumeral(String romanNumeral) {
		// core idea from https://stackoverflow.com/a/17534350
		if (romanNumeral.length() < 1)
			return 0;
		if (romanNumeral.startsWith("V"))
			return 5 + parseRomanNumeral(romanNumeral.substring(1));
		if (romanNumeral.startsWith("IV"))
			return 4 + parseRomanNumeral(romanNumeral.substring(2));
		if (romanNumeral.startsWith("I"))
			return 1 + parseRomanNumeral(romanNumeral.substring(1));
		return 0;
	}

	@Override
	public TimeslotMap get(Object key) {
		if (super.get(key.toString()) == null)
			put(key.toString(), new TimeslotMap(84, 0));
		return super.get(key.toString());
	}

	public String getLoglinefilterRegex() {
		return allLines;
	}
}
