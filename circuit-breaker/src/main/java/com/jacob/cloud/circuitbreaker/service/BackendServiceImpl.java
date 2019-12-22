package com.jacob.cloud.circuitbreaker.service;

import org.springframework.stereotype.Service;

@Service(value = "backendServiceImpl")
public class BackendServiceImpl implements BackendService {
    @Override
    public String doSomething() {
        return "backend service";
    }
}
