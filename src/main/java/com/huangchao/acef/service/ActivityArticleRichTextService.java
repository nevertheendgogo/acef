package com.huangchao.acef.service;

import ch.qos.logback.classic.Logger;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.ActivityArticleRichTextMapper;
import com.huangchao.acef.dao.PictureMapper;
import com.huangchao.acef.entity.ActivityArticle;
import com.huangchao.acef.utils.CookieUtil;
import com.huangchao.acef.utils.SystemConfig;
import com.huangchao.acef.utils.ZIPUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.huangchao.acef.utils.Common.deletePreviousPicture;

/**
 * 本类为活动文章操作类
 */
@Service
public class ActivityArticleRichTextService {

    //注入富文本活动文章操作类
    @Autowired
    private ActivityArticleRichTextMapper activityArticleRichTextMapper;
    //注入图片操作类
    @Autowired
    private PictureMapper pictureMapper;
    @Autowired
    private SystemConfig systemConfig;

    //TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
    private final static Logger logger = (Logger) LoggerFactory.getLogger(ActivityArticleRichTextService.class);

    //活动文章上传
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> result = new HashMap<>();
        result.put("result", "0");

        logger.info("活动文章内容为：\n{}", aa);
        logger.info("活动时间为：\n{}", activityTime);

        //活动信息处理
        aa = activityArticleDispose(aa, activityTime, entryForm, poster, request, response);
        //将活动信息数据存进数据库
        activityArticleRichTextMapper.addActivityArticle(aa);
        result.put("result", "1");

