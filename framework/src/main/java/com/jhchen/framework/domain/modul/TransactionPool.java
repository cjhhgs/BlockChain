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

    /**
     * 接收一笔新的交易
     * @param transaction
     */
    public void add(SignedTransaction transaction){
        transactionList.add(transaction);
    }


    /**
     * 获取n笔未分配的交易
     * @param num
     * @return
     */
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


    /**
     * 分配给一个人
     * @param transaction
     * @param minerAddr
     */
    public void allocate(SignedTransaction transaction, String minerAddr){
        //在未分配中寻找是否有
        Iterator<SignedTransaction> iterator = transactionList.iterator();
        Boolean flag = false;
        while (iterator.hasNext()){
            SignedTransaction next = iterator.next();
            if(next.equals(transaction))
                iterator.remove();
                break;
        }
        //加入已分配
        allocatedTransactionList.add(new TransactionPoolItem(transaction,minerAddr));
    }


    /**
     * 在已分配队列中寻找
     * @param transaction
     * @param minerAddr
     * @return
     */
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

