# redisson-netty-direct-memory
Reproducer of the issue with Redisson and Netty possible direct memory leak.

## Requirements
- Docker
- Java 21

## How to start
1. Start Redis instance
```shell
docker-compose up -d
```
2. Build artifact 
```shell
./gradlew clean build
```
3. Run application 
```shell
java -XX:MaxDirectMemorySize=64M -jar build/libs/redisson-netty-direct-memory-0.0.1-SNAPSHOT.jar
```

## How to reproduce
Send POST request to the application with the following parameters. Do it few times (to invoke OOM it might sometimes take dozens of times) and watch used direct memory log.
```shell
curl -X POST 'http://localhost:8080/send-packages-to-redis?objectsInPackage=8000&numberOfPackages=200&batchSize=8000&poolSize=4'
```
