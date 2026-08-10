package com.scanCrunch;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.scanCrunch", "com.scanCrunch"})
public class ScanCrunchApplication {

    public static void main(String[] args) {
        SpringApplication.run(ScanCrunchApplication.class, args);
    }

}
