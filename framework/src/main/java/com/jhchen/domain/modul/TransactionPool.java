package com.jhchen.domain.modul;

import lombok.Data;

import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 待处理交易池
 * @createDate 2023/3/22 15:29
 * @since 1.0.0
 */
@Data
public class TransactionPool {
    private List<SignedTransaction> transactionList;
    private List<TransactionPoolItem> allocatedTransactionList;
    private List<TransactionPoolItem> finishedTransactionList;

    public void add(SignedTransaction transaction){
        transactionList.add(transaction);
    }

    /**
     * 给一个矿工分配num条交易打包
     * @param num
     * @param minerAddr
     */
    public void allocate(Integer num,String minerAddr){
        if(num> transactionList.size()){
            num = transactionList.size();
        }
        Iterator<SignedTransaction> iterator = transactionList.iterator();
        for (int i = 0; i < num; i++) {
            SignedTransaction transaction = iterator.next();
            TransactionPoolItem transactionWrapper = new TransactionPoolItem(transaction, minerAddr);
            allocatedTransactionList.add(transactionWrapper);
            iterator.remove();
        }
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

