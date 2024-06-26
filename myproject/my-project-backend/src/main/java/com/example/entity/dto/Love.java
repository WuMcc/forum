package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_love")
public class Love {
    @TableId(type = IdType.AUTO)
    Integer id;
    String title;
    String content;
    Date time;
    Integer uid;
}
