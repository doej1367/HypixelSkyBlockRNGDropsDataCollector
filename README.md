# Hypixel SkyBlock - RNG-Drops Data Collector
A tool that searches through Minecraft log files to collect data about the drop chance of RNG-Drops in Hypixel SkyBlock and sends it to an accociated Google Form

(*Note: It was once called HypixelSkyBlockNecronHandleDataCollector, but with all the changes in v1.1.0 I renamed it*)

## Client-Side: Java Tool
### Where can I download it?
- [Download](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/releases/download/v1.1.7/RNGDropsDataCollector.jar) (latest release)
    - It's the first .jar-file in [Realeases](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/releases)
### What exactly does this app do?
1. Start the app. ([MainWindow.java#L223-L234](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/main/MainWindow.java#L223-L234))
2. Show a small window to keep you up to date on what the app is doing and two buttons. One to start analyzing with the default .minecraft folders the other to add custom .minecraft folders. ([MainWindow.java#L61-L127](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/main/MainWindow.java#L61-L127))
3. You can now add the custom folders if needed and then start the analyzing. ([FolderWindow.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/main/FolderWindow.java), [MainWindow.java#L236-L246](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/main/MainWindow.java#L236-L246))
4. Get the roots of the local file systems like e.g. "C:\\" and "D:\\" for Windows or "/" for Mac/Linux and check what name your operating system has to determin where your minecraft folder is. For Windows this would be "%APPDATA%\\.minecraft", MacOS uses "\~/Library/Application Support/minecraft" and for Linux it's simply "\~/.minecraft". Once the minecraft folders are located the log folders are searched for. ([MainWindow.java#L132-L133](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/main/MainWindow.java#L132-L133), [OSFileSystem.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/util/OSFileSystem.java) and [OSName.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/util/OSName.java))
5. Once the "logs" folders are found, the actual parsing and data extraction of the chat messages from all those dungeon runs and slayers, etc. can begin. ([MainWindow.java#L137-L174](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/main/MainWindow.java#L137-L174), [MCLogFile.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/util/MCLogFile.java), [MCLogLine.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/util/MCLogLine.java), [LogRecords.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/util/LogRecords.java) and [TimeslotMap.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/util/TimeslotMap.java))
6. Now that we have the data we need, the final step is to send it to the Google Form, so that it can be summed up and processed together with all those other submissions in the to forms connected Google Sheet. ([MainWindow.java#L177-L205](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/main/MainWindow.java#L177-L205), [GoogleFormApi.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/util/GoogleFormApi.java))
7. Finally the buttons are freed and in case that an exception occured during the analysis the error is shown ([MainWindow.java#L206-L217](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/7bbbe70c81fdfa6a53c48bdd1eaa6eefaad7cc4f/src/main/MainWindow.java#L206-L217))
8. Thats it. You can now close the app. If you want to you can also delete the jar-file or start the app again to submit a new and updated response.


    ![icon](screenshots/screenshot01.png)

  

## Server-Side: Google-Forms / Google-Sheets
- Submitted automatically on successful log file analysis
- [Results as Google Sheets Graphic](https://docs.google.com/spreadsheets/d/e/2PACX-1vReIuER28dXhxg4nQA-9RasMRvrXXb14EZdMTEmccgl-ACaybZ1nYHQVauiW9S08nWOOawyQ48P4HU0/pubhtml)
