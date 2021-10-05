package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import java.util.zip.GZIPInputStream;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JScrollPane;

/**
 * 
 * @author doej1367
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;

	private JPanel contentPane;
	private JTextArea outputTextField;

	TreeMap<String, Integer> playerNames = new TreeMap<>();

	private String[] logFilterRegex = { "The Catacombs - Floor [VI]*", "Master Mode Catacombs - Floor [VI]*",
			"Team Score: [0-9]* [\\(][SABCD][+]?[\\)]", "Necron's Handle", "You [a-z]+ Kismet Feather.*",
			"You collected [0-9,]+ coins from selling Kismet Feather to .*" };

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					MainWindow frame = new MainWindow();
					frame.setVisible(true);
					Thread t0 = new Thread() {
						@Override
						public void run() {
							frame.startAnalysis();
						}
					};
					t0.start();
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		// create window
		setTitle("Hypixel SkyBlock Necron Handle Data Collector");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 540);
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);

		// add a text field with multiple lines of text for debug output
		outputTextField = new JTextArea();
		outputTextField.setFont(new Font("Consolas", Font.PLAIN, 14));
		outputTextField.setText("");
		outputTextField.setEditable(false);
		scrollPane.setViewportView(outputTextField);

	}

	private void startAnalysis() {
		// get the roots of the file system structure of your OS
		addOutput("INFO: Analyzing file system structure ...");
		File[] rootFileSystems = File.listRoots();
		if (rootFileSystems.length <= 0) {
			addOutput("ERROR: No file systems found!");
			return;
		}
		addOutput("INFO: Found " + Arrays.toString(rootFileSystems));

		// adjust minecraft file path to operating system and found drives
		File tmpLogFolder = null;
		String homePath = System.getProperty("user.home");
		ArrayList<File> minecraftFolders = new ArrayList<File>();
		switch (OsCheck.getOperatingSystemType()) {
		case Windows:
			addOutput("INFO: Windows operating system found");
			for (File drive : rootFileSystems) {
				tmpLogFolder = new File(drive + homePath.substring(3) + "\\AppData\\Roaming\\.minecraft");
				if (tmpLogFolder.exists())
					minecraftFolders.add(tmpLogFolder);
			}
			break;
		case MacOS:
			addOutput("INFO: MacOS operating system found");
			tmpLogFolder = new File(homePath + "/Library/Application Support/minecraft");
			if (tmpLogFolder.exists())
				minecraftFolders.add(tmpLogFolder);
			break;
		case Linux:
			addOutput("INFO: Linux operating system found");
			tmpLogFolder = new File(homePath + "/.minecraft");
			if (tmpLogFolder.exists())
				minecraftFolders.add(tmpLogFolder);
			break;
		case Other:
			addOutput("ERROR: Unknown operating system!");
			break;
		}
		if (minecraftFolders.size() <= 0) {
			addOutput("ERROR: No minecraft folders found!");
			return;
		}
		for (File minecraftFolder : minecraftFolders)
			addOutput("INFO: Found " + minecraftFolder.getAbsolutePath());

		// get log folders
		ArrayList<File> minecraftLogFolders = new ArrayList<File>();
		for (File minecraftFolder : minecraftFolders) {
			for (File folder : minecraftFolder.listFiles()) {
				if (folder.isDirectory()) {
					if (folder.getName().equalsIgnoreCase("logs")) {
						minecraftLogFolders.add(folder);
					} else {
						for (File subFolder : folder.listFiles()) {
							if (subFolder.getName().equalsIgnoreCase("logs"))
								minecraftLogFolders.add(subFolder);
						}
					}
				}
			}
		}
		for (File minecraftLogFolder : minecraftLogFolders)
			addOutput("INFO: Found " + minecraftLogFolder.getAbsolutePath());

		// analyze all found log files
		addOutput("INFO: Analyzing log files ... (might take a minute)");
		List<LogLine> results = new ArrayList<>();
		BufferedReader br = null;
		for (File minecraftLogFolder : minecraftLogFolders) {
			for (File logFile : minecraftLogFolder.listFiles()) {
				try {
					long creationTime = getCreationTime(logFile);
					br = new BufferedReader(
							new InputStreamReader(new GZIPInputStream(new FileInputStream(logFile)), "UTF-8"));
					List<LogLine> tmp = br.lines().filter(a -> {
						if (a.matches("\\[[0-9:]{8}\\] \\[Client thread/INFO\\]: Setting user: .*")) {
							String name = a.replaceAll("\\[[0-9:]{8}\\] \\[Client thread/INFO\\]: Setting user: ", "");
							playerNames.put(name, playerNames.getOrDefault(name, 0) + 1);
						}
						return a.contains("[CHAT]");
					}).map(a -> a.replaceAll("\\[[0-9:]{8}\\] \\[Client thread/INFO\\]: \\[CHAT\\] ", "").trim())
							.filter(a -> {
								for (int i = 0; i < logFilterRegex.length; i++) {
									if (a.matches(logFilterRegex[i])) {
										return true;
									}
								}
								return false;
							}).map(a -> new LogLine(creationTime, a)).collect(Collectors.toList());
					br.close();
					results.addAll(tmp);
				} catch (FileNotFoundException ignored) {
				} catch (IOException ignored) {
				}
			}
		}

		// count floor 7 runs with S+ score and count hadle's and chestplate's
		DungeonRunLog runs = new DungeonRunLog(); // Floor 7 S+ runs
		DungeonRunLog necronHandles = new DungeonRunLog(); // Necron's Handles dropped
		DungeonRunLog kismetFeathers = new DungeonRunLog(); // Kismet Feathers added / removed
		boolean isFloorSeven = false;
		for (LogLine line : results) {
			if (isFloorSeven && line.getText().matches("Team Score: [0-9]* [\\(]S\\+[\\)]"))
				runs.add(line.getCreationTime(), 1);
			if (line.getText().matches("You .* Kismet Feather.*")) {
				switch (line.getText().substring(4).split(" ")[0]) {
				case "bought":
				case "claimed":
					kismetFeathers.add(line.getCreationTime(), 1);
					break;
				case "sold":
				case "collected":
					kismetFeathers.add(line.getCreationTime(), -1);
					break;
				}
			}
			if (line.getText().matches("Necron's Handle"))
				necronHandles.add(line.getCreationTime(), 1);
			isFloorSeven = line.getText().matches("The Catacombs - Floor VII");
		}
		String name = playerNames.entrySet().stream().max((a, b) -> a.getValue() - b.getValue()).get().getKey();
		addOutput("INFO: Found most logins from " + name);
		addOutput("INFO: Found " + runs.getSum() + " normal Floor VII S+ runs");
		addOutput("INFO: Runs/week " + runs.values());
		addOutput("INFO: Found " + necronHandles.getSum() + " Necron's Handle Drop(s)");
		addOutput("INFO: Drops/week " + necronHandles.values());
		addOutput("INFO: Found " + kismetFeathers.getSum() + " Kismet Feathers in inventory");
		addOutput("INFO: Bought/week " + kismetFeathers.values());

		// send data to google form
		addOutput("INFO: Sending collected data to associated Google Form ...");
		GoogleFormApi api = new GoogleFormApi("1FAIpQLSffEH7mVGTbzxWM4_AuAlvJzOFLtVt41Er7re8maAsaiUT68Q");
		api.put(651714876, name); // name id
		api.put(94270834, runs.toString()); // runs
		api.put(969988648, necronHandles.toString()); // drops
		api.put(1950438969, kismetFeathers.toString()); // feathers
		api.put(495627145, "" + System.currentTimeMillis()); // timestamp
		if (api.sendData()) {
			addOutput("INFO: Data sent successfully");
			addOutput("");
			addOutput("Thanks for your contribution :)");
		} else {
			addOutput("ERROR: Data could not be submitted");
		}

	}

	private long getCreationTime(File file) {
		try {
			return Files.readAttributes(file.toPath(), BasicFileAttributes.class).creationTime().toMillis();
		} catch (IOException ignored) {
		}
		return 0;
	}

	/**
	 * Adds a new line of text to the outputTextField
	 * 
	 * @param s - text to add
	 */
	private void addOutput(String s) {
		String oldText = outputTextField.getText();
		outputTextField.setText(oldText + s + "\n");
	}

}
