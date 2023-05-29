package com.jhchen.mine.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.*;
import com.jhchen.framework.domain.vo.CheckBlockVo;
import com.jhchen.framework.service.Block.BlockGenerateService;
import com.jhchen.framework.service.Block.BlockVerifyService;
import com.jhchen.framework.service.ECCService;
import com.jhchen.framework.utils.HttpUtil;
import com.jhchen.mine.utils.JSONUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class MineService {
    @Value("${centerAddr}")
    private String centerAddr;
    @Value("${accountPath}")
    private String accountPath;
    @Autowired
    @Qualifier("accountList")
    List<Account> accountList;

    @Autowired
    Account account;
    @Autowired
    public List<Block> blockChain;
    @Autowired
    public TransactionPool transactionPool;
    @Autowired
    public BlockGenerateService blockGenerateService;
    @Autowired
    public BlockVerifyService blockVerifyService;
    @Autowired
    private ECCService eccService;
    @Autowired
    public MineTransactionService mineTransactionService;

    public void saveAccount(Account account){
        if(account.equals(null)){
            return;
        }
        File directory = new File("./mine/"+accountPath);
        try {
            String path = directory.getCanonicalPath();
            JSONUtil.createJsonFile(account,path);
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    /**
     * 从文件加载账户信息
     */
    public void loadAccount(){
        Account a = null;
        File directory = new File("./mine/"+accountPath);
        try {
            String path = directory.getCanonicalPath();
            a = JSONUtil.loadJSONObject(path,Account.class);

        }catch (Exception e){
            e.printStackTrace();
        }
        if (a != null){
            account.setPublicKey(a.getPublicKey());
            account.setPrivateKey(a.getPrivateKey());
            account.setAddr(a.getAddr());
            account.setIp(a.getIp());
        }
    }


    /**
     * 向其他节点注册
     */
    public ResponseResult registerToOther(){
        getAccountList();

        Account a = new Account();
        a.setAddr(account.getAddr());
        a.setIp(account.getIp());
        System.out.println(accountList);

        try {
            HttpUtil.broadcastMessage("/register",JSON.toJSONString(a),accountList);
            return ResponseResult.okResult();
        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
    }

    /**
     * 向中心申请入对
     * @return
     */
    public ResponseResult inQueue(){
        try {
            HttpUtil.post(centerAddr+"/queue",JSON.toJSONString(account));
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
    @Async
    public ResponseResult mine(){
        //获取分配给自己的交易
        Map<Integer,List<SignedTransaction>> map = mineTransactionService.getTrans();

        //获取前区块头hash
        List<String> idList = getIdList();
        Integer target = Integer.valueOf(targetBits);
        int size = blockChain.size();
        Block last = blockChain.get(size-1);
        //开始构建
        for (Integer height : map.keySet()) {
            //查看是否已经挖过
            if(blockChain.size()>height && blockChain.get(height)!=null){
                continue;
            }
            //body
            List<SignedTransaction> transactionList = map.get(height);
            //preId
            if(idList.size()<height)
                return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
            String preId = idList.get(height-1);
            //generate
            Block block = blockGenerateService.generateBlock(transactionList, preId, target, height);
            block.setMinerAddr(account.getAddr());

            //添加到本地
            while(blockChain.size()<block.getHeight()+1){
                blockChain.add(null);
            }
            blockChain.set(block.getHeight(),block);
            //广播
            try {
                HttpUtil.broadcastMessage("/addBlock",JSON.toJSONString(block), accountList);
            } catch (IOException e) {
                return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
            }
        }
        inQueue();
        return ResponseResult.okResult(blockChain);
    }

    /**
     * 添加区块
     * @param block
     * @return
     */
    public ResponseResult addBlock(Block block){
        //验证
        if(!(blockVerifyService.addBlock(block,targetBits,blockChain,transactionPool).getCode()==200)){
            return ResponseResult.errorResult(AppHttpCodeEnum.BLOCK_NOT_VERIFIED);
        }
        //ack
        try {
            String sign = eccService.eccSign(account.getPrivateKey(),block.toString());
            CheckBlockVo checkBlockVo = new CheckBlockVo();
            checkBlockVo.setBlock(block);
            checkBlockVo.setSign(sign);
            checkBlockVo.setAccount(account);
            HttpUtil.post(centerAddr+"/checkBlock",JSON.toJSONString(checkBlockVo));
            return ResponseResult.okResult();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
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
     * 请求id列表
     * @return
     */
    private List<String> getIdList(){
        try {
            String s = HttpUtil.get(centerAddr+"/getIdList");
            ResponseResult<List<String>> res = new ResponseResult<>();
            ResponseResult<List<String>> responseResult = JSON.parseObject(s, res.getClass());
            List<String> idList = responseResult.getData();
            return idList;
        } catch (IOException e) {
            return null;
        }

    }


    /**
     * 获取中央交易池副本
     * @return
     */
    public ResponseResult getTransactions(){
        try {
            String s = HttpUtil.get(centerAddr + "/showTransactions");
            ResponseResult<TransactionPool> o = new ResponseResult<TransactionPool>();
            ResponseResult responseResult = JSON.parseObject(s, o.getClass());
            TransactionPool object = JSON.parseObject(responseResult.getData().toString(), TransactionPool.class);
            transactionPool.copy(object);
            System.out.println(transactionPool);
            return ResponseResult.okResult(transactionPool);
        } catch (IOException e) {
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);
        }
    }

    /**
     * 从中心获取账户列表
     */
    public void getAccountList(){
        try{
            String s = HttpUtil.get(centerAddr + "/showAccount");
            ResponseResult<List<Account>> o = new ResponseResult<>();
            ResponseResult responseResult = JSON.parseObject(s, o.getClass());
            List<Account> al = JSON.parseArray(responseResult.getData().toString(), Account.class);
            // 筛选出al中有而accountList中没有的account
            al.removeAll(accountList);
            accountList.addAll(al);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * 接受其他节点的注册
     * @param account1
     * @return
     */
    public ResponseResult register(Account account1){
        if(account1==null || account1.getAddr()==null)
            return ResponseResult.errorResult(AppHttpCodeEnum.HTTP_ERROR);

        if(account.getAddr().equals(account1.getAddr())){
            return ResponseResult.okResult();
        }

        List<Account> collect = accountList.stream().filter(i -> i.getAddr().equals(account1.getAddr())).collect(Collectors.toList());
        if(collect.isEmpty()){
            accountList.add(account1);

            return ResponseResult.okResult();
        }
        return ResponseResult.errorResult(AppHttpCodeEnum.SYSTEM_ERROR);
    }

    public ResponseResult finishBlock(Block block){
        blockVerifyService.finishTrans(block.getBody(),new Date(),transactionPool);
        return ResponseResult.okResult();
    }



}
