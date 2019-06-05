package com.huangchao.acef.dao;

import com.huangchao.acef.entity.Feedback;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 本类为意见反馈持久层映射类
 */
@Repository
public interface FeedbackMapper {

    //保存用户反馈信息
    void setFeedback(Feedback feedback);

    //意见反馈信息获取接口
    List<Feedback> getFeedback();

    //后台意见反馈信息批量删除
    void deleteFeedback(int[] idList);

}
