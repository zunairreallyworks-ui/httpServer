Secure Java Web Server
Muhammad Zunair

--------------------------------------------------
Project Overview
--------------------------------------------------
This project is a secure Java-based web server developed to serve static web pages, process HTTP GET and POST requests, handle form submissions, store submitted data, and demonstrate secure software design principles.

The application was developed as part of the Software Architecture and Security module.

--------------------------------------------------
Requirements
--------------------------------------------------
- Java Development Kit (JDK) 17 or later recommended
- Tested using:

  java version "17.0.12"
  javac 17.0.12

- Compatible with Windows and Linux environments with Java installed.

--------------------------------------------------
Dependencies / Libraries / Frameworks
--------------------------------------------------
This project uses standard Java SE libraries only.

Imported packages include:
- java.io
- java.net
- java.nio.file
- java.util
- java.util.concurrent

No external frameworks were used.
No third-party libraries were required.
No package manager or build tool was required.

--------------------------------------------------
Project Contents
--------------------------------------------------
- Source code: all .java files
- Compiled .class files included for convenience
- Runnable bytecode executable: server.jar
- public/ folder for served files
- data/ folder for stored submissions
- logs/ folder for log output
- README.txt

--------------------------------------------------
Compile Instructions
--------------------------------------------------
Open terminal / PowerShell inside the project folder and run:

javac *.java

This compiles the Java source files into .class bytecode files.

--------------------------------------------------
Run Instructions
--------------------------------------------------

Option 1: Run compiled classes

java MainServer

Option 2: Run packaged executable JAR

java -jar server.jar

--------------------------------------------------
Server Access
--------------------------------------------------
Once started, the server listens on port 8080.

Open a browser and visit:

http://localhost:8080/

Other devices or clients on the same network may also access the server using the machine IP address:

http://<IP_ADDRESS>:8080/

Example:

http://192.168.1.10:8080/

--------------------------------------------------
Notes
--------------------------------------------------
- Run the server from the project root directory so relative folders such as public, data, and logs are resolved correctly.
- Console output is visible when run from terminal or PowerShell.
- Log files are written to the logs folder.
- Submitted form data is stored in the data folder.
- If port 8080 is already in use, close the conflicting process or change the port number in MainServer.java.
- The included .class files are compiled bytecode generated from the .java source files and are provided as an additional execution option.
- The server.jar file contains the compiled application packaged into a single executable file.

--------------------------------------------------
Environment Used During Development
--------------------------------------------------
- Operating System: Windows
- Java Runtime: 17.0.12
- Java Compiler: 17.0.12

--------------------------------------------------
Expected Folder Structure
--------------------------------------------------
public/
data/
logs/

--------------------------------------------------
Example Usage
--------------------------------------------------
1. Open terminal in the project folder
2. Run:

java -jar server.jar

3. Open browser:

http://localhost:8080/

--------------------------------------------------
End of File
--------------------------------------------------