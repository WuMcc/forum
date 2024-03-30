package com.example.controller;

import com.example.entity.RestBean;
import com.example.entity.dto.Interact;
import com.example.entity.dto.LostComment;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.*;
import com.example.service.LostService;
import com.example.service.TopicService;
import com.example.service.WeatherService;
import com.example.utils.Const;
import com.example.utils.ControllerUtils;
import jakarta.annotation.Resource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Pattern;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/forum")
public class ForumController {

    @Resource
    WeatherService service;
    @Resource
    TopicService topicService;
    @Resource
    ControllerUtils utils;
    @Resource
    LostService lostService;

    @GetMapping("/weather")
    public RestBean<WeatherVo> weather(double longitude, double latitude){
        WeatherVo vo = service.fetchWeather(longitude,latitude);
        return vo == null ?
                RestBean.failure(400,"获取地理位置信息与天气失效，请联系管理员！") : RestBean.success(vo);

    }
    @GetMapping("/types")
    public RestBean<List<TopicTypeVo>> listTypes(){
        return RestBean.success(topicService
                .listTypes()
                .stream()
                .map(type -> type.asViewObject(TopicTypeVo.class))
                .toList());
    }

    @PostMapping("/create-topic")
    public RestBean<Void> createTopic(@Valid @RequestBody TopicCreateVo vo,
                                      @RequestAttribute(Const.ATTR_USER_ID)int id){
        return utils.messageHandle(() -> topicService.createTopic(id, vo));

    }

    @GetMapping("/list-topic")
    public RestBean<List<TopicPreviewVo>> listTopic(@RequestParam @Min(0) int page,
                                                    @RequestParam @Min(0) int type){
        return RestBean.success(topicService.listTopicByPage(page + 1, type));
    }

    @GetMapping("/top-topic")
    public RestBean<List<TopicTopVo>> topTopic(){
        return RestBean.success(topicService.listTopTopics());
    }

    @GetMapping("/topic")
    public RestBean<TopicDetailVo> topic(@RequestParam @Min(0) int tid,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(topicService.getTopic(tid, id));
    }

    @GetMapping("/interact")
    public RestBean<Void> interact(@RequestParam @Min(0) int tid,
                                   @RequestParam @Pattern(regexp = "(like|collect)") String type,
                                   @RequestParam boolean state,
                                   @RequestAttribute(Const.ATTR_USER_ID) int id) {
        topicService.interact(new Interact(tid, id, new Date(), type), state);
        return RestBean.success();
    }

    @GetMapping("/collects")
    public RestBean<List<TopicPreviewVo>> collects(@RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(topicService.listTopicCollects(id));
    }

    @PostMapping("/update-topic")
    public RestBean<Void> updateTopic(@Valid @RequestBody TopicUpdateVo vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id){
        return utils.messageHandle(() -> topicService.updateTopic(id, vo));
    }

    @PostMapping("/add-comment")
    public RestBean<Void> addComment(@Valid @RequestBody AddCommentVo vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int id){
        return utils.messageHandle(() -> topicService.createComment(id, vo));
    }

    @GetMapping("/comments")
    public RestBean<List<CommentVo>> comments(@RequestParam @Min(0) int tid,
                                              @RequestParam @Min(0) int page){
        return RestBean.success(topicService.comments(tid, page + 1));
    }

    @GetMapping("/delete-comment")
    public RestBean<Void> deleteComment(@RequestParam @Min(0) int id,
                                        @RequestAttribute(Const.ATTR_USER_ID) int uid){
        topicService.deleteComment(id, uid);
        return RestBean.success();
    }

    @PostMapping("/create-lost")
    public RestBean<Void> createTopic(@Valid @RequestBody LostCreateVo vo,
                                      @RequestAttribute(Const.ATTR_USER_ID)int id){
        return utils.messageHandle(() -> lostService.createLost(id, vo));

    }

    @GetMapping("/list-lost")
    public RestBean<List<LostPreviewVo>> listLost(@RequestParam @Min(0) int page){
        return RestBean.success(lostService.listLostByPage(page + 1));
    }


    @GetMapping("/lost")
    public RestBean<LostDetailVo> lost(@RequestParam @Min(0) int tid,
                                         @RequestAttribute(Const.ATTR_USER_ID) int id){
        return RestBean.success(lostService.getLost(tid, id));
    }

    @PostMapping("/update-lost")
    public RestBean<Void> updateLost(@Valid @RequestBody LostUpdateVo vo,
                                      @RequestAttribute(Const.ATTR_USER_ID) int id){
        return utils.messageHandle(() -> lostService.updateLost(id, vo));
    }

    @PostMapping("/add-lost-comment")
    public RestBean<Void> addLostComment(@Valid @RequestBody AddCommentVo vo,
                                     @RequestAttribute(Const.ATTR_USER_ID) int id){
        return utils.messageHandle(() -> lostService.createComment(id, vo));
    }

    @GetMapping("/lost-comments")
    public RestBean<List<CommentVo>> LostComments(@RequestParam @Min(0) int tid,
                                              @RequestParam @Min(0) int page){
        return RestBean.success(lostService.comments(tid, page + 1));
    }

    @GetMapping("/delete-lost-comment")
    public RestBean<Void> deleteLostComment(@RequestParam @Min(0) int id,
                                        @RequestAttribute(Const.ATTR_USER_ID) int uid){
        lostService.deleteComment(id, uid);
        return RestBean.success();
    }


}
