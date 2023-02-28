package com.jhchen.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.domain.AppHttpCodeEnum;
import com.jhchen.domain.ResponseResult;
import com.jhchen.domain.modul.*;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@Service
public class CenterService {
    @Autowired
    private List<SignedTransaction> transactionList;
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
        transactionList.add(transaction);
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
        if(verifyBlockBody(body)==false){
            return ResponseResult.errorResult(AppHttpCodeEnum.BLOCK_NOT_VERIFIED);
        }
        System.out.println("6");
        blockChain.add(block);
        return ResponseResult.okResult(block);
    }

    public Boolean verifyBlockBody(List<SignedTransaction> body){
        AtomicReference<Boolean> res = new AtomicReference<>(true);
        Boolean result = true;
        for (SignedTransaction item : body) {
            Boolean flag = false;
            String s1 = JSON.toJSONString(item);
            System.out.println("body:"+JSON.toJSONString(item));
            for (SignedTransaction transaction : transactionList) {
                String s2 = JSON.toJSONString(transaction);
                System.out.println("tran:"+JSON.toJSONString(transaction));
                System.out.println(s1.equals(s2));
                if(s1.equals(s2)==true){
                    flag=true;
                    break;
                }
            }
            if(flag==false){res.set(false);result = false;}
        }
        System.out.println(res.get());
        System.out.println(result);
        return res.get();
    }

    public void finishTrans(List<SignedTransaction> body){
        for (SignedTransaction item : body) {
            for (SignedTransaction signedTransaction : transactionList) {
                if(signedTransaction.equals(item)){
                    signedTransaction.setFinished(true);
                    break;
                }
            }
        }
    }
}
