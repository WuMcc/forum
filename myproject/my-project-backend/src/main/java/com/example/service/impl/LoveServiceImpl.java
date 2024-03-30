package com.example.service.impl;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.example.entity.dto.*;
import com.example.entity.vo.request.*;
import com.example.entity.vo.response.*;
import com.example.mapper.*;
import com.example.service.LoveService;
import com.example.service.NotificationService;
import com.example.utils.CacheUtils;
import com.example.utils.Const;
import com.example.utils.FlowUtils;
import jakarta.annotation.Resource;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.Consumer;

@Service
public class LoveServiceImpl extends ServiceImpl<LoveMapper, Love> implements LoveService {
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
    LoveCommentMapper commentMapper;

    @Resource
    StringRedisTemplate template;

    @Resource
    NotificationService notificationService;


    @Override
    public String createLove(int uid, LoveCreateVo vo) {
        if(!textLimitCheck(vo.getContent(), 20000))
            return "帖子内容太多，发文失败！";
        String key = Const.FORUM_LOVE_CREATE_COUNTER + uid;
        if(!flowUtils.limitPeriodCounterCheck(key, 3, 3600))
            return "发贴频繁，请稍后再试！";
        Love love = new Love();
        BeanUtils.copyProperties(vo, love);
        love.setContent(vo.getContent().toJSONString());
        love.setUid(uid);
        love.setTime(new Date());
        if(this.save(love)) {
            cacheUtils.deleteCachePattern(Const.FORUM_LOVE_PREVIEW_CACHE + "*");
            return null;
        } else {
            return "内部错误，请联系管理员！";
        }
    }

    @Override
    public String updateLove(int uid, LoveUpdateVo vo) {
        if(!textLimitCheck(vo.getContent(), 20000))
            return "文章内容太多，发文失败！";
        baseMapper.update(null, Wrappers.<Love>update()
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
        LoveComment comment = new LoveComment();
        comment.setUid(uid);
        BeanUtils.copyProperties(vo, comment);
        comment.setTime(new Date());
        commentMapper.insert(comment);
        Love love = baseMapper.selectById(vo.getTid());
        Account account = accountMapper.selectById(uid);
        if(vo.getQuote() > 0) {
            LoveComment com = commentMapper.selectById(vo.getQuote());
            if(!Objects.equals(account.getId(), com.getUid())) {
                notificationService.addNotification(
                        com.getUid(),
                        "您有新的帖子评论回复",
                        account.getUsername()+" 回复了你发表的评论，快去看看吧！",
                        "success", "/index/love-detail/"+com.getTid()
                );
            }
        } else if (!Objects.equals(account.getId(), love.getUid())) {
            notificationService.addNotification(
                    love.getUid(),
                    "您有新的帖子回复",
                    account.getUsername()+" 回复了你发表主题: "+love.getTitle()+"，快去看看吧！",
                    "success", "/index/love-detail/"+love.getId()
            );
        }
        return null;
    }

    @Override
    public List<CommentVo> comments(int tid, int pageNumber) {
        Page<LoveComment> page = Page.of(pageNumber, 10);
        commentMapper.selectPage(page, Wrappers.<LoveComment>query().eq("tid", tid));
        return page.getRecords().stream().map(dto -> {
            CommentVo vo = new CommentVo();
            BeanUtils.copyProperties(dto, vo);
            if(dto.getQuote() > 0) {
                LoveComment comment = commentMapper.selectOne(Wrappers.<LoveComment>query()
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
        commentMapper.delete(Wrappers.<LoveComment>query().eq("id", id).eq("uid", uid));
    }

    @Override
    public List<LovePreviewVo> listLoveByPage(int pageNumber) {
        String key = Const.FORUM_LOVE_PREVIEW_CACHE + pageNumber;
        List<LovePreviewVo> list = cacheUtils.takeListFromCache(key, LovePreviewVo.class);
        if(list != null)
            return list;
        Page<Love> page = Page.of(pageNumber, 10);
        baseMapper.selectPage(page, Wrappers.<Love>query().orderByDesc("time"));
        List<Love> loves = page.getRecords();
        if(loves.isEmpty()) return null;
        list = loves.stream().map(this::resolveToPreview).toList();
        cacheUtils.saveListToCache(key, list, 60);
        return list;
    }


    @Override
    public LoveDetailVo getLove(int tid, int uid) {
        LoveDetailVo vo = new LoveDetailVo();
        Love love = baseMapper.selectById(tid);
        BeanUtils.copyProperties(love, vo);
        LoveDetailVo.User user = new LoveDetailVo.User();
        vo.setUser(this.fillUserDetailsByPrivacy(user, love.getUid()));
        vo.setComments(commentMapper.selectCount(Wrappers.<LoveComment>query().eq("tid", tid)));
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

    private LovePreviewVo resolveToPreview(Love love) {
        LovePreviewVo vo = new LovePreviewVo();
        BeanUtils.copyProperties(accountMapper.selectById(love.getUid()), vo);
        BeanUtils.copyProperties(love, vo);
        List<String> images = new ArrayList<>();
        StringBuilder previewText = new StringBuilder();
        JSONArray ops = JSONObject.parseObject(love.getContent()).getJSONArray("ops");
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
