package com.jhchen.framework.domain.vo;

import com.jhchen.framework.domain.modul.Account;
import com.jhchen.framework.domain.modul.Block;
import lombok.Data;

/**
 * @author chen.jiahao
 * @version 1.0.0
 * @description
 * @createDate 2023/5/26 10:01
 * @since 1.0.0
 */
@Data
public class CheckBlockVo {
    Account account;
    Block block;
    String sign;

}
