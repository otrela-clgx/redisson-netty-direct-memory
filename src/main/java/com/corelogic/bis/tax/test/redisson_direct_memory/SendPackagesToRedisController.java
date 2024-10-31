package com.corelogic.bis.tax.test.redisson_direct_memory;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SendPackagesToRedisController {

  private final SendPackagesToRedisService sendPackagesToRedisService;

  public SendPackagesToRedisController(SendPackagesToRedisService sendPackagesToRedisService) {
    this.sendPackagesToRedisService = sendPackagesToRedisService;
  }

  @PostMapping("/send-packages-to-redis")
  public ResponseEntity<String> sendPackagesToRedis(
      @RequestParam Integer objectsInPackage,
      @RequestParam Integer numberOfPackages,
      @RequestParam Integer batchSize,
      @RequestParam Integer poolSize
  ) {
    try {
      sendPackagesToRedisService.sendPackagesToRedis(objectsInPackage, numberOfPackages, batchSize, poolSize);
      return ResponseEntity.ok().build();
    } catch (Exception e) {
      return ResponseEntity.status(500).build();
    }
  }

  @GetMapping("/memory-metrics")
  public ResponseEntity<String> metrics() {
    sendPackagesToRedisService.logMemoryMetrics();
    return ResponseEntity.ok().build();
  }



}
