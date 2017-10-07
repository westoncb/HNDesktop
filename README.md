# HNDesktop
A (Java) Swing desktop application Hacker News client written in Kotlin. I think it would be funny to make it actually good—contribute if you think so too. Or if you think the world actually needs this, that's okay too.

More background on the project here (and a **video** of it running!): http://symbolflux.com/projects/hndesktop

To run:

1) Install kotlinc (https://kotlinlang.org/docs/tutorials/command-line.html)
2) `kotlinc -cp ./lib/gson-2.6.2.jar:./lib/image4j-0.7.2.jar:./lib/jsoup-1.10.3.jar:./lib/prettytime-4.0.1.Final.jar:./ ./src/* -include-runtime -d hndesktop.jar`
3) Double click hndesktop.jar

To develop: unfortunately, at the moment you have to manually set up an IntelliJ Kotlin project, import the source, add all the jars in the 'lib' folder.

Check out the 'Issues' section here for some details on what's broken, and some enhancements ideas.

![Screenshot](https://github.com/westoncb/HNDesktop/blob/master/screenshot.png?raw=true "Optional Title")

Or if you are nostalgic for the old Metal 'look and feel,' like I am:
![Screenshot](https://github.com/westoncb/HNDesktop/blob/master/screenshot2.png?raw=true "Optional Title")
