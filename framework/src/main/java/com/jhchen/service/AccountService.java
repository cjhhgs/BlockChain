package com.jhchen.service;

import com.jhchen.domain.modul.Account;
import javafx.util.Pair;
import org.bitcoinj.core.Base58;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.*;

@Service
public class AccountService {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    @Autowired
    private ECCService eccService;

    /**
     * 创建一个账户
     * @return
     */
    public Account createAccount() {
        //生成密钥对
        Pair<String, String> pair;
        try {
            pair = eccService.newKeyPair();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        String privateString = pair.getKey();
        String publicString = pair.getValue();

        String address = getAddress(publicString);

        Account account = new Account(address,publicString,privateString,null);

        return account;
    }

    /**
     * 验证公钥私钥地址正确性
     * @param account
     * @return
     */
    public Boolean verifyUserAccount(Account account){

        return null;
    }


    /**
     * 公钥字符串转地址字符串
     * @param publicString 需要转换的公钥字符串
     * @return 公钥对应的25字节地址
     */
    static public String getAddress(String publicString){
        //在公钥上执行SHA-256
        MessageDigest sha = null;
        try {
            sha = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }

        byte[] s1 = new byte[0];
        try {
            s1 = sha.digest(publicString.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }


        //使用Bouncy Castle提供程序来执行RIPEMD-160
        MessageDigest rmd = null;
        try {
            rmd = MessageDigest.getInstance("RipeMD160");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        byte[] r1 = rmd.digest(s1);

        //在哈希开头添加一个0x00的版本字节。
        byte[] r2 = new byte[r1.length + 1];
        r2[0] = 0;
        for (int i = 0 ; i < r1.length ; i++) r2[i+1] = r1[i];

        //两次hash
        byte[] s2 = sha.digest(r2);
        byte[] s3 = sha.digest(s2);

        //第二次散列结果的前4个字节用作地址校验和。它附加到上面的RIPEMD160哈希。这是25字节的比特币地址
        byte[] a1 = new byte[25];
        for (int i = 0 ; i < r2.length ; i++) a1[i] = r2[i];
        for (int i = 0 ; i < 5 ; i++) a1[20 + i] = s3[i];

        //使用bitcoinj库中的Base58.encode()方法来获得最终的比特币地址
        String address = Base58.encode(a1);

        return address;
    }

}
