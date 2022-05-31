package io.github.wendy512.easyboot.test.controller;

import io.github.wendy512.easyboot.test.dao.People;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import io.github.wendy512.easyboot.vo.VoResponse;
import lombok.extern.slf4j.Slf4j;

/**
 * @author wendy512
 * @date 2022-05-11 16:48:16:48
 * @since 1.0.0
 */
@RestController
@RequestMapping("/test")
@Slf4j
public class TestController {
    
    @GetMapping("/hello")
    public VoResponse hello(@RequestParam(value = "name", required = true) String name) throws Exception {
        log.info("say hello!");
        return VoResponse.builder().ok(name + ", hello world");
    }
    
    @PostMapping(value = "/add", consumes = "application/json")
    public VoResponse add(@RequestBody @Validated People people) {
        return VoResponse.builder().ok();
    }
}
