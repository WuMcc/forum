package com.example.entity.vo.response;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

@Data
public class LostDetailVo {
    Integer id;
    String title;
    String content;
    Integer type;
    Date time;
    LostDetailVo.User user;
    Long comments;

    @Data
    public static class User{
        Integer id;
        String username;
        String avatar;
        String desc;
        boolean gender;
        String qq;
        String wx;
        String phone;
        String email;
    }
}
