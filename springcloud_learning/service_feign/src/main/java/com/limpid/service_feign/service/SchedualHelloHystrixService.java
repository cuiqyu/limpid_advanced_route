package com.limpid.service_feign.service;

import org.springframework.stereotype.Component;

/**
 * @auther cuiqiongyu
 * @create 2020-02-09 10:28
 */
@Component
public class SchedualHelloHystrixService implements SchedualHelloService {

    @Override
    public String sayHiFromClientOne(String name) {
        return "sorry, " + name;
    }

}