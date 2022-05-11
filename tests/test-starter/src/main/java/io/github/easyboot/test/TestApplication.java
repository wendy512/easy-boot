package io.github.easyboot.test;

import io.github.easyboot.logging.Log;
import io.github.easyboot.logging.LogFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * @author taowenwu
 * @date 2021-04-20 11:41:11:41
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "io.github.easyboot")
public class TestApplication {
    private static final Log LOG = LogFactory.getLog(TestApplication.class);
    
    public static void main(String[] args) throws Exception {
        SpringApplication.run(TestApplication.class, args);
    }

}
