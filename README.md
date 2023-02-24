# FileScan App

FileScan app is a Spring Boot Rest Application for analyzing threats in files

## Description

* Start your application (Read more about this below)
* Copy your files into `files/process` package and wait for schedule run in every minute (default) or trigger with endpoint
* After the analysis is completed you can check the result into `files/report` package, but these report files also contain in progress data
* The report name is contains the file name and the flow id which is the unique id of scan

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

### Swagger

http://localhost:8081/FileScan/swagger-ui/index.html

### Endpoints

POST http://localhost:8081/FileScan/api/scan/file send file for analyzing
    form-data key is "file" and attach a file

GET http://localhost:8081/FileScan/api/scan/trigger trigger the file process and report flow

GET http://localhost:8081/FileScan/api/report?flowId=YOUR_FLOW_ID give back the report of scan

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