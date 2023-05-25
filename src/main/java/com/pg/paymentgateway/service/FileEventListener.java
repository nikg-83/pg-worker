package com.pg.paymentgateway.service;

import org.springframework.stereotype.Service;

@Service
public interface FileEventListener {
    public void handleEvent(FileEvent event);
}
