package com.example.listener;

import jakarta.annotation.Resource;
import org.springframework.amqp.rabbit.annotation.RabbitHandler;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
@RabbitListener(queues = "mail")
public class MailQueueListener {

    @Resource
    JavaMailSender sender;

    @Value("${spring.mail.username}")
    String username;

    @RabbitHandler(isDefault = true)
    public void sendMailMessage(Map<String, Object> data){
        String email = data.get("email").toString();
        Integer code = (Integer) data.get("code");
        String type = data.get("type").toString();
        SimpleMailMessage message = switch (type){
            case "register" ->creatMessage("欢迎注册我们的网站",
                    "您的邮件注册验证码为: " + code +",有效时间3分种,为了保障您的安全,请勿向他人泄露验证码信息。", email);
            case "reset" -> creatMessage("你您的密码重置邮件", "你好，您正在进行重置密码操作, 验证码: "+ code +",有效时间3分钟，如非本人操作请无视。", email);
            case "modify" ->
                creatMessage("您的邮箱修改验证邮件",
                        "您好，您正在绑定新的电子邮件地址，验证码："+code+"，有效时间3分钟，如非本人操作，请忽略。",email);
            default -> null;
        };
        if (message == null) return;
        sender.send(message);
    }

    private SimpleMailMessage creatMessage(String title, String content, String email){
        SimpleMailMessage message = new SimpleMailMessage();
        message.setSubject(title);
        message.setText(content);
        message.setTo(email);
        message.setFrom(username);
        return message;
    }
}
