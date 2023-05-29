package com.jhchen.framework.domain.modul;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.Synchronized;

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
            synchronized (transactionList){
                transactionList.add(transaction);
            }

    }

    public void addBatch(List<SignedTransaction> signedTransactionList){
        synchronized (transactionList){
            transactionList.addAll(signedTransactionList);
        }

    }


    /**
     * 获取n笔未分配的交易
     * @param num
     * @return
     */
    public List<SignedTransaction> getUnallocated(Integer num){
        List<SignedTransaction> res = new ArrayList<>();
        synchronized (transactionList){
            if(num> transactionList.size()){
                num = transactionList.size();
            }
            for (int i = 0; i < num; i++) {
                SignedTransaction next = transactionList.get(0);
                res.add(next);
                transactionList.remove(0);

            }
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
        for (int i = 0; i < transactionList.size(); i++) {
            SignedTransaction signedTransaction = transactionList.get(i);
            if (signedTransaction.equals(transaction)){
                synchronized (transactionList){
                    transactionList.remove(i);
                }
                break;
            }

        }


        TransactionPoolItem transactionPoolItem = new TransactionPoolItem(transaction, minerAddr, height);
        //查看是否已存在
        for (int i = 0; i < allocatedTransactionList.size(); i++) {
            TransactionPoolItem transactionPoolItem1 = allocatedTransactionList.get(i);
            if(transactionPoolItem1.equals(transactionPoolItem))
                return;
        }

        //加入已分配
        synchronized (allocatedTransactionList){
            allocatedTransactionList.add(transactionPoolItem);
        }



    }


    /**
     * 在已分配队列中寻找
     * @param transaction
     * @param minerAddr
     * @return
     */
    public boolean find(SignedTransaction transaction,String minerAddr, Integer height){
        for (int i = 0; i < allocatedTransactionList.size(); i++) {
            TransactionPoolItem transactionPoolItem = allocatedTransactionList.get(i);
            if(transactionPoolItem.equals(transaction)
                    && transactionPoolItem.getMinerAddr().equals(minerAddr)
                    &&transactionPoolItem.getHeight().equals(height)){
                return true;
            }
        }
        return false;
//        synchronized (allocatedTransactionList){
//            for (TransactionPoolItem transactionPoolItem : allocatedTransactionList) {
//                if(transactionPoolItem.equals(transaction)
//                        && transactionPoolItem.getMinerAddr().equals(minerAddr)
//                        &&transactionPoolItem.getHeight().equals(height)){
//                    return true;
//                }
//            }
//            return false;
//        }

    }




    /**
     * 检验区块中的交易是否是交易池中的，要检验交易本体是否相同以及是否是指定的miner
     * @param transaction
     * @return
     */
    public boolean finish(SignedTransaction transaction,Date finishTime){
        for (int i = 0; i < allocatedTransactionList.size(); i++) {
            TransactionPoolItem transactionPoolItem = allocatedTransactionList.get(i);
            if(transactionPoolItem.equals(transaction)){
                transactionPoolItem.setFinished(true);
                transactionPoolItem.setFinishTime(finishTime);
                synchronized (allocatedTransactionList){
                    allocatedTransactionList.remove(i);
                }
                synchronized (finishedTransactionList){
                    finishedTransactionList.add(transactionPoolItem);
                }
                return true;
            }
        }
//        synchronized (this){
//            for (TransactionPoolItem transactionPoolItem : allocatedTransactionList) {
//                if(transactionPoolItem.equals(transaction)){
//
//                    transactionPoolItem.setFinished(true);
//                    transactionPoolItem.setFinishTime(finishTime);
//                    allocatedTransactionList.remove(transactionPoolItem);
//                    finishedTransactionList.add(transactionPoolItem);
//                    return true;
//                }
//            }
//        }
        return false;
//        Iterator<TransactionPoolItem> iterator = allocatedTransactionList.iterator();
//        while(iterator.hasNext()){
//            TransactionPoolItem transactionPoolItem = iterator.next();
//            if(transactionPoolItem.equals(transaction)){
//                transactionPoolItem.setFinished(true);
//                transactionPoolItem.setFinishTime(finishTime);
//                iterator.remove();
//                finishedTransactionList.add(transactionPoolItem);
//                return true;
//            }
//        }
//        return false;
    }
}

