package org.mae.twg.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@SpringBootApplication
@EnableCaching
public class TwgBackendApplication {

    public static void main(String[] args) {
        SpringApplication.run(TwgBackendApplication.class, args);
    }

}
