package main;

import java.awt.BorderLayout;
import java.awt.Button;
import java.awt.EventQueue;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import util.TimeslotMap;
import util.GoogleFormApi;
import util.LogRecords;
import util.MCLogLine;
import util.MCLogFile;
import util.OSFileSystem;
import javax.swing.JTextArea;
import java.awt.Font;
import javax.swing.JScrollPane;
import java.awt.Component;
import javax.swing.Box;
import java.awt.Color;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import javax.swing.BoxLayout;

/**
 * 
 * @author doej1367
 */
public class MainWindow extends JFrame {
	private static final long serialVersionUID = 1L;
	private static MainWindow mainWindow;
	private FolderWindow folderWindow;

	private JPanel contentPane;
	private JTextArea outputTextField;
	private Button defaultButton;
	private Button addFoldersButton;
	private JPanel panel;
	private Component horizontalGlue;
	private Component horizontalGlue_1;

	private TreeSet<String> additionalFolderPaths = new TreeSet<>();
	private LogRecords logRecords = new LogRecords();
	private Component horizontalGlue_2;

	/**
	 * Create the frame.
	 */
	public MainWindow() {
		setFont(new Font("Consolas", Font.PLAIN, 14));
		// create window
		setTitle("Hypixel SkyBlock RNG-Drops Data Collector");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 800, 540);
		setMinimumSize(getSize());
		setLocationRelativeTo(null);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 5));
		setContentPane(contentPane);

		// add buttons
		panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);
		panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));

		horizontalGlue = Box.createHorizontalGlue();
		panel.add(horizontalGlue);

		defaultButton = new Button("Start analyzing currently known .minecraft folders");
		defaultButton.setBackground(new Color(240, 248, 255));
		defaultButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				startAnalysis();
			}
		});
		panel.add(defaultButton);
		defaultButton.setForeground(Color.BLUE);
		defaultButton.setFont(new Font("Consolas", Font.PLAIN, 14));
		
		horizontalGlue_2 = Box.createHorizontalGlue();
		panel.add(horizontalGlue_2);

		addFoldersButton = new Button("Add custom .minecraft folder locations");
		addFoldersButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				EventQueue.invokeLater(new Runnable() {
					public void run() {
						try {
							if (folderWindow == null)
								folderWindow = new FolderWindow(mainWindow);
							folderWindow.setVisible(true);
						} catch (Exception ignored) {
						}
					}
				});
			}
		});
		addFoldersButton.setBackground(new Color(255, 240, 245));
		panel.add(addFoldersButton);
		addFoldersButton.setForeground(new Color(153, 0, 102));
		addFoldersButton.setFont(new Font("Consolas", Font.PLAIN, 14));

		horizontalGlue_1 = Box.createHorizontalGlue();
		panel.add(horizontalGlue_1);

		// add a scrollable text field with multiple lines of text for debug output
		JScrollPane scrollPane = new JScrollPane();
		contentPane.add(scrollPane, BorderLayout.CENTER);
		outputTextField = new JTextArea();
		outputTextField.setFont(new Font("Consolas", Font.PLAIN, 14));
		outputTextField.setText("");
		outputTextField.setEditable(false);
		scrollPane.setViewportView(outputTextField);
	}

	private synchronized void analyze() {
		// look for log folders
		OSFileSystem fileSystem = new OSFileSystem(mainWindow);
		ArrayList<File> minecraftLogFolders = fileSystem.lookForMinecraftLogFolders();

		// analyze all found log files
		addOutput("INFO: Loading log files ... (might take a minute)");
		TreeMap<String, Integer> playerNames = new TreeMap<String, Integer>();
		List<MCLogLine> relevantLogLines = new ArrayList<>();
		final String logLineFilters_regex = logRecords.getLoglinefilterRegex();
		MCLogFile minecraftLogFile = null;
		for (File minecraftLogFolder : minecraftLogFolders) {
			addOutput("INFO: Loading files from " + minecraftLogFolder.getAbsolutePath());
			for (File logFile : minecraftLogFolder.listFiles()) {
				try {
					minecraftLogFile = new MCLogFile(logFile);
					relevantLogLines.addAll(minecraftLogFile.filterLines(logLineFilters_regex));
					String name = minecraftLogFile.getPlayerName();
					if (name != null)
						playerNames.put(name, playerNames.getOrDefault(name, 0) + 1);
				} catch (FileNotFoundException ignored) {
				} catch (IOException ignored) {
				}
			}
		}
		Collections.sort(relevantLogLines);

		// count floor 7 runs with S+ score and count Hadle's and Kismet feathers
		addOutput("INFO: Analyzing log lines ...");

		for (int i = 0; i < relevantLogLines.size(); i++)
			i = logRecords.add(i, relevantLogLines);

		String name = playerNames.entrySet().stream().max((a, b) -> a.getValue() - b.getValue()).get().getKey();
		addOutput("INFO: Found most logins from " + name);

		// send data to google form
		addOutput("INFO: Sending collected data to associated Google Form ...");
		GoogleFormApi api = new GoogleFormApi("1FAIpQLSffEH7mVGTbzxWM4_AuAlvJzOFLtVt41Er7re8maAsaiUT68Q");
		api.put(651714876, name); // name id
		api.put(94270834, logRecords.get("extra.d.f7.S+").toString()); // runs
		logRecords.remove("extra.d.f7.S+");
		api.put(969988648, logRecords.get("extra.d.f7.S+.Necron's Handle").toString()); // drops
		logRecords.remove("extra.d.f7.S+.Necron's Handle");
		api.put(1950438969, logRecords.get("extra.i.Kismet Feather").toString()); // feathers
		logRecords.remove("extra.i.Kismet Feather");
		StringBuilder sb = new StringBuilder();
		boolean firstLine = true;
		for (Entry<String, TimeslotMap> entry : logRecords.entrySet()) {
			if (firstLine)
				firstLine = false;
			else
				sb.append(";");
			sb.append(entry.getKey());
			sb.append(":");
			sb.append(entry.getValue().toString());
		}
		api.put(1820154262, sb.toString());
		api.put(495627145, "" + System.currentTimeMillis()); // timestamp
		if (api.sendData()) {
			addOutput("INFO: Data sent successfully");
			addOutput("");
			addOutput("Thanks for your contribution :)");
		} else {
			addOutput("ERROR: Data could not be submitted");
		}
		defaultButton.setEnabled(true);
		addFoldersButton.setEnabled(true);
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
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	private void startAnalysis() {
		defaultButton.setEnabled(false);
		addFoldersButton.setEnabled(false);
		Thread t0 = new Thread() {
			@Override
			public void run() {
				mainWindow.analyze();
			}
		};
		t0.start();
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

	public TreeSet<String> getAdditionalFolderPaths() {
		return additionalFolderPaths;
	}
}
