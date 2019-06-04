package com.huangchao.acef.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.DataMapper;
import com.huangchao.acef.entity.ActivityArticle;
import com.huangchao.acef.entity.Slideshow;
import com.huangchao.acef.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import static com.huangchao.acef.global.Common.deletePreviousPicture;

/**
 * 本类为用户数据相关操作
 */
@Service
public class DataService {

    //注入映射展示界面相关数据库操作类
    @Autowired
    private DataMapper mapper;
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
    //统一cookie存活基准时间
    @Value("${survivalTime}")
    private int survivalTime;
    //注入报名表保存路径
    @Value("${entryFormPath}")
    String entryFormPath;


    //保存轮播图链接或协会介绍图片链接数据到数据库
    public void uploadPicture(List<String> imgPaths, String part) {
        mapper.uploadPicture(imgPaths, part);
    }

    //修改协会介绍
    public void updateAssociationIntroduction(String url, String part, int id) {
        mapper.updateAssociationIntroduction(url, part, id);
    }

    //轮播图或协会介绍图片链接获取
    public List<Slideshow> getPicture(String part) {
        List<Slideshow> slideshowList = mapper.getPicture(part);
        for (Slideshow s : slideshowList) {
            s.setUrl("http://" + IpAddress + s.getUrl());
        }
        return slideshowList;
    }

    //    轮播图删除
    public void deleteSlideshow(int id, String url) throws IOException {
        //删除数据库数据
        mapper.deleteSlideshow(id);
        //删除图片
        deletePreviousPicture(url, filePath, imgPath);
    }

    //保存富文本上传的图片链接
    public void addRichTextPicture(String articleId, String url) {
        mapper.addRichTextPicture(articleId, url);
    }

    //活动文章保存
    public void addActivityArticle(ActivityArticle aa) {
        mapper.addActivityArticle(aa);
    }

    //删除中断富文本文章上传的图片
    public void deleteRichTextPicture(String articleId) {
        mapper.deleteRichTextPicture(articleId);
    }

    //查找富文本文章上传的图片
    public String[] getRiceTextPictures(String articleId) {
        return mapper.getRiceTextPictures(articleId);
    }

    //    判断articleId是否已经存在，即此文章是否已经上传
    public boolean existArticleId(String articleId) {
        return mapper.existArticleId(articleId) != null;
    }

    //获取活动文章
    public PageInfo<ActivityArticle> getActivityArticle(String language, int currentPage, int pageSize) {

        if (language.equals("all"))
            language = null;
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        List<ActivityArticle> activityArticleList = mapper.getActivityArticle(language);
        //使路径完整
        if (language != null)
            for (ActivityArticle s : activityArticleList) {
                s.setEntryFormUrl("http://" + IpAddress + s.getEntryFormUrl());
                s.setPosterUrl("http://" + IpAddress + s.getPosterUrl());
            }
        //用PageInfo对结果进行包装
        PageInfo<ActivityArticle> pageInfo = (PageInfo<ActivityArticle>) page.toPageInfo();
        return pageInfo;
    }

    //根据文章id删除活动文章
    public void deleteActivityArticle(String[] articleId) throws IOException {
        for (int i = 0; i < articleId.length; i++) {
            //删除文章中的图片
            String[] imgPaths = mapper.getRiceTextPictures(articleId[i]);
            if (imgPaths != null) {
                for (String imgUrl : imgPaths) {
                    //删除图片
                    deletePreviousPicture(imgUrl, filePath, imgPath + activityArticleImgPath);
                }
            }
            //删除海报图片
            deletePreviousPicture(mapper.getOneActivityArticle(articleId[i]).getPosterUrl(), filePath, imgPath + activityArticleImgPath);
            //删除报名表
            deletePreviousPicture(mapper.getOneActivityArticle(articleId[i]).getEntryFormUrl(), filePath, entryFormPath);
            //删除数据库存储的文章内容
            mapper.deleteActivityArticle(articleId[i]);
            //删除数据库中保存的图片链接
            mapper.deleteRichTextPicture(articleId[i]);
        }
    }

