# File-Server

Upload file, list files, and download file.

In this branch, this app is not a standalone server, it internally uses Dubbo for RPC and connects to the `auth-service` (https://github.com/CurtisNewbie/auth-service) for authentication. You must have `auth-service` setup as well as other middlewares, such as MySQL, Nacos (or others, e.g., Zookeeper) and so on.

## Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

### 1. auth-module

For authentication related functionalities

```
URL: https://github.com/CurtisNewbie/authmodule
Branch: microservice
```

### 2. common-module

For common functionalities, such as utility classes

```
URL: https://github.com/CurtisNewbie/common-module
Branch: dev
```

### 3. service-module

Make the app a standalone Dubbo service

```
URL: https://github.com/CurtisNewbie/service-module
Branch: main
```
