package com.corelogic.bis.tax.test.redisson_direct_memory;

import java.lang.management.BufferPoolMXBean;
import java.lang.management.ManagementFactory;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.redisson.Redisson;
import org.redisson.api.RLocalCachedMap;
import org.redisson.api.RedissonClient;
import org.redisson.api.options.LocalCachedMapOptions;
import org.redisson.codec.JsonJacksonCodec;
import org.redisson.config.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class SendPackagesToRedisService {

  private static final Logger log = LoggerFactory.getLogger(SendPackagesToRedisService.class);

  private final RLocalCachedMap<Integer, TestObject> redissonCacheMap;

  public SendPackagesToRedisService(RedissonClient redissonClient) {
    Config config = redissonClient.getConfig();
    config.setCodec(new JsonJacksonCodec());
    RedissonClient overridedRedissonClient = Redisson.create(config);

    LocalCachedMapOptions<Integer, TestObject> options = LocalCachedMapOptions.name("direct-memory-test");
    options.syncStrategy(LocalCachedMapOptions.SyncStrategy.UPDATE);
    redissonCacheMap = overridedRedissonClient.getLocalCachedMap(options);
  }

  public void sendPackagesToRedis(int objectsInPackage, int numberOfPackages, int batchSize, int poolSize) {
    try (ExecutorService executorService = Executors.newFixedThreadPool(poolSize)) {
      List<Callable<Void>> tasks = new ArrayList<>();

      for (int j = 0; j < numberOfPackages; j++) {
        HashMap<Integer, TestObject> hashMap = new HashMap<>();
        for (int i = 0; i < objectsInPackage; i++) {
          TestObject testObject = new TestObject("value" + i + j, i);
          hashMap.put(testObject.hashCode(), testObject);
        }

        tasks.add(() -> {
          redissonCacheMap.putAll(hashMap, batchSize);
          logDirectMemoryMetrics("Direct memory info after sending packages to Redis");
          return null;
        });
      }

      List<Future<Void>> results = executorService.invokeAll(tasks);
      for (Future<Void> result : results) {
        result.get();
      }
    } catch (Exception e) {
      log.error("Error while sending packages to Redis", e);
    }
  }

  public void logMemoryMetrics() {
    logDirectMemoryMetrics("Direct memory info");
  }

  private void logDirectMemoryMetrics(String logMsg) {
    List<BufferPoolMXBean> bufferPools = ManagementFactory.getPlatformMXBeans(BufferPoolMXBean.class);
    for (BufferPoolMXBean pool : bufferPools) {
      if ("direct".equals(pool.getName())) {
        log.info("{}: used {} bytes ", logMsg, pool.getMemoryUsed());
      }
    }
  }

}
