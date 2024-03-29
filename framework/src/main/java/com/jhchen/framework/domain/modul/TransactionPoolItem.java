package com.jhchen.framework.domain.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 待处理交易池元素
 * @createDate 2023/3/22 15:48
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class TransactionPoolItem {
    private SignedTransaction transaction;
    private String minerAddr;
    private Boolean finished;
    private Date allocateTime;
    private Date finishTime;
    private Integer height;

    public TransactionPoolItem(SignedTransaction transaction,String minerAddr, Integer height){
        this.transaction = new SignedTransaction(transaction);
        this.minerAddr =minerAddr;
        finished = false;
        allocateTime = new Date();
        finishTime = null;
        this.height = height;
    }

    public TransactionPoolItem(TransactionPoolItem transactionPoolItem) {
        this.transaction = new SignedTransaction(transactionPoolItem.getTransaction());
        this.minerAddr = new String(transactionPoolItem.getMinerAddr());
        this.height = new Integer(transactionPoolItem.getHeight());
    }

    public Boolean equals(SignedTransaction signedTransaction){
        return this.transaction.equals(signedTransaction);
    }
}
