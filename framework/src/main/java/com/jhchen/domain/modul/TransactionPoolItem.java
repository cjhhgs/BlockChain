package com.jhchen.domain.modul;

import lombok.Data;

import java.util.Date;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 待处理交易池元素
 * @createDate 2023/3/22 15:48
 * @since 1.0.0
 */
@Data
public class TransactionPoolItem {
    private SignedTransaction transaction;
    private String minerAddr;
    private Boolean finished;
    private Date allocateTime;
    private Date finishTime;

    public TransactionPoolItem(SignedTransaction transaction,String minerAddr){
        this.transaction = new SignedTransaction(transaction);
        this.minerAddr =minerAddr;
        finished = false;
        allocateTime = new Date();
        finishTime = null;
    }

    public Boolean equals(SignedTransaction signedTransaction){

        return this.transaction.equals(signedTransaction);
    }
}
