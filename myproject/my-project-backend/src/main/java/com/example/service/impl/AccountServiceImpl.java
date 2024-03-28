package com.example.service.impl;

import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.Account;
import com.example.entity.dto.AccountDetails;
import com.example.entity.dto.AccountPrivacy;
import com.example.entity.vo.request.*;
import com.example.mapper.AccountDetailsMapper;
import com.example.mapper.AccountMapper;
import com.example.mapper.AccountPrivacyMapper;
import com.example.service.AccountService;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Service
public class AccountServiceImpl extends ServiceImpl<AccountMapper, Account> implements AccountService{

    //验证邮件发送冷却时间限制，秒为单位
    @Value("${spring.web.verify.mail-limit}")
    int verifyLimit;

    @Resource
    FlowUtils flowUtils;

    @Resource
    AmqpTemplate amqpTemplate;

    @Resource
    StringRedisTemplate stringRedisTemplate;
    @Resource
    PasswordEncoder passwordEncoder;

    @Resource
    AccountPrivacyMapper privacyMapper;

    @Resource
    AccountDetailsMapper detailsMapper;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = this.findAccountByNameOrEmail(username);
        if (account == null)
            throw new UsernameNotFoundException("用户名或密码错误");
        return User
                .withUsername(username)
                .password(account.getPassword())
                .roles(account.getRole())
                .build();
    }


    public Account findAccountByNameOrEmail(String text){
        return this.query()
                .eq("username", text).or()
                .eq("email",text)
                .one();
    }

    @Override
    public Account findAccountById(int id) {
        return this.query().eq("id", id).one();
    }

    @Override
    public String registerEmailVerifyCode(String type, String email, String ip) {
        synchronized (ip.intern()) {
            if (!this.verifyLimit(ip))
                return "请求频繁，请稍后再试";
            Random random = new Random();
            int code = random.nextInt(899999) + 10000;
            Map<String, Object> data = Map.of("type", type, "email", email, "code", code);
            amqpTemplate.convertAndSend("mail", data);
            stringRedisTemplate.opsForValue()
                    .set(this.getKey(email), String.valueOf(code), 3, TimeUnit.MINUTES);
            return null;
        }
    }

    /**
     * 获取Redis中存储的邮件验证码
     * @param email 电邮
     * @return 验证码
     */
    private String getEmailVerifyCode(String email){
        String key = Const.VERIFY_EMAIL_DATA + email;
        return stringRedisTemplate.opsForValue().get(key);
    }


    @Override
    public String registerEmailAccount(EmailRegisterVo vo) {
        String email = vo.getEmail();
        String username = vo.getUsername();
        String key = this.getKey(email);
        String code = stringRedisTemplate.opsForValue().get(key);
        if(code == null) return "请先获取验证码！";
        if (!code.equals(vo.getCode())) return "验证码输入错误，请重新输入！";
        if (this.existsAccountByEmail(email)) return "此电子邮箱已被其他用户注册！";
        if (this.existsAccountByUsername(username)) return "此用户名已被其他人注册，请更新一个新的用户名！";
        String password = passwordEncoder.encode(vo.getPassword());
        Account account = new Account(null, vo.getUsername(), password, email, "user", null, new Date());
        if(this.save(account)){
            this.deleteEmailVerifyCode(email);
            privacyMapper.insert(new AccountPrivacy(account.getId()));
            AccountDetails details = new AccountDetails();
            details.setId(account.getId());
            detailsMapper.insert(details);
            return null;
        }else {
            return "内部错误，请联系管理员！";
        }
    }

    @Override
    public String resetConfirm(ConfirmResetVo vo) {
        String email = vo.getEmail();
        String code = stringRedisTemplate.opsForValue().get(this.getKey(email));
        if (code == null) return "请先获取验证码！";
        if (!code.equals(vo.getCode())) return "验证码错误，请重新输入验证码！";
        return null;
    }

    @Override
    public String resetEmailAccountPassword(EmailResetVo vo) {
        String email = vo.getEmail();
        String verify = this.resetConfirm(new ConfirmResetVo(vo.getEmail(), vo.getCode()));
        if (verify != null) return verify;
        String password = passwordEncoder.encode(vo.getPassword());
        boolean update = this.update().eq("email",email).set("password", password).update();
        if (update){
            stringRedisTemplate.delete(this.getKey(email));
        }
        return null;
    }

    @Override
    public String ModifyEmail(int id, ModifyEmailVo vo) {
        String email = vo.getEmail();
        String code = getEmailVerifyCode(email);
        if (code == null) return "请先获取验证码！";
        if (!code.equals(vo.getCode())) return "验证码错误，请重新输入";
        this.getEmailVerifyCode(email);
        Account account = this.findAccountByNameOrEmail(email);
        if (account != null && account.getId() != id)
            return "该电子邮箱已被其他账号绑定，无法完成此操作！";
        this.update()
                .set("email", email)
                .eq("id", id)
                .update();
        return null;
    }

    @Override
    public String changePassword(int id, ChangePasswordVo vo) {
        String password = this.query().eq("id", id).one().getPassword();
        if(!passwordEncoder.matches(vo.getPassword(), password))
            return "原密码错误，请重新输入！";
        boolean success = this.update()
                .eq("id", id)
                .set("password", passwordEncoder.encode(vo.getNew_password()))
                .update();
        return success ? null : "未知错误，请联系管理员";
    }

    private void deleteEmailVerifyCode(String email) {
        String key = this.getKey(email);
        stringRedisTemplate.delete(key);
    }

    public boolean verifyLimit(String ip){
        String key = this.getKey(ip);
        return flowUtils.limitOnceCheck(key, verifyLimit);
    }

    private boolean existsAccountByEmail(String email){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("email",email));
    }

    private boolean existsAccountByUsername(String username){
        return this.baseMapper.exists(Wrappers.<Account>query().eq("username",username));
    }
    private String getKey(String redis){
        return Const.VERIFY_EMAIL_DATA + redis;
    }
}
