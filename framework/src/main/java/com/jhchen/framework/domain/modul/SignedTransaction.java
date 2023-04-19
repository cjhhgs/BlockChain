package com.jhchen.framework.domain.modul;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.util.Date;

@ApiModel("交易")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignedTransaction {
    @ApiModelProperty("hash值，id")
    private String hash;
    @ApiModelProperty("发起者公钥")
    private String publicKey;
    @ApiModelProperty("交易内容")
    private Transaction transaction;
    @ApiModelProperty("发起者签名")
    private String sign;
    @ApiModelProperty("创建时间")
    private Date createTime;

    public SignedTransaction(String publicKey,Transaction transaction,String sign){

        this.publicKey = publicKey;
        this.transaction = new Transaction(transaction);
        this.sign = sign;
        this.createTime = new Date();
        this.hash = DigestUtils.sha256Hex(publicKey+ transaction +sign+createTime.toString());
    }

    public SignedTransaction(SignedTransaction t) {
        this.createTime = (Date)t.getCreateTime().clone();
        this.publicKey = t.getPublicKey();
        this.sign = t.getSign();
        this.transaction = new Transaction(t.getTransaction());
        this.hash = DigestUtils.sha256Hex(publicKey+ transaction +sign+createTime.toString());
    }

    boolean equals(SignedTransaction transaction){
        return this.hash.equals(transaction.getHash());
    }
}


