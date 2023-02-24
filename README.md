# FileScan App

FileScan app is a Spring Boot Rest Application for analyzing threats in files 

## Description
1. [x] Start your application (Read more about this below)
2. [x] Copy your files into `files/process` package
3. [x] After the analyzing is completed you can check the result into `files/report` package

## Tech tree

- [Spring Boot] - framework
- [Java] - Java 19
- [JPA] - db queries
- [H2 or MySQL] - database
- [Maven] - software project management and build tool

## Installation
### Build app
Need Maven on machine , and use this command : `mvn clean install`

### Start app
Copy the created jar file into run package

Need JRE 19 on machine, and use this command: `java -jar filescan-0.0.1-SNAPSHOT.jar`

## Use app
### Start app
The app is listening in this url : http://localhost:8081/FileScan

You have to modify `filescanio.api-key` in `application.properties` with yours

You can change the settings in `application.properties` but the default is h2 memory storage db for quick use

You can use with mysql database too, but you have installed and create database software on your device

### Endpoints
POST http://localhost:8081/FileScan/api/scan/file send file for analyzing

GET http://localhost:8081/FileScan/api/scan/files trigger the file process and report flow

### Useful use knowladge about application

## Development


## License
gasada007(fialgabor)