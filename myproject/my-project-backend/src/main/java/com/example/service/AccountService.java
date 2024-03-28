package com.example.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.example.entity.dto.Account;
import com.example.entity.vo.request.*;
import org.springframework.security.core.userdetails.UserDetailsService;

public interface AccountService extends IService<Account>, UserDetailsService {
    Account findAccountByNameOrEmail(String text);
    Account findAccountById(int id);
    String registerEmailVerifyCode(String type, String mail, String ip);
    String registerEmailAccount(EmailRegisterVo vo);
    String resetConfirm(ConfirmResetVo vo);
    String resetEmailAccountPassword(EmailResetVo vo);
    String ModifyEmail(int id, ModifyEmailVo vo);
    String changePassword(int id, ChangePasswordVo vo);

}
