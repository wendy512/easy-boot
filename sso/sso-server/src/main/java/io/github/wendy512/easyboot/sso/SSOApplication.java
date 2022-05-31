package io.github.wendy512.easyboot.sso;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * springboot 启动类
 * @author taowenwu
 * @date 2021-04-02 09:59:9:59
 * @since 1.0.0
 */
@SpringBootApplication(scanBasePackages = "io.github.wendy512")
public class SSOApplication {

    public static void main(String[] args) {
        SpringApplication.run(SSOApplication.class);
    }
}
