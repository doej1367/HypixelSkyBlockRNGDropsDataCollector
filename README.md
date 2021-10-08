# Hypixel SkyBlock - Necon Handle - Data Collector
A tool to collect data about the drop chance of a Necron's Handle on Floor VII in Hypixel SkyBlock Dungeons

# Client-Side: Java Tool
## Where can I download it?
- It's the first .jar-file in [Realeases](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/releases)
## What exactly does this app do?
1. Start the app. ([MainWindow.java#L141-L159](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/main/MainWindow.java#L141-L159))
2. Show a small window to keep you up to date on what the app is doing. ([MainWindow.java#L38-L57](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/main/MainWindow.java#L38-L57))
3. Get the roots of the local file systems like e.g. "C:\\" and "D:\\" for Windows or "/" for Mac/Linux and check what name your operating system has to determin where your minecraft folder is. For Windows this would be "%APPDATA%\\.minecraft", MacOS uses "~/Library/Application Support/minecraft" and for Linux it's simply "~/.minecraft". Once the minecraft folders are located the log folders are searched for. ([MainWindow.java#L61-L64](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/main/MainWindow.java#L61-L64), [OSFileSystem.java](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/util/OSFileSystem.java) and [OSName.java](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/util/OSName.java))
4. Once the "logs" folders are found, the actual parsing and data extraction of the chat messages from all those dungeon runs can begin. ([MainWindow.java#L67-L119](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/main/MainWindow.java#L67-L119), [MinecraftLogFile.java](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/util/MinecraftLogFile.java), [MinecraftLogLine.java](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/util/MinecraftLogLine.java) and [DungeonRunLog.java](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/util/DungeonRunLog.java))
5. Now that we have the data we need, the final step is to send it to the Google Form, so that it can be summed up and processed together with all those other submissions in the to forms connected Google Sheet. ([MainWindow.java#L122-L135](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/main/MainWindow.java#L122-L135), [GoogleFormApi.java](https://github.com/doej1367/HypixelSkyBlockNeconHandleDataCollector/blob/47d975d4aec78f601807b9c33d9d5761edbf34e5/src/util/GoogleFormApi.java))
6. Thats it. Now you can close the app. If you want you can also delete the jar-file or start the app again to submit a new and updated response.


    ![icon](screenshots/screenshot01.png)

  

# Server-Side: Google-Forms / Google-Sheets
- Submitted automatically on successful log file analysis
- [Results as Google Sheets Graphic](https://docs.google.com/spreadsheets/d/e/2PACX-1vReIuER28dXhxg4nQA-9RasMRvrXXb14EZdMTEmccgl-ACaybZ1nYHQVauiW9S08nWOOawyQ48P4HU0/pubhtml)
