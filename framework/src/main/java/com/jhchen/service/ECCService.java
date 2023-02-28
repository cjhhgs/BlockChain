package com.jhchen.service;

import javafx.util.Pair;
import org.apache.commons.codec.binary.Base64;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import java.security.*;
import java.security.spec.ECGenParameterSpec;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import static sun.security.x509.X509CertImpl.SIGNATURE;

@Component
public class ECCService {
    static {
        Security.addProvider(new BouncyCastleProvider());
    }

    /**
     * 生成一个密钥对
     * @return
     * @throws Exception
     */
    public Pair<String,String> newKeyPair(){
        // 创建椭圆曲线算法的密钥对生成器，算法为 ECDSA
        try{
            KeyPairGenerator generator = KeyPairGenerator.getInstance("EC", "BC");
            ECGenParameterSpec ecSpec = new ECGenParameterSpec("secp256k1");
            generator.initialize(ecSpec);

            KeyPair keyPair = generator.generateKeyPair();
            String privateString = Base64.encodeBase64String(keyPair.getPrivate().getEncoded());
            String publicString = Base64.encodeBase64String(keyPair.getPublic().getEncoded());

            Pair<String,String> pair = new Pair<>(privateString,publicString);
            return pair;
        }catch (Exception e){
            e.printStackTrace();
        }
        return null;

    }


    /**
     * ECC 加密
     *
     * @param publicKeyText 公钥
     * @param data      原文
     * @return　密文
     */
    public String eccEncrypt(String publicKeyText, String data) {
        try {
            X509EncodedKeySpec x509EncodedKeySpec2 = new X509EncodedKeySpec(Base64.decodeBase64(publicKeyText));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PublicKey publicKey = keyFactory.generatePublic(x509EncodedKeySpec2);
            Cipher cipher = Cipher.getInstance("ECIES", "BC");
            cipher.init(Cipher.ENCRYPT_MODE, publicKey);
            byte[] result = cipher.doFinal(data.getBytes());
            return Base64.encodeBase64String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    /**
     * ECC 解密
     *
     * @param privateKeyText 私钥
     * @param data  　       密文
     * @return　原文
     */
    public String eccDecrypt(String privateKeyText, String data) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKeyText));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey privateKey = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
            Cipher cipher = Cipher.getInstance("ECIES", "BC");
            cipher.init(Cipher.DECRYPT_MODE, privateKey);
            byte[] result = cipher.doFinal(Base64.decodeBase64(data));
            return new String(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 私钥签名
     *
     * @param privateKey 私钥
     * @param data       原文
     * @return 签名
     */
    public String eccSign(String privateKey, String data) {
        try {
            PKCS8EncodedKeySpec pkcs8EncodedKeySpec5 = new PKCS8EncodedKeySpec(Base64.decodeBase64(privateKey));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PrivateKey key = keyFactory.generatePrivate(pkcs8EncodedKeySpec5);
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initSign(key);
            signature.update(data.getBytes());
            return new String(Base64.encodeBase64(signature.sign()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 公钥验签
     *
     * @param publicKey 公钥
     * @param srcData   原文
     * @param sign      签名
     * @return
     */
    public boolean eccVerify(String publicKey, String srcData, String sign) {
        try {
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(Base64.decodeBase64(publicKey));
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            PublicKey key = keyFactory.generatePublic(keySpec);
            Signature signature = Signature.getInstance("SHA256withECDSA");
            signature.initVerify(key);
            signature.update(srcData.getBytes());
            return signature.verify(Base64.decodeBase64(sign.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

}
