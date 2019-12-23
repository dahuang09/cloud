package com.jacob.cloud.item.controller;

import com.jacob.cloud.itemapi.entity.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Random;

@RestController
@RequestMapping("/item")
public class ItemController {

    @GetMapping("/item")
    public Item item() {
        Item item = new Item();
        item.setItemId("item" + System.currentTimeMillis());
        item.setItemNme("item");
//        int randomValue = new Random().nextInt(3);
//        if (randomValue != 2) {
//            int i = 1 / 0; //这里会报错
//        }

        return item;

    }
}
