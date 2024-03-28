package com.example.entity.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.example.entity.vo.BaseData;
import lombok.Data;

import java.util.Date;

@Data
@TableName("db_topic")
public class Topic implements BaseData {
    @TableId(type = IdType.AUTO)
    Integer id;
    String title;
    String content;
    Integer type;
    Date time;
    Integer uid;
}
