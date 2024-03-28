package com.example.service;

import com.example.entity.dto.Interact;
import com.example.entity.dto.TopicType;
import com.example.entity.vo.request.AddCommentVo;
import com.example.entity.vo.request.TopicCreateVo;
import com.example.entity.vo.request.TopicUpdateVo;
import com.example.entity.vo.response.CommentVo;
import com.example.entity.vo.response.TopicDetailVo;
import com.example.entity.vo.response.TopicPreviewVo;
import com.example.entity.vo.response.TopicTopVo;
import jakarta.validation.Valid;

import java.util.List;

public interface TopicService {
    List<TopicType> listTypes();
    String createTopic(int uid, @Valid TopicCreateVo vo);
    List<TopicPreviewVo> listTopicByPage(int page, int type);
    List<TopicTopVo> listTopTopics();
    TopicDetailVo getTopic(int tid, int uid);
    void interact(Interact interact, boolean state);

    List<TopicPreviewVo> listTopicCollects(int uid);

    String updateTopic(int uid, TopicUpdateVo vo);

    String createComment(int uid, AddCommentVo vo);

    List<CommentVo> comments(int tid, int pageNumber);

    void deleteComment(int id, int uid);
}
