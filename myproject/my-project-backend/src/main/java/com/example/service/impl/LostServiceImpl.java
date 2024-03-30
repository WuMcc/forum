package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.AddCommentVo;
import com.example.entity.vo.request.LostCreateVo;
import com.example.entity.vo.request.LostUpdateVo;
import com.example.entity.vo.response.CommentVo;
import com.example.entity.vo.response.LostDetailVo;
import com.example.entity.vo.response.LostPreviewVo;
import com.example.mapper.*;
import com.example.service.LostService;
import com.example.service.NotificationService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import jakarta.validation.constraints.Min;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Service
public class LostServiceImpl extends ServiceImpl<LostMapper, Lost> implements LostService{
    @Resource
    FlowUtils flowUtils;

    @Resource
    CacheUtils cacheUtils;

    @Resource
    AccountMapper accountMapper;

    @Resource
    AccountDetailsMapper accountDetailsMapper;

    @Resource
    AccountPrivacyMapper accountPrivacyMapper;

    @Resource
    LostCommentMapper commentMapper;

    @Resource
    StringRedisTemplate template;

    @Resource
    NotificationService notificationService;


    @Override
    public String createLost(int uid, LostCreateVo vo) {
        if(!textLimitCheck(vo.getContent(), 20000))
            return "帖子内容太多，发文失败！";
        String key = Const.FORUM_LOST_CREATE_COUNTER + uid;
        if(!flowUtils.limitPeriodCounterCheck(key, 3, 3600))
            return "发贴频繁，请稍后再试！";
        Lost lost = new Lost();
        BeanUtils.copyProperties(vo, lost);
        lost.setContent(vo.getContent().toJSONString());
        lost.setUid(uid);
        lost.setTime(new Date());
        if(this.save(lost)) {
            cacheUtils.deleteCachePattern(Const.FORUM_LOST_PREVIEW_CACHE + "*");
            return null;
        } else {
            return "内部错误，请联系管理员！";
        }
    }

    @Override
    public String updateLost(int uid, LostUpdateVo vo) {
        if(!textLimitCheck(vo.getContent(), 20000))
            return "文章内容太多，发文失败！";
        baseMapper.update(null, Wrappers.<Lost>update()
                .eq("uid", uid)
                .eq("id", vo.getId())
                .set("title", vo.getTitle())
                .set("content", vo.getContent().toString())
        );
        return null;
    }

    @Override
    public String createComment(int uid, AddCommentVo vo) {
        if(!textLimitCheck(JSONObject.parseObject(vo.getContent()), 2000))
            return "评论内容太多，发表失败！";
        String key = Const.FORUM_LOST_COMMENT_COUNTER + uid;
        if(!flowUtils.limitPeriodCounterCheck(key, 2, 60))
            return "发表评论频繁，请稍后再试！";
        LostComment comment = new LostComment();
        comment.setUid(uid);
        BeanUtils.copyProperties(vo, comment);
        comment.setTime(new Date());
        commentMapper.insert(comment);
        Lost lost = baseMapper.selectById(vo.getTid());
        Account account = accountMapper.selectById(uid);
        if(vo.getQuote() > 0) {
            LostComment com = commentMapper.selectById(vo.getQuote());
            if(!Objects.equals(account.getId(), com.getUid())) {
                notificationService.addNotification(
                        com.getUid(),
                        "您有新的帖子评论回复",
                        account.getUsername()+" 回复了你发表的评论，快去看看吧！",
                        "success", "/index/lost-detail/"+com.getTid()
                );
            }
        } else if (!Objects.equals(account.getId(), lost.getUid())) {
            notificationService.addNotification(
                    lost.getUid(),
                    "您有新的帖子回复",
                    account.getUsername()+" 回复了你发表主题: "+lost.getTitle()+"，快去看看吧！",
                    "success", "/index/lost/lost-detail/"+lost.getId()
            );
        }
        return null;
    }

