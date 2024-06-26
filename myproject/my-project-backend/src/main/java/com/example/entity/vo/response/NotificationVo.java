package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;

@Data
public class NotificationVo {
    Integer id;
    String title;
    String content;
    String type;
    String url;
    Date time;
}
