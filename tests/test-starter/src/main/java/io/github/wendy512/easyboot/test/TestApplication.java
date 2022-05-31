package io.github.wendy512.easyboot.test;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author taowenwu
 * @date 2021-04-20 11:41:11:41
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "io.github.wendy512")
public class TestApplication {
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestApplication.class, args);
    }

}
