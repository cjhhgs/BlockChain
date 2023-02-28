package com.jhchen.utils;

import com.jhchen.domain.modul.Account;
import com.jhchen.service.AccountService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class JSONUtilTest {
    @Autowired
    AccountService accountService;

    @Value("${accountPath}")
    public String accountPath;

    @Test
    public void accountJSONTest(){
        Account account = accountService.createAccount();
        JSONUtil.createJsonFile(account,accountPath);
        Account account1 = JSONUtil.loadJSONObject(accountPath,Account.class);
        System.out.println(account1);
    }


}
