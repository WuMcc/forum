package com.example.service;

import com.example.entity.vo.request.AddCommentVo;
import com.example.entity.vo.request.LoveCreateVo;
import com.example.entity.vo.request.LoveUpdateVo;
import com.example.entity.vo.response.CommentVo;
import com.example.entity.vo.response.LostDetailVo;
import com.example.entity.vo.response.LoveDetailVo;
import com.example.entity.vo.response.LovePreviewVo;
import jakarta.validation.Valid;

import java.util.List;

public interface LoveService {
    String createLove(int uid, @Valid LoveCreateVo vo);
    List<LovePreviewVo> listLoveByPage(int page);
    LoveDetailVo getLove(int tid, int uid);
    String updateLove(int uid, LoveUpdateVo vo);

    String createComment(int uid, AddCommentVo vo);

    List<CommentVo> comments(int tid, int pageNumber);

    void deleteComment(int id, int uid);
}
