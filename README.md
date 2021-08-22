# File-Server

Upload file, list files, and download file.

In this branch, this app is ***not a standalone server***, it internally uses Dubbo for RPC to talk to other services (e..g, auth-service mentioned below). You must have `auth-service` setup as well as other middlewares. To compile this app, you will also need to manually install the following modules & dependencies, these are all my repositories.

## Related-Services

- auth-service 
    - description: service for managing users, access log and operation log.
    - url: https://github.com/CurtisNewbie/auth-service

## Middlewares

- MySQL
- Nacos (or others, e.g., zookeeper)
- RabbitMQ
- Redis

## Modules and Dependencies

This project depends on the following modules that you must manually install (using `mvn clean install`).

- curtisnewbie-bom
    - description: BOM file for dependency management
    - url: https://github.com/CurtisNewbie/curtisnewbie-bom
    - branch: main
    - version: micro-0.0.1 (under `/microservce` folder)

- auth-module
    - description: for user authentication, security and integration with auth-service
    - url: https://github.com/CurtisNewbie/auth-module
    - branch: main 

- common-module
    - description: for common utility classes 
    - url: https://github.com/CurtisNewbie/common-module
    - branch: main

- service-module
    - description: import dependencies for a Dubbo service
    - url: https://github.com/CurtisNewbie/service-module
    - branch: main

- redis-util-module
    - description: Utility classes for Redis
    - url: https://github.com/CurtisNewbie/redis-util-module
    - branch: main

- log-tracing-module
    - desription: for log tracing between web endpoints and service layers
    - url: https://github.com/CurtisNewbie/log-tracing-module
    - branch: main

- distributed-task-module
    - description: for distributed task scheduling
    - url: https://github.com/CurtisNewbie/distributed-task-module
    - branch: main

- messaging-module
    - description: for RabbitMQ-based messaging 
    - url: https://github.com/CurtisNewbie/messaging-module
    - branch: main
