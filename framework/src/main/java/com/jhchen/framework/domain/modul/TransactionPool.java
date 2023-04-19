package com.jhchen.framework.domain.modul;

import lombok.Data;

import java.util.*;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 交易池
 * @createDate 2023/3/22 15:29
 * @since 1.0.0
 */
@Data
public class TransactionPool {
    //未分配交易池
    private Queue<SignedTransaction> transactionList;
    //已分配交易池
    private List<TransactionPoolItem> allocatedTransactionList;
    //已完成交易池
    private List<TransactionPoolItem> finishedTransactionList;

    public void add(SignedTransaction transaction){
        transactionList.add(transaction);
    }


    public List<SignedTransaction> getUnallocated(Integer num){
        List<SignedTransaction> res = new ArrayList<>();
        if(num> transactionList.size()){
            num = transactionList.size();
        }
        for (int i = 0; i < num; i++) {
            res.add(transactionList.poll());
        }
        return res;

    }

    public boolean find(SignedTransaction transaction,String minerAddr){
        Iterator<TransactionPoolItem> iterator = allocatedTransactionList.iterator();
        while (iterator.hasNext()){
            TransactionPoolItem transactionPoolItem = iterator.next();
            if(transactionPoolItem.equals(transaction)&&transactionPoolItem.getMinerAddr().equals(minerAddr)){
                return true;
            }
        }
        return false;
    }


    /**
     * 检验区块中的交易是否是交易池中的，要检验交易本体是否相同以及是否是指定的miner
     * @param transaction
     * @return
     */
    public boolean finish(SignedTransaction transaction,Date finishTime){
        Iterator<TransactionPoolItem> iterator = allocatedTransactionList.iterator();
        while(iterator.hasNext()){
            TransactionPoolItem transactionPoolItem = iterator.next();
            if(transactionPoolItem.equals(transaction)){
                transactionPoolItem.setFinished(true);
                transactionPoolItem.setFinishTime(finishTime);
                iterator.remove();
                finishedTransactionList.add(transactionPoolItem);
                return true;
            }
        }
        return false;
    }
}

