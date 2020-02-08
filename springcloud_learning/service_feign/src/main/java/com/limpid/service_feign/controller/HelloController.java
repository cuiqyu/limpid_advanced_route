package com.limpid.service_feign.controller;

import com.limpid.service_feign.service.SchedualHelloService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * @auther cuiqiongyu
 * @create 2020-02-08 16:33
 */
@RestController
public class HelloController {

    @Autowired
    private SchedualHelloService schedualHelloService;

    @GetMapping(value = "/hi")
    public String sayHi(@RequestParam String name) {
        return schedualHelloService.sayHiFromClientOne( name );
    }

}