package com.example.entity.vo.request;

import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class PrivacySaveVo {
    @Pattern(regexp = "(phone|email|qq|wx|gender)")//每次勾选哪个传入对应类型，节省性能
    String type;
    boolean status;
}
