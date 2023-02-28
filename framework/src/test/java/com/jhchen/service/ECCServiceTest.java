package com.jhchen.service;

import javafx.util.Pair;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;


@SpringBootTest
@RunWith(SpringRunner.class)
public class ECCServiceTest {
    @Autowired
    ECCService eccService;
    @Autowired
    private AccountService accountService;

    @Test
    public void eccTest(){
        Pair<String, String> keyPair = eccService.newKeyPair();
        String privateKey = keyPair.getKey();
        String publicKey = keyPair.getValue();

        System.out.println("ECC公钥: "+publicKey);
        System.out.println("ECC私钥: "+privateKey);

        String s = "123456";
        String encrypt = eccService.eccEncrypt(publicKey, s);
        String decrypt = eccService.eccDecrypt(privateKey, encrypt);
        System.out.println("原文："+s);
        System.out.println("密文："+encrypt);
        System.out.println("解密："+decrypt);

    }


}