    //轮播图或协会介绍图上传
    public void uploadSlideshowOrAssociationIntroduction(MultipartFile[] slideshows, String part, Integer id, String url) throws IOException {
        //保存图片映射路径
        List<String> imgPaths = new ArrayList<>();
        for (MultipartFile slideshow : slideshows) {
            if (slideshow != null && !slideshow.isEmpty()) {
                //获取文件名
                String fileName = slideshow.getOriginalFilename();
                //获取文件后缀名
                String suffixName = fileName.substring(fileName.lastIndexOf("."));
                //重新生成图片名
                fileName = UUID.randomUUID() + suffixName;
                //设置图片映射路径
                imgPaths.add(mapPath + imgPath + fileName);
                //保存图片到指定文件夹,可能出现io异常
                slideshow.transferTo(new File(filePath + imgPath + fileName));
            }
        }

        //若url不空则为更新协会介绍照片
        if (url != null && !url.equals("")) {
            deletePreviousPicture(url, filePath, imgPath);
            //保存协会介绍数据到数据库

            updateAssociationIntroduction(imgPaths.get(0), part, id);
        } else
            //保存轮播图或协会介绍数据到数据库
            uploadPicture(imgPaths, part);
    }

    //富文本(rice text)图片上传
    public String uploadRiceTextPicture(MultipartFile picture, String articleId, HttpServletRequest request, HttpServletResponse response) throws IOException {

        String url;
        //获取文件名
        String fileName = picture.getOriginalFilename();
        //获取文件后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //重新生成图片名
        fileName = UUID.randomUUID() + suffixName;
        //设置图片映射路径
        url = mapPath + imgPath + activityArticleImgPath + fileName;
        //获取cookie
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieUtil.findCookie(cookies, "articleId");

        //若id为articleId的cookie不为空且和当前文章id不同，则认为上次文章上传中断
        if (cookie != null && !cookie.getValue().equals(articleId)) {
            //删除数据库对应数据
            deleteRichTextPicture(cookie.getValue());

            String[] riceTextPictures = getRiceTextPictures(cookie.getValue() + "");
            for (int i = 0; i < riceTextPictures.length; i++) {
                //删除图片
                deletePreviousPicture(riceTextPictures[i], filePath, imgPath + activityArticleImgPath);
            }
        }
        CookieUtil.saveCookie("articleId", articleId, response, survivalTime * 3);

        //执行图片持久化操作
        addRichTextPicture(articleId, url);
        //保存图片到指定文件夹,可能出现io异常
        picture.transferTo(new File(filePath + imgPath + activityArticleImgPath + fileName));
        return "http://" + IpAddress + url;
    }

    //活动文章上传
    public void uploadActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //活动信息处理
        aa=activityArticleDispose(aa, activityTime, entryForm, poster, request, response);
        //将活动信息数据存进数据库
        addActivityArticle(aa);

    }

    //活动文章修改
    public void changeActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile poster, HttpServletRequest request, HttpServletResponse response) throws IOException {
        //查找文章id对应的所有文章信息
        ActivityArticle activityArticle = mapper.getOneActivityArticle(aa.getArticleId());
        //如果原来的文章有报名表则将之删除
        if (activityArticle.getEntryFormUrl() != null && !activityArticle.getEntryFormUrl().equals("")) {
            deletePreviousPicture(activityArticle.getEntryFormUrl(), filePath, entryFormPath);
        }
        //如果原来的文章有海报表则将之删除
        if (activityArticle.getPosterUrl() != null && !activityArticle.getPosterUrl().equals("")) {
            deletePreviousPicture(activityArticle.getPosterUrl(), filePath, imgPath + activityArticleImgPath);
        }
        //活动信息处理
        aa=activityArticleDispose(aa, activityTime, entryForm, poster, request, response);

        //将修改后活动信息数据存进数据库
        mapper.changeActivityArticle(aa);

    }

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
            //设置报名表映射路径
            aa.setEntryFormUrl(mapPath + entryFormPath + fileName);
            //保存图片到指定文件夹,可能出现io异常
            entryForm.transferTo(new File(filePath + entryFormPath + fileName));
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

            //获取cookie
            Cookie[] cookies = request.getCookies();
            Cookie cookie = CookieUtil.findCookie(cookies, "articleId");
            //若id为articleId的cookie为不为空或者和当前文章id相同，则认为文章上传未中断,清除保存再cookie的文章id
            if (cookie != null && cookie.getValue().equals(aa.getArticleId()))
                CookieUtil.saveCookie("articleId", aa.getArticleId(), response, 0);
        }
        return aa;
    }

    //根据文章id获取活动文章
    public ActivityArticle getOneActivityArticle(String articleId) {
        return mapper.getOneActivityArticle(articleId);
    }


}
