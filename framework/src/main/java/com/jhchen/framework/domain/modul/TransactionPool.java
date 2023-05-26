package com.jhchen.framework.domain.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 交易池
 * @createDate 2023/3/22 15:29
 * @since 1.0.0
 */
@Data
@AllArgsConstructor
public class TransactionPool {
    //未分配交易池
    public List<SignedTransaction> transactionList;
    //已分配交易池
    public List<TransactionPoolItem> allocatedTransactionList;
    //已完成交易池
    private List<TransactionPoolItem> finishedTransactionList;

    public TransactionPool(){
        transactionList = new ArrayList<>();
        allocatedTransactionList = new ArrayList<>();
        finishedTransactionList = new ArrayList<>();
    }

    public TransactionPool(TransactionPool transactionPool){
        transactionList = new LinkedList<>(transactionPool.getTransactionList());
        allocatedTransactionList = new ArrayList<>(transactionPool.getAllocatedTransactionList());
        finishedTransactionList = new ArrayList<>(transactionPool.getFinishedTransactionList());
    }

    public void copy(TransactionPool transactionPool){
        this.transactionList = new ArrayList<>(transactionPool.getTransactionList());
        this.allocatedTransactionList = new ArrayList<>(transactionPool.getAllocatedTransactionList());
        this.finishedTransactionList = new ArrayList<>(transactionPool.getFinishedTransactionList());
    }

    /**
     * 接收一笔新的交易，不重复
     * @param transaction
     */
    public void add(SignedTransaction transaction){
        AtomicReference<Boolean> flag = new AtomicReference<>(true);
        transactionList.stream().forEach(i -> {
            if(i.equals(transaction))
                flag.set(false);
        });
        if(flag.get()==true)
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
        Iterator<SignedTransaction> iterator = transactionList.iterator();

        for (int i = 0; i < num; i++) {
            SignedTransaction next = iterator.next();
            res.add(next);
            iterator.remove();
        }
        return res;
    }


    /**
     * 分配给一个人
     * @param transaction
     * @param minerAddr
     */
    public void allocate(SignedTransaction transaction, String minerAddr,Integer height){
        //在未分配中寻找是否有
        Iterator<SignedTransaction> iterator = transactionList.iterator();
        while (iterator.hasNext()){
            SignedTransaction next = iterator.next();
            if(next.equals(transaction))
                iterator.remove();
                break;
        }

        TransactionPoolItem transactionPoolItem = new TransactionPoolItem(transaction, minerAddr, height);
        //查看是否已存在
        Iterator<TransactionPoolItem> iterator1 = allocatedTransactionList.iterator();
        while (iterator1.hasNext()){
            TransactionPoolItem next = iterator1.next();
            if(next.equals(transactionPoolItem))
                return;
        }
        //加入已分配
        allocatedTransactionList.add(transactionPoolItem);
    }


    /**
     * 在已分配队列中寻找
     * @param transaction
     * @param minerAddr
     * @return
     */
    public boolean find(SignedTransaction transaction,String minerAddr, Integer height){
        Iterator<TransactionPoolItem> iterator = allocatedTransactionList.iterator();
        while (iterator.hasNext()){
            TransactionPoolItem transactionPoolItem = iterator.next();
            if(transactionPoolItem.equals(transaction)
                    && transactionPoolItem.getMinerAddr().equals(minerAddr)
                    &&transactionPoolItem.getHeight().equals(height)){
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

