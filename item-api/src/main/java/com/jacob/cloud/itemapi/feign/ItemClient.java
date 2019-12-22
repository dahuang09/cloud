package com.jacob.cloud.itemapi.feign;

import com.jacob.cloud.itemapi.entity.Item;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;

@FeignClient(value = "item-service")
public interface ItemClient {

    @GetMapping(value = "item/item")
    Item item();
}
