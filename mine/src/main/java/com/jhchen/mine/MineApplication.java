package com.jhchen.mine;


import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;

@SpringBootApplication
@ComponentScan(value = {"com.jhchen.mine.*", "com.jhchen.framework.*"})
@EnableWebMvc
public class MineApplication {
    public static void main(String[] args) {
        SpringApplication.run(MineApplication.class,args);
    }

}
