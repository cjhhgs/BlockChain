package com.jhchen.domain.modul;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("账户")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Account {
    @ApiModelProperty("地址")
    private String addr;
    @ApiModelProperty("公钥")
    private String publicKey;
    @ApiModelProperty("私钥")
    private String privateKey;
    @ApiModelProperty("ip地址")
    private String ip;
}
