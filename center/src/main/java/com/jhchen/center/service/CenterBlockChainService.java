package com.jhchen.center.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.jhchen.framework.domain.vo.CheckBlockVo;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import com.jhchen.framework.service.Block.BlockVerifyService;
import com.jhchen.framework.service.ECCService;
import com.jhchen.framework.service.TransactionService;
import com.jhchen.framework.utils.HttpUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.List;

@Service
public class CenterBlockChainService {
    @Autowired
    private List<SignedTransaction> transactionList;
    @Autowired
    private TransactionPool transactionPool;
    @Autowired
    @Qualifier("accountList")
    private  List<Account> accountList;
    @Autowired
    private List<Block> blockChain;
    @Autowired
    private TransactionService transactionService;
    @Autowired
    private BlockVerifyService blockVerifyService;
    @Autowired
    private ECCService eccService;
    @Autowired
    @Qualifier("ackList")
    List<Integer> ackList;

    @Value("${targetBits}")
    private String targetBits;


    /**
     * 接受一个新的区块并验证
     * @param block
     * @return
     */
    public ResponseResult addBlock(Block block){
        ResponseResult responseResult = blockVerifyService.addBlock(block, targetBits, blockChain, transactionPool);
        synchronized (ackList){
            if(responseResult.getCode()==AppHttpCodeEnum.SUCCESS.getCode()){
                while(ackList.size()<block.getHeight()+1){
                    ackList.add(0);
                }
                ackList.set(block.getHeight(),1);
            }
        }

        return responseResult;
    }

    public void finishBlock(Block block, Date date){
        blockVerifyService.finishTrans(block.getBody(),date,transactionPool);
        try {
            HttpUtil.broadcastMessage("/finishBlock", JSON.toJSONString(block),accountList);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    /**
     * 节点验证区块
     * @param checkBlockVo
     * @return
     */
    public ResponseResult checkBlock(CheckBlockVo checkBlockVo){
        //检查签名
        Block block = checkBlockVo.getBlock();
        Account account = checkBlockVo.getAccount();
        if(!eccService.eccVerify(account.getPublicKey(),block.toString(),checkBlockVo.getSign()))
            return ResponseResult.errorResult(AppHttpCodeEnum.SIGN_NOT_VERIFY);

        //ack+1
        synchronized (ackList){
            ackList.set(block.getHeight(), ackList.get(block.getHeight())+1);
        }

        return null;
    }

}
