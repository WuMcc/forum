package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class LoveDetailVo {
    Integer id;
    String title;
    String content;
    Integer type;
    Date time;
    LoveDetailVo.User user;
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
