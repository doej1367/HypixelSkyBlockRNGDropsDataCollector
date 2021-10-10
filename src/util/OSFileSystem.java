package util;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import main.MainWindow;

public class OSFileSystem {
	private final MainWindow mainWindow;

	public OSFileSystem(MainWindow mainWindow) {
		this.mainWindow = mainWindow;
	}

	public ArrayList<File> lookForMinecraftLogFolders() {
		// get roots of file system structure of your OS
		mainWindow.addOutput("INFO: Analyzing file system structure ...");
		File[] rootFileSystems = File.listRoots();
		if (rootFileSystems.length <= 0) {
			mainWindow.addOutput("ERROR: No file systems found!");
			return null;
		}
		mainWindow.addOutput("INFO: Found " + Arrays.toString(rootFileSystems));

		// adjust Minecraft file path to operating system and found drives
		ArrayList<File> minecraftFolders = new ArrayList<>();
		String homePath = System.getProperty("user.home");
		File currentMinecraftFolder = null;
		switch (OSName.getOperatingSystemType()) {
		case Windows:
			mainWindow.addOutput("INFO: Found Windows operating system");
			for (File drive : rootFileSystems) {
				currentMinecraftFolder = new File(drive + homePath.substring(3) + "\\AppData\\Roaming\\.minecraft");
				if (currentMinecraftFolder.exists())
					minecraftFolders.add(currentMinecraftFolder);
			}
			break;
		case MacOS:
			mainWindow.addOutput("INFO: Found MacOS operating system");
			currentMinecraftFolder = new File(homePath + "/Library/Application Support/minecraft");
			if (currentMinecraftFolder.exists())
				minecraftFolders.add(currentMinecraftFolder);
			break;
		case Linux:
			mainWindow.addOutput("INFO: Found Linux operating system");
			currentMinecraftFolder = new File(homePath + "/.minecraft");
			if (currentMinecraftFolder.exists())
				minecraftFolders.add(currentMinecraftFolder);
			break;
		case Other:
			mainWindow.addOutput("ERROR: Unknown operating system!");
			break;
		}
		if (minecraftFolders.size() <= 0) {
			mainWindow.addOutput("ERROR: No minecraft folders found!");
		}
		for (String path : mainWindow.getAdditionalFolderPaths()) {
			currentMinecraftFolder = new File(path);
			if (currentMinecraftFolder.exists())
				minecraftFolders.add(currentMinecraftFolder);
		}
		for (File minecraftFolder : minecraftFolders)
			mainWindow.addOutput("INFO: Found " + minecraftFolder.getAbsolutePath());

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
		return minecraftLogFolders;
	}
}
