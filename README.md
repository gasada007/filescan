# FileScan App

FileScan app is a Spring Boot Rest Application for analyzing threats in files

## Description

1. [x] Start your application (Read more about this below)
2. [x] Copy your files into `files/process` package
3. [x] After the analyzing is completed you can check the result into `files/report` package
3. [x] The reports collection and scan is a scheduled task, run in every minute (default) and store in progress data too

## Tech tree

- [Spring Boot] - framework
- [Java] - Java 19
- [JPA] - db operations
- [H2 or MySQL] - database
- [Maven] - software project management and build tool

## Installation

### Build app

Need Maven on machine , and use this command : `mvn clean install`

### Start app

After the previous step copy the created `jar` file into run package next to it `application.properties`

Need JRE 19 on machine, and use this command:

    macOS: `java -jar filescan-0.0.1-SNAPSHOT.jar`
    Windows: `call java -jar filescan-0.0.1-SNAPSHOT.jar`

## Use app

### Start app

**You have to modify `filescanio.api-key` in `application.properties` with yours**

You can change the settings in `application.properties` but the default is h2 memory storage db for quick use

You can use with mysql database too, but if you want this you have to install database software on your device

### Endpoints

POST http://localhost:8081/FileScan/api/scan/file send file for analyzing

GET http://localhost:8081/FileScan/api/scan/trigger trigger the file process and report flow

### Useful knowledge about application

The file send for scan and report collection is a scheduled task, run in every minute

The `files/report` package is still store in progress data you have to wait or trigger with endpoint

### `application.properties` Project specify values

| Key                          | Default value           | Description                                                            | 
|------------------------------|-------------------------|------------------------------------------------------------------------|
| filescanio.api-key           | YOUR_APIKEY             | Api key for use file scan io api **(ADD IT BEFORE RUN)**               |
| filescanio.host              | https://www.filescan.io | File Scan IO api host url                                              |
| files.process.dir            | files/process           | Dir for files which waiting for process (if you modify create package) |
| files.finished.dir           | files/finished          | Dir for finished files (if you modify create package)                  |
| files.failed.dir             | files/failed            | Dir for failed files (if you modify create package)                    |
| files.report.dir             | files/report            | Dir for report of files (if you modify create package)                 |
| file.processor.delayInSecond | 60000                   | Scan scheduler delays between runs in milliseconds                     |
| file.report.delayInSecond    | 60000                   | Report scheduler delays between runs in milliseconds                   |                                            |

## License

gasada007(fialgabor)