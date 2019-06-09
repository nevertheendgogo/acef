package com.huangchao.acef.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.ActivityArticleRichTextMapper;
import com.huangchao.acef.dao.PictureMapper;
import com.huangchao.acef.entity.ActivityArticle;
import com.huangchao.acef.utils.CookieUtil;
import com.huangchao.acef.utils.ZIPUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.huangchao.acef.global.Common.deletePreviousPicture;

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
    //注入本机公网ip地址
    @Value("${IpAddress}")
    String IpAddress;
    //注入图片保存路径
    @Value("${filePath}")
    String filePath;
    //注入图片保存路径，相对根路径
    @Value("${imgPath}")
    String imgPath;
    //注入tomcat文件映射路径名
    @Value("${mapPath}")
    String mapPath;
    //注入活动文章图片/海报保存路径
    @Value("${activityArticleImgPath}")
    String activityArticleImgPath;
    //注入报名表保存路径
    @Value("${entryFormPath}")
    String entryFormPath;


    //活动文章上传
    public void uploadActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //活动信息处理
        aa = activityArticleDispose(aa, activityTime, entryForm, poster, request, response);
        //将活动信息数据存进数据库
        activityArticleRichTextMapper.addActivityArticle(aa);

    }

    //根据文章id删除活动文章
    public void deleteActivityArticle(String[] articleId) throws IOException {
        for (int i = 0; i < articleId.length; i++) {
            //删除文章中的图片
            String[] imgPaths = pictureMapper.getRiceTextPictures(articleId[i]);
            if (imgPaths != null) {
                for (String imgUrl : imgPaths) {
                    //删除图片
                    deletePreviousPicture(imgUrl, filePath, imgPath + activityArticleImgPath);
                }
            }
            //删除海报图片
            deletePreviousPicture(activityArticleRichTextMapper.getOneActivityArticle(articleId[i]).getPosterUrl(), filePath, imgPath + activityArticleImgPath);
            //删除报名表
            deletePreviousPicture(activityArticleRichTextMapper.getOneActivityArticle(articleId[i]).getEntryFormUrl(), filePath, entryFormPath);
            //删除数据库存储的文章内容
            activityArticleRichTextMapper.deleteActivityArticle(articleId[i]);
            //删除数据库中保存的图片链接
            pictureMapper.deleteRichTextPicture(articleId[i]);
        }
    }

    //根据文章id获取活动文章
    public ActivityArticle getOneActivityArticle(String articleId) {
        ActivityArticle activityArticle = activityArticleRichTextMapper.getOneActivityArticle(articleId);
        //使报名表、海报路径完整
        activityArticle.setEntryFormUrl("http://"+IpAddress+activityArticle.getEntryFormUrl());
        activityArticle.setPosterUrl("http://"+IpAddress+activityArticle.getPosterUrl());
        activityArticle.setDisplayTime(activityArticle.getDisplayTime().substring(0, 10));
        return activityArticle;
    }

    //批量获取活动文章
    public PageInfo<ActivityArticle> getActivityArticle(String language, int currentPage, int pageSize, String part) {

        if (language.equals("all"))
            language = null;
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        List<ActivityArticle> activityArticleList = activityArticleRichTextMapper.getActivityArticle(language, part);
        //使路径完整、并截断时间
        for (ActivityArticle s : activityArticleList) {
            s.setPosterUrl("http://" + IpAddress + s.getPosterUrl());
            s.setDisplayTime(s.getDisplayTime().substring(0,10));
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
    public void changeActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //查找文章id对应的所有文章信息
        ActivityArticle activityArticle = activityArticleRichTextMapper.getOneActivityArticle(aa.getArticleId());
        //如果原来的文章有报名表则将之删除
        if (activityArticle.getEntryFormUrl() != null && !activityArticle.getEntryFormUrl().equals("")) {
            deletePreviousPicture(activityArticle.getEntryFormUrl(), filePath, entryFormPath);
        }
        //如果原来的文章有海报表则将之删除
        if (activityArticle.getPosterUrl() != null && !activityArticle.getPosterUrl().equals("")) {
            deletePreviousPicture(activityArticle.getPosterUrl(), filePath, imgPath + activityArticleImgPath);
        }
        //活动信息处理
        aa = activityArticleDispose(aa, activityTime, entryForm, poster, request, response);

        //将修改后活动信息数据存进数据库
        activityArticleRichTextMapper.changeActivityArticle(aa);

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

            //保存图片到指定文件夹,可能出现io异常
            entryForm.transferTo(new File(filePath + entryFormPath + fileName));
            //删除原文件
            File file=new File(filePath + entryFormPath + fileName);
            //压缩文件
            fileName=ZIPUtil.compress(mapPath + entryFormPath + fileName);
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
            aa.setPosterUrl(mapPath + imgPath + activityArticleImgPath + fileName);
            //保存图片到指定文件夹,可能出现io异常
            poster.transferTo(new File(filePath + imgPath + activityArticleImgPath + fileName));

        }

        //获取cookie
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieUtil.findCookie(cookies, "articleId");
        //若id为articleId的cookie为不为空或者和当前文章id相同，则认为文章上传未中断,清除保存再cookie的文章id
        if (cookie != null && cookie.getValue().equals(aa.getArticleId()))
            CookieUtil.saveCookie("articleId", aa.getArticleId(), response, 0);

        return aa;
    }


}