        return result;
    }

    //根据文章id删除活动文章
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteActivityArticle(String[] articleId) {
        logger.info("要删除的活动文章id数组为：{}", Arrays.toString(articleId));
        Map<String, String> result = new HashMap<>();
        for (int i = 0; i < articleId.length; i++) {
            //删除文章中的图片
            String[] imgPaths = pictureMapper.getRiceTextPictures(articleId[i]);
            if (imgPaths != null) {
                for (String imgUrl : imgPaths) {
                    //删除文章内图片
                    deletePreviousPicture(imgUrl, systemConfig.filePath, systemConfig.richTextPath);
                }
            }
            //删除海报图片
            deletePreviousPicture(activityArticleRichTextMapper.getOneActivityArticle(articleId[i]).getPosterUrl(), systemConfig.filePath, systemConfig.activityArticleImgPath);
            //删除报名表
            deletePreviousPicture(activityArticleRichTextMapper.getOneActivityArticle(articleId[i]).getEntryFormUrl(), systemConfig.filePath, systemConfig.entryFormPath);
            //删除数据库存储的文章内容
            activityArticleRichTextMapper.deleteActivityArticle(articleId[i]);
            //删除数据库中保存的图片链接
            pictureMapper.deleteRichTextPicture(articleId[i]);
        }
        result.put("result", "1");
        return result;
    }

    //根据文章id获取活动文章
    public ActivityArticle getOneActivityArticle(String articleId) {
        logger.info("获取文章所用的文章id为：{}",articleId);
        ActivityArticle activityArticle = activityArticleRichTextMapper.getOneActivityArticle(articleId);
        //使报名表、海报路径完整
        if (activityArticle.getEntryFormUrl() != null) {
            activityArticle.setEntryFormUrl(systemConfig.urlPrefix + activityArticle.getEntryFormUrl());
        }
        if (activityArticle.getPosterUrl() != null) {
            activityArticle.setPosterUrl(systemConfig.urlPrefix + activityArticle.getPosterUrl());
        }
        activityArticle.setDisplayTime(activityArticle.getDisplayTime().substring(0, 10));
        return activityArticle;
    }

    //批量获取活动文章
    public PageInfo<ActivityArticle> getActivityArticle(String language, int currentPage, int pageSize, String part) {

        logger.info("批量获取活动文章");
        logger.info("language：{},part:{}",language,part);
        logger.info("当前页数：{},每页大小：{}",currentPage,pageSize);
        if (language.equals("all"))
            language = null;
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        List<ActivityArticle> activityArticleList = activityArticleRichTextMapper.getActivityArticle(language, part);
        //使路径完整、并截断时间
        for (ActivityArticle s : activityArticleList) {
            s.setPosterUrl(systemConfig.urlPrefix + s.getPosterUrl());
            s.setDisplayTime(s.getDisplayTime().substring(0, 10));
        }
        //用PageInfo对结果进行包装
        PageInfo<ActivityArticle> pageInfo = (PageInfo<ActivityArticle>) page.toPageInfo();
        return pageInfo;
    }

    //判断articleId是否已经存在，即此活动文章是否已经上传
    public boolean existArticleId(String articleId) {
        return activityArticleRichTextMapper.existArticleId(articleId) != null;
    }

    //活动文章修改
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> changeActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        Map<String, String> result = new HashMap<>();
        //查找文章id对应的所有文章信息
        logger.info("开始修改活动文章");
        logger.info("查找文章id对应的所有文章信息");
        ActivityArticle activityArticle = activityArticleRichTextMapper.getOneActivityArticle(aa.getArticleId());
        //如果原来的文章有报名表则将之删除
        if (activityArticle.getEntryFormUrl() != null && !activityArticle.getEntryFormUrl().equals("")) {
            logger.info("删除原来的文章");
            deletePreviousPicture(activityArticle.getEntryFormUrl(), systemConfig.filePath, systemConfig.entryFormPath);
        }
        //如果原来的文章有海报表则将之删除
        if (activityArticle.getPosterUrl() != null && !activityArticle.getPosterUrl().equals("")) {
            logger.info("删除原来的海报");
            deletePreviousPicture(activityArticle.getPosterUrl(), systemConfig.filePath, systemConfig.activityArticleImgPath);
        }
        //活动信息处理
        aa = activityArticleDispose(aa, activityTime, entryForm, poster, request, response);

        //将修改后活动信息数据存进数据库
        logger.info("将修改后活动信息数据存进数据库:\n{}",aa);
        activityArticleRichTextMapper.changeActivityArticle(aa);
        result.put("result", "1");
        return result;
    }

    //辅助方法，活动文章处理
    private ActivityArticle activityArticleDispose(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //设置活动开始和结束时间
        if (activityTime != null) {
            if (!activityTime[0].equals(""))
                aa.setActivityStartTime(activityTime[0]);
            if (activityTime[1] != null && !activityTime[1].equals(""))
                aa.setActivityEndTime(activityTime[1]);
        }

        //报名表处理
        if (entryForm != null && !entryForm.isEmpty()) {
            //获取报名表名
            String fileName = aa.getActivityStartTime() + new Date().getTime() + "~" + entryForm.getOriginalFilename();

            //保存报名表到指定文件夹,可能出现io异常
            entryForm.transferTo(new File(systemConfig.filePath + systemConfig.entryFormPath + fileName));
            File file = new File(systemConfig.filePath + systemConfig.entryFormPath + fileName);
            //压缩文件
            ZIPUtil.compress(systemConfig.filePath + systemConfig.entryFormPath + fileName);
            fileName = systemConfig.entryFormPath + fileName;
            fileName = fileName.substring(0, fileName.lastIndexOf(".")) + ".zip";
            //删除原文件
            file.delete();
            //设置报名表映射路径
            aa.setEntryFormUrl(fileName);
        }


        //海报处理
        if (poster != null && !poster.isEmpty()) {
            //获取文件名
            String fileName = poster.getOriginalFilename();
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //重新生成图片名
            fileName = UUID.randomUUID() + suffixName;
            //设置海报映射路径
            aa.setPosterUrl(systemConfig.activityArticleImgPath + fileName);
            logger.info("海报图片保存全路径为：{}",systemConfig.filePath + systemConfig.activityArticleImgPath + fileName);
            //保存图片到指定文件夹,可能出现io异常
            poster.transferTo(new File(systemConfig.filePath + systemConfig.activityArticleImgPath + fileName));

        }

        //获取cookie
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieUtil.findCookie(cookies, "articleId");
        //若id为articleId的cookie为不为空或者和当前文章id相同，则认为文章上传未中断,清除保存再cookie的文章id
        if (cookie != null && cookie.getValue().equals(aa.getArticleId())){
            CookieUtil.saveCookie("articleId", aa.getArticleId(), response, 0);
        }

        return aa;
    }


}
