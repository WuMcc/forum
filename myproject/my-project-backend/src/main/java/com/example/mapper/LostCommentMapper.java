package com.example.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.entity.dto.LostComment;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface LostCommentMapper extends BaseMapper<LostComment> {
}
