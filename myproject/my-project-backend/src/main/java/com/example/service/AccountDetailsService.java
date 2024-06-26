package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.AccountDetails;
import com.example.entity.vo.request.DetailsSaveVo;

public interface AccountDetailsService extends IService<AccountDetails> {
    AccountDetails findAccountDetailsById(int id);
    boolean saveAccountDetails(int id, DetailsSaveVo vo);
}
