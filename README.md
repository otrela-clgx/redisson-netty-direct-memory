# redisson-netty-direct-memory
OutOfMemory reproducer

## Requirements
- Docker
- Java 21

## How to start
1. Run `docker-compose up -d`
2. Run `gradle clean build`
3. Run `java -jar target/libs/redisson-direct-memory-0.0.1-SNAPSHOT.jar`
