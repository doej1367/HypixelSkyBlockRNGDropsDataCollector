package main;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import util.DungeonRunLog;
import util.GoogleFormApi;
import util.MinecraftLogLine;
import util.MinecraftLogFile;
import util.OSFileSystem;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JScrollPane;

/**
 * 
 * @author doej1367
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static MainWindow mainWindow;

	private JPanel contentPane;
	private JTextArea outputTextField;

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

		// add a scrollable text field with multiple lines of text for debug output
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		outputTextField = new JTextArea();
		outputTextField.setFont(new Font("Consolas", Font.PLAIN, 14));
		outputTextField.setText("");
		outputTextField.setEditable(false);
		scrollPane.setViewportView(outputTextField);
	}

	private void startAnalysis() {
		// look for log folders
		OSFileSystem fileSystem = new OSFileSystem(mainWindow);
		ArrayList<File> minecraftLogFolders = fileSystem.lookForMinecraftLogFolders();
		for (File minecraftLogFolder : minecraftLogFolders)
			addOutput("INFO: Found " + minecraftLogFolder.getAbsolutePath());

		// analyze all found log files
		addOutput("INFO: Analyzing log files ... (might take a minute)");
		TreeMap<String, Integer> playerNames = new TreeMap<String, Integer>();
		List<MinecraftLogLine> relevantLogLines = new ArrayList<>();
		final String[] logLineFilters_regex = { "The Catacombs - Floor [VI]*", "Master Mode Catacombs - Floor [VI]*",
				"Team Score: [0-9]* [\\(][SABCD][+]?[\\)]", "Necron's Handle", "You [a-z]+ Kismet Feather.*",
				"You collected [0-9,]+ coins from selling Kismet Feather to .*" };
		MinecraftLogFile minecraftLogFile = null;
		for (File minecraftLogFolder : minecraftLogFolders) {
			for (File logFile : minecraftLogFolder.listFiles()) {
				try {
					minecraftLogFile = new MinecraftLogFile(logFile);
					relevantLogLines.addAll(minecraftLogFile.filterLines(logLineFilters_regex));
					String name = minecraftLogFile.getPlayerName();
					if (name != null)
						playerNames.put(name, playerNames.getOrDefault(name, 0) + 1);
				} catch (FileNotFoundException ignored) {
				} catch (IOException ignored) {
				}
			}
		}

		// count floor 7 runs with S+ score and count Hadle's and Kismet feathers
		DungeonRunLog runs = new DungeonRunLog(); // Floor 7 S+ runs
		DungeonRunLog necronHandles = new DungeonRunLog(); // Necron's Handles dropped
		DungeonRunLog kismetFeathers = new DungeonRunLog(); // Kismet Feathers added / removed
		boolean isFloorSeven = false;
		for (MinecraftLogLine line : relevantLogLines) {
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

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mainWindow = new MainWindow();
					mainWindow.setVisible(true);
					Thread t0 = new Thread() {
						@Override
						public void run() {
							mainWindow.startAnalysis();
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
	 * Adds a new line of text to the outputTextField
	 * 
	 * @param s - text to add
	 */
	public void addOutput(String s) {
		String oldText = outputTextField.getText();
		outputTextField.setText(oldText + s + "\n");
	}
}
