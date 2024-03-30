package com.example.entity.vo.request;

import com.alibaba.fastjson2.JSONObject;
import jakarta.validation.constraints.Min;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class LoveUpdateVo {
    @Min(0)
    int id;
    @Length(min = 1, max = 30)
    String title;
    JSONObject content;
}
