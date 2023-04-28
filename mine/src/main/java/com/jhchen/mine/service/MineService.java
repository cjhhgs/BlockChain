package com.jhchen.mine.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.*;
import com.jhchen.framework.service.Block.BlockGenerateService;
import com.jhchen.framework.utils.HttpUtil;
import com.jhchen.mine.utils.JSONUtil;
import io.swagger.models.auth.In;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Map;

@Service
public class MineService {
    @Value("${centerAddr}")
    private String centerAddr;
    @Autowired
    @Qualifier("accountList")
    List<Account> accountList;

    @Value("${accountPath}")
    private String accountPath;
    @Autowired
    Account account;
    @Autowired
    public List<Block> blockChain;
    @Autowired
    public TransactionPool transactionPool;
    @Autowired
    public BlockGenerateService blockGenerateService;
    @Autowired
    public MineTransactionService mineTransactionService;

    public void saveAccount(Account account){
        if(account.equals(null)){
            return;
        }
        JSONUtil.createJsonFile(account,accountPath);
    }

    /**
     * 从文件加载账户信息
     */
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


    /**
     * 向其他节点注册
     */
    public ResponseResult register(){
        Account a = new Account();
        a.setAddr(account.getAddr());
        a.setIp(account.getIp());
        try {
            HttpUtil.broadcastMessage("/register",JSON.toJSONString(a),accountList);
            return ResponseResult.okResult();
        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
    }

    @Value("${targetBits}")
    private String targetBits;


    /**
     * 挖矿
     * @return
     */
    public ResponseResult mine(){
        //获取分配给自己的交易
        Map<Integer,List<SignedTransaction>> map = mineTransactionService.getTrans();

        //获取前区块头hash
        Integer target = Integer.valueOf(targetBits);
        int size = blockChain.size();
        Block last = blockChain.get(size-1);
        //开始构建
        for (Integer key : map.keySet()) {
            List<SignedTransaction> transactionList = map.get(key);
            Block block = blockGenerateService.generateBlock(transactionList, last.getId(), target, key);
            //添加到本地
            blockChain.add(block);
            //广播
            try {
                HttpUtil.broadcastMessage("/addBlock",JSON.toJSONString(block), accountList);
            } catch (IOException e) {
                return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
            }
        }
        return ResponseResult.okResult();
    }

    /**
     * 从中心获取区块链副本
     * @return
     */
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


    /**
     * 获取中央交易池副本
     * @return
     */
    public ResponseResult getTransactions(){
        try {
            String s = HttpUtil.get(centerAddr + "/showTransactions");
            JSONObject object = JSONObject.parseObject(s);
            System.out.println(object);
            TransactionPool data = JSON.parseObject(object.getString("data"), TransactionPool.class);
            transactionPool = data;
            return ResponseResult.okResult(transactionPool);
        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
    }




}
