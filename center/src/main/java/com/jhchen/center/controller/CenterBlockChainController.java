package com.jhchen.center.controller;

import com.jhchen.center.service.CenterBlockChainService;
import com.jhchen.framework.domain.ResponseResult;
import com.jhchen.framework.domain.modul.Block;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@Api(tags = "处理区块")
public class CenterBlockChainController {
    @Autowired
    CenterBlockChainService centerBlockChainService;
    @Autowired
    List<Block> blockChain;
    @Autowired
    List<String> idList;


    @GetMapping("/showBlockChain")
    public ResponseResult showBlockChain(){
        return ResponseResult.okResult(blockChain);
    }

    @Value("${targetBits}")
    private String targetBits;
    private Integer target = Integer.getInteger(targetBits);

    @PostMapping("/addBlock")
    public ResponseResult addBlock(@RequestBody Block block){
        return centerBlockChainService.addBlock(block);

    }

    @GetMapping("/getIdList")
    public ResponseResult getIdList(){
        return ResponseResult.okResult(idList);
    }
}
