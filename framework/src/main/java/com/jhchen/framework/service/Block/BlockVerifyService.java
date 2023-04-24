package com.jhchen.framework.service.Block;

import com.alibaba.fastjson.JSON;
import com.jhchen.framework.domain.AppHttpCodeEnum;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Block;
import com.jhchen.framework.domain.modul.SignedTransaction;
import com.jhchen.framework.domain.modul.TransactionPool;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description 区块校验函数
 * @createDate 2023/3/22 17:21
 * @since 1.0.0
 */
public class BlockVerifyService {
    public Boolean verifyBlock(Block block){
        //验证hash
        String tarId = DigestUtils.sha256Hex(block.getPreID() + block.getMerkle() + block.getTimestamp());
        if(!tarId.equals(block.getId()))
            return false;

        //验证Nonce
        BigInteger targetValue = BigInteger.valueOf(1).shiftLeft((256 - block.getTargetBits()));
        String hash = block.getId()+block.getPreID()+block.getNonce().toString();
        String s = DigestUtils.sha256Hex(hash);
        BigInteger bigInteger = new BigInteger(s, 16);
        if (bigInteger.compareTo(targetValue) == -1){
            return true;
        }
        return false;
    }

    /**
     * 区块上链接口，验证区块并整理交易池
     * @param block
     * @param targetBits
     * @param blockChain
     * @param transactionPool
     * @return
     */
    public ResponseResult addBlock(Block block, String targetBits, List<Block> blockChain, TransactionPool transactionPool){
        Integer target = Integer.valueOf(targetBits);
        System.out.println("block:"+ JSON.toJSONString(block));

        int blockTargetBits = block.getTargetBits();

        System.out.println("1");
        //是否满足难度要求
        if(blockTargetBits<target){
            return ResponseResult.errorResult(AppHttpCodeEnum.BLOCK_NOT_VERIFIED);
        }
        //验证格式是否正确
        System.out.println("2");
        Boolean v =verifyBlock(block);
        System.out.println("3");
        System.out.println(v);
        if(v==false){
            return ResponseResult.errorResult(AppHttpCodeEnum.BLOCK_NOT_VERIFIED);
        }
        System.out.println("4");
        //验证包含的交易信息是否正确
        List<SignedTransaction> body = block.getBody();
        System.out.println("5");
        if(verifyBlockBody(body,block.getMinerAddr(),transactionPool,block.getHeight())==false){
            return ResponseResult.errorResult(AppHttpCodeEnum.BLOCK_NOT_VERIFIED);
        }
        System.out.println("6");
        //将交易移入已完成列表
        finishTrans(body,new Date(),transactionPool);
        //添加区块
        blockChain.add(block);
        return ResponseResult.okResult(block);
    }

    /**
     * 验证区块体，是否按照分配
     * @param body
     * @param minerAddr
     * @param transactionPool
     * @return
     */
    public Boolean verifyBlockBody(List<SignedTransaction> body, String minerAddr, TransactionPool transactionPool, Integer height){
        AtomicReference<Boolean> res = new AtomicReference<>(true);

        for (SignedTransaction item : body) {
            //在已分配交易列表中找到item
            Boolean flag = transactionPool.find(item,minerAddr,height);
            if(flag==false){
                res.set(false);
                break;
            }
        }
        return res.get();
    }

    public void finishTrans(List<SignedTransaction> body, Date date,TransactionPool transactionPool){
        for (SignedTransaction item : body) {
            transactionPool.finish(item,date);
        }
    }


}
