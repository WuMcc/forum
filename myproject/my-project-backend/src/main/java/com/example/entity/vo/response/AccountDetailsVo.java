package com.example.entity.vo.response;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.vo.BaseData;
import lombok.Data;

@Data
public class AccountDetailsVo implements BaseData {
    int gender;
    String phone;
    String qq;
    String wx;
    String desc;
}
