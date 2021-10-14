# Hypixel SkyBlock - RNG-Drops Data Collector
A tool that searches through Minecraft log files to collect data about the drop chance of RNG-Drops in Hypixel SkyBlock and sends it to an accociated Google Form

(*Note: It was once called HypixelSkyBlockNecronHandleDataCollector, but with all the changes in v1.1.0 I renamed it*)

## Client-Side: Java Tool
### Where can I download it?
- [Download](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/releases/download/v1.1.8/RNGDropsDataCollector.jar) (latest release)
    - It's the first .jar-file in [Realeases](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/releases)
### What exactly does this app do?
1. Start the app. ([MainWindow.java#L255-L266](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/main/MainWindow.java#L255-L266))
2. Show a small window to keep you up to date on what the app is doing and two buttons. One to start analyzing with the default .minecraft folders the other to add custom .minecraft folders. ([MainWindow.java#L65-L131](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/main/MainWindow.java#L65-L131))
3. You can now add the custom folders if needed and then start the analyzing. ([FolderWindow.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/main/FolderWindow.java), [MainWindow.java#L268-L279](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/main/MainWindow.java#L268-L279))
4. Get the roots of the local file systems like e.g. "C:\\" and "D:\\" for Windows or "/" for Mac/Linux and check what name your operating system has to determin where your minecraft folder is. For Windows this would be "%APPDATA%\\.minecraft", MacOS uses "\~/Library/Application Support/minecraft" and for Linux it's simply "\~/.minecraft". Once the minecraft folders are located the log folders are searched for. ([MainWindow.java#L136-L137](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/main/MainWindow.java#L136-L137), [OSFileSystem.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/util/OSFileSystem.java) and [OSName.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/util/OSName.java))
5. Once the "logs" folders are found, the actual parsing and data extraction of the chat messages from all those dungeon runs and slayers, etc. can begin. ([MainWindow.java#L140-L195](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/main/MainWindow.java#L140-L195), [MCLogFile.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/util/MCLogFile.java), [MCLogLine.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/util/MCLogLine.java), [LogRecords.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/util/LogRecords.java) and [TimeslotMap.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/util/TimeslotMap.java))
6. Now that we have the data we need, the final step is to send it to the Google Form, so that it can be summed up and processed together with all those other submissions in the to forms connected Google Sheet. ([MainWindow.java#L198-L226](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/main/MainWindow.java#L198-L226), [GoogleFormApi.java](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/util/GoogleFormApi.java#))
7. Finally the buttons are re-enabled and in case that an exception occured during the analysis the error is shown ([MainWindow.java#L227-L238](https://github.com/doej1367/HypixelSkyBlockRNGDropsDataCollector/blob/ce00ecea3e481f9e5af7983a25bb69c9ce836cfe/src/main/MainWindow.java#L227-L238))
8. Thats it. You can now close the app. If you want to you can also delete the jar-file or start the app again to submit a new and updated response.


    ![icon](screenshots/screenshot01.png)

  

## Server-Side: Google-Forms / Google-Sheets
- Submitted automatically on successful log file analysis
- [Results as Google Sheets Graphic](https://docs.google.com/spreadsheets/d/e/2PACX-1vReIuER28dXhxg4nQA-9RasMRvrXXb14EZdMTEmccgl-ACaybZ1nYHQVauiW9S08nWOOawyQ48P4HU0/pubhtml)
