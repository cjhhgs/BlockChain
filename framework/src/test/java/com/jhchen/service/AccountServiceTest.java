package com.jhchen.service;


import com.jhchen.domain.modul.Account;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class AccountServiceTest {
    @Autowired
    private AccountService accountService;

    @Test
    public void testAccount(){
        Account account = accountService.createAccount();
        System.out.println("私钥["+ account.getPrivateKey().length()+"]："+ account.getPrivateKey());

        System.out.println("公钥["+ account.getPublicKey().length()+"]："+ account.getPublicKey());

        System.out.println("地址["+ account.getAddr().length()+"]："+ account.getAddr());
    }


}
