package com.jhchen.mine.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.service.BlockChainService;
import com.jhchen.framework.utils.HttpUtil;
import com.jhchen.mine.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;

@Service
public class MineService {
    @Value("${centerAddr}")
    private String centerAddr;

    @Value("${accountPath}")
    private String accountPath;
    @Autowired
    Account account;
    @Autowired
    public List<Block> blockChain;
    @Autowired
    public List<SignedTransaction> transactionList;
    @Autowired
    public BlockChainService blockChainService;

    public void saveAccount(Account account){
        if(account.equals(null)){
            return;
        }
        JSONUtil.createJsonFile(account,accountPath);
    }
    public void loadAccount(){
        Account a = null;
        try {
            a = JSONUtil.loadJSONObject(accountPath,Account.class);

        }catch (Exception e){
            e.printStackTrace();
        }
        if (!a.equals(null)){
            account.setPublicKey(a.getPublicKey());
            account.setPrivateKey(a.getPrivateKey());
            account.setAddr(a.getAddr());
            account.setIp(a.getIp());
        }

    }

    public void register(){
        Account a = new Account();
        a.setAddr(account.getAddr());
        a.setIp(account.getIp());

    }

    @Value("${targetBits}")
    private String targetBits;


    public ResponseResult mine(){
        System.out.println("4");
        Integer target = Integer.valueOf(targetBits);
        System.out.println("5");
        int size = blockChain.size();
        Block last = blockChain.get(size-1);
        //
        System.out.println("1");
        System.out.println(JSON.toJSONString(transactionList));
        //
        Block block = blockChainService.generateBlock(transactionList, last.getId(), target);
        //
        System.out.println("2");
        System.out.println(JSON.toJSONString(block));

        System.out.println("3");
        //
        Boolean a = blockChainService.verifyBlock(block);
        System.out.println("验证hash:"+a);
        blockChain.add(block);

        //广播
        try {
            String post = HttpUtil.post(centerAddr + "/addBlock", JSON.toJSONString(block));
            ResponseResult jsonObject = JSON.parseObject(post,ResponseResult.class);
            return jsonObject;
        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
    }

    public ResponseResult getBlockChainFromCenter(){
        String s = null;
        try {
            s = HttpUtil.get(centerAddr + "/showBlockChain");

        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
        System.out.println(s);
        JSONObject object = JSON.parseObject(s);

        //
        List<Block> list = JSON.parseArray(object.getString("data"),Block.class);

        blockChain.clear();
        for (Block block : list) {
            blockChain.add(new Block(block));
        }
        System.out.println("blockChain:"+blockChain);

        return ResponseResult.okResult(blockChain);


    }


    public ResponseResult getTransactions(){
        try {
            String s = HttpUtil.get(centerAddr + "/showTransactions");
            JSONObject object = JSONObject.parseObject(s);
            List<SignedTransaction> list = JSON.parseArray(object.getString("data"), SignedTransaction.class);

            transactionList.clear();
            for (SignedTransaction transaction : list) {
                transactionList.add(new SignedTransaction(transaction));
            }

            System.out.println(JSON.toJSONString("trans:"+transactionList));
            return ResponseResult.okResult(transactionList);
        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
    }




}
