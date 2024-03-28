package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.AccountPrivacy;
import jakarta.validation.constraints.Max;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface AccountPrivacyMapper extends BaseMapper<AccountPrivacy> {
}
