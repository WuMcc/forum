package com.example.entity.vo.response;

import lombok.Data;

import java.util.Date;
import java.util.List;

@Data
public class LovePreviewVo {
    int id;
    int type;
    String title;
    String text;
    List<String> images;
    Date time;
    Integer uid;
    String email;
    String username;
    String avatar;
}
