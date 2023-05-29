package com.jhchen.center.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import com.jhchen.framework.domain.modul.TransactionPoolItem;
import com.jhchen.framework.service.Block.BlockGenerateService;
import com.jhchen.framework.service.TransactionService;
import com.jhchen.framework.utils.HttpUtil;
import org.apache.commons.codec.digest.DigestUtils;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Queue;
import java.util.stream.Collectors;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 中央节点处理交易池
 * @createDate 2023/4/19 16:50
 * @since 1.0.0
 */
@Service
public class CenterTransService {
    @Autowired
    TransactionPool transactionPool;
    @Autowired
    @Qualifier("accountList")
    List<Account> accountList;
    @Autowired
    @Qualifier("waitList")
    Queue<Account> waitList;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BlockGenerateService blockGenerateService;
    @Autowired
    private Integer height;
    @Autowired
    @Qualifier("idList")
    private List<String> idList;

    /**
     * 添加新的交易记录
     * @param transaction
     * @return
     */
    public ResponseResult addTransaction(SignedTransaction transaction){
        if(transactionService.verify(transaction).equals(false)){
            return ResponseResult.errorResult(AppHttpCodeEnum.SIGN_NOT_VERIFY);
        }
        transactionPool.add(transaction);
        //announceTrans();
        return ResponseResult.okResult();
    }

    public ResponseResult addTransBatch(List<SignedTransaction> signedTransactionList){
        transactionPool.addBatch(signedTransactionList);
        return ResponseResult.okResult();
    }

    /**
     * 广播TransPool
     */
    public void announceTrans(){
        try {
            HttpUtil.broadcastMessage("/updateTrans",transactionPool.toString(),accountList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 分配并广播消息
     */

    public ResponseResult allocate(){
        //获取队列中第一个账户
        Account tarAccount = waitList.poll();
        if(tarAccount==null){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }
        waitList.add(tarAccount);

        //取出未分配的交易，默认1条
        List<SignedTransaction> unallocated = transactionPool.getUnallocated(10);
        if(unallocated.size()==0){
            return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
        }

        //计算id
        String merkle = blockGenerateService.generateHash(unallocated);
        String preID = idList.get(height-1);
        String id = DigestUtils.sha256Hex(preID + merkle);
        idList.add(id);

        // 包装成已分配交易
        List<TransactionPoolItem> collect = unallocated.stream().map(i -> {
            TransactionPoolItem transactionPoolItem = new TransactionPoolItem(i, tarAccount.getAddr(),height);
            //设置层数
            transactionPoolItem.setHeight(height);
            return transactionPoolItem;
        }).collect(Collectors.toList());
        unallocated.stream().forEach(i -> transactionPool.allocate(i, tarAccount.getAddr(),height));
        try {
            HttpUtil.broadcastMessage("/alloc", JSON.toJSONString(collect),accountList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        height++;
        return ResponseResult.okResult();
    }


}
