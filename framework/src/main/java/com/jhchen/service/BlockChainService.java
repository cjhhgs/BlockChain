package com.jhchen.service;

import com.alibaba.fastjson.JSON;
import com.jhchen.domain.modul.Block;
import com.jhchen.domain.modul.SignedTransaction;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Service;

import java.math.BigInteger;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BlockChainService {
    public Boolean verifyBlock(Block block){
        BigInteger targetValue = BigInteger.valueOf(1).shiftLeft((256 - block.getTargetBits()));

        String hash = block.getId()+block.getPreID()+block.getNonce().toString();
        String s = DigestUtils.sha256Hex(hash);
        BigInteger bigInteger = new BigInteger(s, 16);
        if (bigInteger.compareTo(targetValue) == -1){
            return true;
        }
        return false;
    }



    public String generateHash(List<SignedTransaction> list){
        if(list.size()==0)return null;

        List<String> hashList = list.stream().map((item) -> DigestUtils.sha256Hex(item.toString())).collect(Collectors.toList());

        while(hashList.size()>1){
            if(hashList.size()%2==1){
                hashList.add(hashList.get(hashList.size()-1));
            }
            List<String> temp = new ArrayList<>();
            for (int i = 0; i < hashList.size(); i+=2) {
                String h = DigestUtils.sha256Hex(hashList.get(i)+hashList.get(i+1));
                temp.add(h);
            }
            hashList = temp;
        }
        return hashList.get(0);

    }

    public Integer calculateNonce(Block block){

        BigInteger targetValue = BigInteger.valueOf(1).shiftLeft((256 - block.getTargetBits()));
        long startTime = System.currentTimeMillis();
        String id = block.getId();
        String preID = block.getPreID();
        Integer nonce = 0;
        while (nonce<Integer.MAX_VALUE){
            String hash = id+preID+nonce.toString();
            String s = DigestUtils.sha256Hex(hash);
            BigInteger bigInteger = new BigInteger(s, 16);
            if (bigInteger.compareTo(targetValue) == -1) {
                System.out.printf("Elapsed Time: %s seconds \n", (float) (System.currentTimeMillis() - startTime) / 1000);
                System.out.printf("correct hash Hex: %s \n\n", s);
                break;
            } else {
                nonce++;
            }
        }
        block.setNonce(nonce);

        return nonce;
    }

    public Block generateBlock(List<SignedTransaction> body,String preID,Integer targetBits){
        String id = generateHash(body);
        Block block = new Block();
        System.out.println(JSON.toJSONString(block));

        List<SignedTransaction> list = body.stream().collect(Collectors.toList());
        //
        System.out.println(JSON.toJSONString(body));
        System.out.println(JSON.toJSONString(list));
        block.setBody(list);
        block.setId(id);
        block.setPreID(preID);
        block.setTimestamp(Instant.now().getEpochSecond());

        block.setTargetBits(targetBits);

        Integer n =calculateNonce(block);
        block.setNonce(n);

        return block;
    }
}
