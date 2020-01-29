package com.huangchao.acef.service;

import ch.qos.logback.classic.Logger;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.FeedbackMapper;
import com.huangchao.acef.entity.Feedback;
import com.huangchao.acef.utils.SystemConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.huangchao.acef.utils.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * 本类为意见反馈处理类
 */
@Service
public class FeedbackService {

    //注入映射用户信息相关数据库操作类
    @Autowired
    private FeedbackMapper feedbackMapper;

    @Autowired
    private SystemConfig systemConfig;

    private final static Logger logger = (Logger) LoggerFactory.getLogger(FeedbackService.class);

    //保存用户反馈信息
    public Map<String, String> setFeedback(Feedback feedback) {
        Map<String, String> result = new HashMap<>();
        logger.info("意见反馈信息如下：\n{}", feedback);
        //设置邮件内容
        String content = "<html><head></head><body><h2>感谢您的反馈，我们将认真考虑您的建议，提供更加完善的服务。</h2></body></html>";
        //发送邮件
        new EmailUtil(systemConfig.fromEmail, feedback.getEmailAccount(), systemConfig.authorizationCode).run("法国东部华人协会(ACEF)", content);
        feedbackMapper.setFeedback(feedback);
        result.put("result", "1");
        return result;
    }

    //后台意见反馈信息批量删除
    public Map<String, String> deleteFeedback(int[] idList) {
        logger.info("批量删除意见反馈的id为：{}", Arrays.toString(idList));
        Map<String, String> result = new HashMap<>();
        feedbackMapper.deleteFeedback(idList);
        result.put("result", "1");
        return result;
    }


    //意见反馈信息获取接口
    public PageInfo<Feedback> getFeedback(int currentPage, int pageSize) {
        logger.info("获取意见反馈的当前页为：{},每页大小为：{}", currentPage, pageSize);
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<Feedback> page = PageHelper.startPage(currentPage, pageSize);
        feedbackMapper.getFeedback();
        //用PageInfo对结果进行包装
//        PageInfo<Feedback> pageInfo = (PageInfo<Feedback>) page.toPageInfo();
        return page.toPageInfo();
    }

}