    @Override
    public List<CommentVo> comments(int tid, int pageNumber) {
        Page<LostComment> page = Page.of(pageNumber, 10);
        commentMapper.selectPage(page, Wrappers.<LostComment>query().eq("tid", tid));
        return page.getRecords().stream().map(dto -> {
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(dto, vo);
            if(dto.getQuote() > 0) {
                LostComment comment = commentMapper.selectOne(Wrappers.<LostComment>query()
                        .eq("id", dto.getQuote()).orderByAsc("time"));
                if(comment != null) {
                    JSONObject object = JSONObject.parseObject(comment.getContent());
                    StringBuilder builder = new StringBuilder();
                    this.shortContent(object.getJSONArray("ops"), builder, ignore -> {});
                    vo.setQuote(builder.toString());
                } else {
                    vo.setQuote("此评论已被删除");
                }
            }
            CommentVo.User user = new CommentVo.User();
            vo.setUser(this.fillUserDetailsByPrivacy(user, dto.getUid()));
            return vo;
        }).toList();
    }

    @Override
    public void deleteComment(int id, int uid) {
        commentMapper.delete(Wrappers.<LostComment>query().eq("id", id).eq("uid", uid));
    }

    @Override
    public List<LostPreviewVo> listLostByPage(int pageNumber) {
        String key = Const.FORUM_LOST_PREVIEW_CACHE + pageNumber;
        List<LostPreviewVo> list = cacheUtils.takeListFromCache(key, LostPreviewVo.class);
        if(list != null)
            return list;
        Page<Lost> page = Page.of(pageNumber, 10);
            baseMapper.selectPage(page, Wrappers.<Lost>query().orderByDesc("time"));
        List<Lost> losts = page.getRecords();
        if(losts.isEmpty()) return null;
        list = losts.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }


    @Override
    public LostDetailVo getLost(int tid, int uid) {
        LostDetailVo vo = new LostDetailVo();
        Lost lost = baseMapper.selectById(tid);
        BeanUtils.copyProperties(lost, vo);
        LostDetailVo.User user = new LostDetailVo.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, lost.getUid()));
        vo.setComments(commentMapper.selectCount(Wrappers.<LostComment>query().eq("tid", tid)));
        return vo;
    }

    private <T> T fillUserDetailsByPrivacy(T target, int uid){
        AccountDetails details = accountDetailsMapper.selectById(uid);
        Account account = accountMapper.selectById(uid);
        AccountPrivacy accountPrivacy = accountPrivacyMapper.selectById(uid);
        String[] ignores = accountPrivacy.hiddenFields();
        BeanUtils.copyProperties(account, target, ignores);
        BeanUtils.copyProperties(details, target, ignores);
        return target;
    }

    private LostPreviewVo resolveToPreview(Lost lost) {
        LostPreviewVo vo = new LostPreviewVo();
        BeanUtils.copyProperties(accountMapper.selectById(lost.getUid()), vo);
        BeanUtils.copyProperties(lost, vo);
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(lost.getContent()).getJSONArray("ops");
        this.shortContent(ops, previewText, obj -> images.add(obj.toString()));
        vo.setText(previewText.length() > 300 ? previewText.substring(0, 300) : previewText.toString());
        vo.setImages(images);
        return vo;
    }

    private void shortContent(JSONArray ops, StringBuilder previewText, Consumer<Object> imageHandler){
        for (Object op : ops) {
            Object insert = JSONObject.from(op).get("insert");
            if(insert instanceof String text) {
                if(previewText.length() >= 300) continue;
                previewText.append(text);
            } else if(insert instanceof Map<?, ?> map) {
                Optional.ofNullable(map.get("image")).ifPresent(imageHandler);
            }
        }
    }

    private boolean textLimitCheck(JSONObject object, int max) {
        if(object == null) return false;
        long length = 0;
        for (Object op : object.getJSONArray("ops")) {
            length += JSONObject.from(op).getString("insert").length();
            if(length > max) return false;
        }
        return true;
    }
}
