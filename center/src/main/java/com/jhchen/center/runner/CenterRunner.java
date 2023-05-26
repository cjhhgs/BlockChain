package com.jhchen.center.runner;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;


@Component
public class CenterRunner implements CommandLineRunner {
    @Autowired
    Task task;


    @Override
    public void run(String... args) throws Exception {
        task.executeAlloc();
    }
}
