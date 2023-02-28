package com.jhchen.controller;

import com.jhchen.domain.ResponseResult;
import com.jhchen.domain.modul.Block;
import com.jhchen.service.CenterService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "处理区块")
public class BlockChainController {
    @Autowired
    CenterService centerService;
    @Autowired
    List<Block> blockChain;

    @GetMapping("/showBlockChain")
    public ResponseResult showBlockChain(){
        return ResponseResult.okResult(blockChain);
    }

    @Value("${targetBits}")
    private String targetBits;
    private Integer target = Integer.getInteger(targetBits);

    @PostMapping("/addBlock")
    public ResponseResult addBlock(@RequestBody Block block){
        return centerService.addBlock(block);

    }
}
