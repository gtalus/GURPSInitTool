
Downloading and running
=======================

The latest release can be found here: https://github.com/gtalus/GURPSInitTool/releases/latest

GURPSInitTool is written in java and requires Java (JRE) to be installed on your computer. It is packaged as an executable jar and can be saved and run from any location.

Typical steps to run:

1. Make sure you have Java installed. If not, it can be obtained from http://java.com
2. Download ``GURPSInitTool.jar`` from the release link (above)
3. Run the downloaded ``GURPSInitTool.jar`` file
   
Note the program will create a 'GitApp.props' in its directory which saves various program attributes, such as window size and location.

Troubleshooting
---------------

Program won't start
~~~~~~~~~~~~~~~~~~~

First, make sure Java is installed. If it is, but the program does not seem to start when clicked, you should run it from a terminal window the command line which will either work or will give a potentially useful error message. The command line for executing the jar file is:::

   (If you are in the directory containing GURPSInitTool.jar)
   java -jar GURPSInitTool.jar
   -or-
   java -jar C:/path/to/GURPSInitTool.jar

If you are on a windows system and are not familiar with running such commands you may be able to use this .bat file: :download:`GURPSInitTool.bat <_static/GURPSInitTool.bat>`. Download it into the same directory as GURPSInitTool.jar and run it.


