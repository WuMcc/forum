package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoveCreateVo {
    @Length(min = 1, max = 30)
    String title;
    JSONObject content;
}
