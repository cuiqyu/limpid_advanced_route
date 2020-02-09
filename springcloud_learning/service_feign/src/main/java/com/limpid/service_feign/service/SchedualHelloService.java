package com.limpid.service_feign.service;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * @auther cuiqiongyu
 * @create 2020-02-08 16:30
 */
@FeignClient(value = "service-client", fallback = SchedualHelloHystrixService.class)
public interface SchedualHelloService {

    @RequestMapping(value = "/hi",method = RequestMethod.GET)
    String sayHiFromClientOne(@RequestParam(value = "name") String name);

}