package com.jhchen.framework.domain.modul;


import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@ApiModel("区块")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Block {
    @ApiModelProperty("区块高度")
    private Integer height;
    @ApiModelProperty("区块id")
    private String id;
    @ApiModelProperty("前区块id")
    private String preID;
    @ApiModelProperty("merkle根")
    private String merkle;
    @ApiModelProperty("难度系数")
    private int targetBits;
    @ApiModelProperty("工作量证明")
    private Integer nonce;
    @ApiModelProperty("时间戳")
    private Long timestamp = 0L;
    @ApiModelProperty("交易列表")
    private List<SignedTransaction> body;
    @ApiModelProperty("矿工addr")
    private String minerAddr;

    public Block(Block block) {
        System.out.println(block.toString());
        //
        this.height = block.getHeight();
        this.id = block.getId();
        this.preID = block.getPreID();
        this.targetBits = block.getTargetBits();
        this.nonce = block.getNonce();
        this.timestamp = block.getTimestamp();
        body = new ArrayList<>();
        //
        if(!(block.getBody()==null)){
            for (SignedTransaction signedTransaction : block.getBody()) {
                body.add(new SignedTransaction(signedTransaction));
            }
        }

    }

    public static Block genesisBlock(){
        Block block = new Block();
        block.setId("0");
        block.setHeight(0);
        return block;
    }

}
