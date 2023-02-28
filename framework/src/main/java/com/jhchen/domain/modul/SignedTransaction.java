package com.jhchen.domain.modul;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
@ApiModel("交易")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SignedTransaction {
    @ApiModelProperty("公钥")
    private String publicKey;
    @ApiModelProperty("交易内容")
    private Transaction transaction;
    @ApiModelProperty("签名")
    private String sign;
    @ApiModelProperty("完成状态")
    private Boolean finished;

    public SignedTransaction(String publicKey,Transaction transaction,String sign){
        this.publicKey = publicKey;
        this.transaction = transaction;
        this.sign = sign;
        this.finished = false;
    }

    public SignedTransaction(SignedTransaction t) {
        this.finished = t.getFinished();
        this.publicKey = t.getPublicKey();
        this.sign = t.getSign();
        this.transaction = new Transaction(t.getTransaction());
    }
}


