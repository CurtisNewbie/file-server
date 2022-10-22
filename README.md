# File-Service V1.2.5

Upload file, list files, and download file.

This app is ***not a standalone server***, you must have `auth-service`, `auth-gateway` and other relevant services setup (the so called microservices :D). To compile this app, you will also need to manually install the following modules & dependencies, these are all my repositories.

***Do not run the 'build' scripts, these are written for my development environment only***

## Requirements 

- file-service-front (Angular frontend) >= [v1.1.15](https://github.com/CurtisNewbie/file-service-front/tree/v1.1.15)
- auth-gateway >= [v1.0.4](https://github.com/CurtisNewbie/auth-gateway/tree/v1.0.4)
- auth-service >= [v1.1.4.3](https://github.com/CurtisNewbie/auth-service/tree/v1.1.4.3)
- MySQL 5.7 or 8
- Consul
- RabbitMQ
- Redis

## Configuration

| Data Type | Property Name                   | Description                                                                                                                                                                                                                                                                                                                                                                                                                                                                           | Default Value  |
|-----------|---------------------------------|---------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------------|----------------|
| string    | base.path                       | base path used by application, this path is not used for file storage, where the uploaded files are stored depends on the table `fs_group`. Currently, this path is only used for `TempFolderDeleteFileOperation` which is an implementation of `DeleteFileOperation`. When the application is configured to use this file operation, deleting a file means that this file is moved to the temp folder, and the location of the temp folder depends on this `base.path` configuration | none           |
| int       | file-service.max-zip-entries    | maximum number of zip entries                                                                                                                                                                                                                                                                                                                                                                                                                                                         | unlimited      |
| int       | file-service.upload-speed-limit | uploading speed limit in megabytes per seconds, e.g., value of `10` will be interpreted as '10mb/s', and `-1` means unlimited speed. It's not a 100% accurate control of speed, it's roughly consistent with the one you specified. E.g., 50mb/s (if your device can handle it) will get speed around 46mb/s ~ 56mb/s, it depends.                                                                                                                                                        | -1 (unlimited) |

## Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- [curtisnewbie-bom](https://github.com/CurtisNewbie/curtisnewbie-bom)
- [distributed-task-module v2.1.1.2](https://github.com/CurtisNewbie/distributed-task-module/tree/v2.1.1.2)
- [messaging-module v2.0.7](https://github.com/CurtisNewbie/messaging-module/tree/v2.0.7)
- [auth-service-remote v1.1.4.3](https://github.com/curtisnewbie/auth-service/tree/v1.1.4.3)
- [common-module v2.1.9](https://github.com/CurtisNewbie/common-module/tree/v2.1.9)
- [redis-util-module v2.0.3](https://github.com/CurtisNewbie/redis-util-module/tree/v2.0.3)


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

Task scheduling in this app is supported by `Quartz` and `distributed-task-module`. A few task implementation beans are already written for this application, you may create a record in table `task` as follows to use them: 

The task implementation beans: 

- com.yongj.job.DeleteFileJob
- com.yongj.job.FetchFileUploaderNameJob
- com.yongj.job.ScanFsGroupSizeJob 

For example:

```sql
INSERT INTO `task`
    (job_name, target_bean, cron_expr, app_group, enabled, concurrent_enabled, update_date)
VALUES
    ('FetchFileUploaderNameJob','fetchFileUploaderNameJob','0 0 0/6 ? * *','file-server',0,0,CURRENT_TIMESTAMP),
    ('DeleteFileJob','deleteFileJob','0 0 0/6 ? * *','file-server',1,0,CURRENT_TIMESTAMP),
    ("ScanFsGroupSizeJob", "scanFsGroupSizeJob", "0 0 0/1 ? * *", "file-server",1,0,CURRENT_TIMESTAMP),
    ("GenerateUserFileAccessJob", "generateUserFileAccessJob", "0 0 0 ? * *", "file-server",1,0,CURRENT_TIMESTAMP);
```
    
## Updates

For release v1.2.5, the job `GenerateUserFileAccessJob` should be added into the `task` table and triggered immediately when it's first time deployed. It scans the `file_info` table and generates the data in `user_file_access`, which controls what files users can see on thier webpage. 



