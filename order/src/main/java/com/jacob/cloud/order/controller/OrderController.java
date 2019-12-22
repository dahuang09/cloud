package com.jacob.cloud.order.controller;

import com.jacob.cloud.itemapi.entity.Item;
import com.jacob.cloud.itemapi.feign.ItemClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private ItemClient itemClient;

    @Value("${server.port}")
    private String port;

    @GetMapping(value = "order")
    public String order() {
        return "Hello Eureka, i am from port: " + port;
    }

    @GetMapping(value = "subItem")
    public String subItem() {
        Item item = itemClient.item();
        return "sub items =" + item.getItemId();
    }
}
