# File-Server

Upload file, list files, and download file.

This app is ***not a standalone server***, you must have `auth-service`, `auth-gateway` and other middlewares setup. To compile this app, you will also need to manually install the following modules & dependencies, these are all my repositories.

***Do not run the 'build' scripts, these are written for my development environment only***

***The frontend project has been moved to a new repository [file-service-front](https://github.com/CurtisNewbie/file-service-front), the one included here may be removed at anytime later.***

## Middleware

- MySQL
- Nacos (or others, e.g., zookeeper)
- RabbitMQ
- Redis

## Configuration

Data Type | Property Name | Description | Default Value
----------|---------------|-------------|---------------
string | base.path | base path used by application, this path is not used for file storage, where the uploaded files are stored depends on the table `fs_group`. Currently, this path is only used for `TempFolderDeleteFileOperation` which is an implementation of `DeleteFileOperation`. When the application is configured to use this file operation, deleting a file means that this file is moved to the temp folder, and the location of the temp folder depends on this `base.path` configuration | none

## File Operations and SPI Interfaces

File Operation is simply some sort of operation for files, e.g., delete a file, read a file, etc. The following are the SPI interfaces used for file operations:

- `DeleteFileOperation`
    - used to delete a file
- `ReadFileOperation`
    - used to read a file
- `WriteFileOperation`
    - used to write a file
- `ZipFileOperation`
    - used to zip multiple entries into a single zip file

This application uses `ServiceLoader` to load implementation for these SPI interfaces, you may change to another SPI implementation if necessary. For more information, read [https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html](https://docs.oracle.com/javase/tutorial/sound/SPI-intro.html).

The location of the mapping files:

```
resources/
    META-INF/
        services/
            com.yongj.io.operation.DeleteFileOperation
            com.yongj.io.operation.ReadFileOperation
            com.yongj.io.operation.WriteFileOperation
            com.yongj.io.operation.ZipFileOperation
```

## Example

For `DeleteFileOperation` SPI interface, there are two implementations:

- com.yongj.io.operation.TempFolderDeleteFileOperation 
- com.yongj.io.operation.PhysicallyDeleteFileOperation

`TempFolderDeleteFileOperation` is the default implementation, and we want to switch to `PhysicallyDeleteFileOperation`, then we will modify the file:

```
resources/
    META-INF/
        services/
            com.yongj.io.operation.DeleteFileOperation
```

The content of this file before modification:

```
com.yongj.io.operation.TempFolderDeleteFileOperation
```

The content of this file after modification:

```
com.yongj.io.operation.PhysicallyDeleteFileOperation
```

## Task Scheduling  

Task scheduling in this app is supported by `Quartz` and `distributed-task-module`. A task implementation bean is already written for this application, you may create a record in table `task` as follows to use it: 

The task implementation bean: 

- com.yongj.job.DeleteFileJob

In table `task`:

|id |job_name      |target_bean |cron_expr    |app_group   |enabled|concurrent_enabled|
|---|--------------|------------|-------------|------------|-------|------------------|
|1  |delete file job |deleteFileJob|0 0 0/1 ? * *|file-server|1      |0               |

## Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- curtisnewbie-bom
    - description: BOM file for dependency management
    - url: https://github.com/CurtisNewbie/curtisnewbie-bom
    - under `/microservice` folder

- common-module
    - description: for common utility classes 
    - url: https://github.com/CurtisNewbie/common-module

- redis-util-module
    - description: Utility classes for Redis
    - url: https://github.com/CurtisNewbie/redis-util-module

- distributed-task-module
    - description: for distributed task scheduling
    - url: https://github.com/CurtisNewbie/distributed-task-module

- messaging-module
    - description: for RabbitMQ-based messaging 
    - url: https://github.com/CurtisNewbie/messaging-module
