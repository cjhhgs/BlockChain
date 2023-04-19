package com.jhchen.center.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import com.jhchen.framework.service.BlockChainService;
import com.jhchen.framework.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

@Service
public class CenterService {
    @Autowired
    private List<SignedTransaction> transactionList;
    @Autowired
    private TransactionPool transactionPool;
    @Autowired
    private  List<Account> accountList;
    @Autowired
    private List<Block> blockChain;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BlockChainService blockChainService;

    //添加新的交易记录
    public ResponseResult addTransaction(SignedTransaction transaction){
        if(transactionService.verify(transaction).equals(false)){
            return ResponseResult.errorResult(AppHttpCodeEnum.SIGN_NOT_VERIFY);
        }
        transactionPool.add(transaction);
        return ResponseResult.okResult();
    }

    public ResponseResult showTransactions(){
        return ResponseResult.okResult();
    }

    //注册挖矿账户
    public ResponseResult register(Account userAccount){
        return null;
      }

    @Value("${targetBits}")
    private String targetBits;



    //接受新区块
    public ResponseResult addBlock(Block block){
        Integer target = Integer.valueOf(targetBits);
        System.out.println("block:"+JSON.toJSONString(block));

        int targetBits = block.getTargetBits();

        System.out.println("1");
        //是否满足难度要求
        if(targetBits<target){
            return ResponseResult.errorResult(AppHttpCodeEnum.BLOCK_NOT_VERIFIED);
        }
        //验证格式是否正确
        System.out.println("2");
        Boolean v =blockChainService.verifyBlock(block);
        System.out.println("3");
        System.out.println(v);
        if(v==false){
            return ResponseResult.errorResult(AppHttpCodeEnum.BLOCK_NOT_VERIFIED);
        }
        System.out.println("4");
        //验证包含的交易信息是否正确
        List<SignedTransaction> body = block.getBody();
        System.out.println("5");
        if(verifyBlockBody(body,block.getMinerAddr())==false){
            return ResponseResult.errorResult(AppHttpCodeEnum.BLOCK_NOT_VERIFIED);
        }

        System.out.println("6");
        //将交易移入已完成列表
        finishTrans(body,new Date());
        //添加区块
        blockChain.add(block);
        return ResponseResult.okResult(block);
    }

    public Boolean verifyBlockBody(List<SignedTransaction> body,String minerAddr){
        AtomicReference<Boolean> res = new AtomicReference<>(true);
        Boolean result = true;
        //在已分配交易列表中找到item
        for (SignedTransaction item : body) {
            Boolean flag = transactionPool.find(item,minerAddr);
            if(flag==false){res.set(false);result = false;}
        }
        return res.get();
    }

    public void finishTrans(List<SignedTransaction> body, Date date){
        for (SignedTransaction item : body) {
            transactionPool.finish(item,date);
        }
    }
}
