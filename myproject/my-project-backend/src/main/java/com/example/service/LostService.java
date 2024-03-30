package com.example.service;

import com.example.entity.vo.request.*;
import com.example.entity.vo.response.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;

import java.util.List;

public interface LostService {
    String createLost(int uid, @Valid LostCreateVo vo);
    List<LostPreviewVo> listLostByPage(int page);
    LostDetailVo getLost(int tid, int uid);
    String updateLost(int uid, LostUpdateVo vo);

    String createComment(int uid, AddCommentVo vo);

    List<CommentVo> comments(int tid, int pageNumber);

    void deleteComment(int id, int uid);
}
