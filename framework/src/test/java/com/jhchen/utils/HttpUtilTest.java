package com.jhchen.utils;

import com.alibaba.fastjson.JSON;
import com.jhchen.domain.modul.Account;
import com.jhchen.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.IOException;

@SpringBootTest
@RunWith(SpringRunner.class)
public class HttpUtilTest {
    @Autowired
    AccountService accountService;
    @Test
    public void testPost(){
        Account account = accountService.createAccount();
        account.setIp("http://localhost:8080");
        account.setPrivateKey(null);
        String s = JSON.toJSONString(account);
        System.out.println(s);
        try {
            String response = HttpUtil.post("http://localhost:8081/register", s);
            System.out.println(response);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
