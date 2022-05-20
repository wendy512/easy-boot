package io.github.easyboot.test.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author wendy512
 * @date 2022-05-11 16:48:16:48
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test")
public class TestController {
    
    @RequestMapping("/hello")
    public String hello() {
        return "hello world";
    }
}
