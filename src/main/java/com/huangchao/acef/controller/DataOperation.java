package com.huangchao.acef.controller;

import ch.qos.logback.classic.Logger;
import com.huangchao.acef.entity.*;
import com.huangchao.acef.service.DataService;
import com.huangchao.acef.utils.CookieUtil;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.huangchao.acef.global.Common.deletePreviousPicture;
import static com.huangchao.acef.global.Common.getLanguage;

/**
 * 本类为跳转游客可访问的页面
 */
@Controller
@RequestMapping("/data")
public class DataOperation {

    //获取业务层操作类
    @Autowired
    private DataService dataService;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(DataOperation.class);
    //注入默认界面语言
    @Value("${defaultLanguage}")
    String defaultLanguage;
    //统一cookie存活基准时间
    @Value("${survivalTime}")
    private int survivalTime;
    //注入图片保存路径
    @Value("${filePath}")
    String filePath;
    //注入图片保存路径，相对根路径
    @Value("${imgPath}")
    String imgPath;
    //注入tomcat文件映射路径名
    @Value("${mapPath}")
    String mapPath;
    //注入本机公网ip地址
    @Value("${IpAddress}")
    String IpAddress;
    //注入活动文章图片/海报保存路径
    @Value("${activityArticleImgPath}")
    String activityArticleImgPath;
    //注入报名表保存路径
    @Value("${entryFormPath}")
    String entryFormPath;


    /***************************************************************  轮播图/协会介绍/富文本图片上传 *************************************************************************************************************/
    //轮播图/协会介绍上传
    @RequestMapping("/up")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadPicture(MultipartFile[] slideshows, String part, Integer id, String url) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();
        try {
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
                System.out.println(imgPaths.size());
                for (String p : imgPaths) {
                    System.out.println(p);
                }
                dataService.updateAssociationIntroduction(imgPaths.get(0), part, id);
            } else
                //保存轮播图或协会介绍数据到数据库
                dataService.uploadPicture(imgPaths, part);

            result.put("result", "1");
            return result;
        } catch (IOException e) {
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            return result;
        }
    }

    //轮播图/协会介绍链接获取
    @RequestMapping("/gp")
    @ResponseBody
    public List<Slideshow> getPicture(String part) {
        logger.info("logbackinfo 成功了");
        try {
            return dataService.getPicture(part);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    //轮播图删除
    @RequestMapping("/dss")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteSlideshow(int id, String url) {
        Map<String, String> result = new HashMap<>();

        try {
            //执行数据库删除操作
            dataService.deleteSlideshow(id);
            //删除图片
            deletePreviousPicture(url, filePath, imgPath);
            result.put("result", "1");
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", "0");
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //富文本(rice text)图片上传
    @RequestMapping("/urtp")
    @ResponseBody                                                           //文章id
    public Map<String, String> uploadRiceTextPicture(MultipartFile picture, String articleId, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();

        String url;
        if (!picture.isEmpty() && articleId != null && !articleId.equals("")) {
            //获取文件名
            String fileName = picture.getOriginalFilename();
            //获取文件后缀名
            String suffixName = fileName.substring(fileName.lastIndexOf("."));
            //重新生成图片名
            fileName = UUID.randomUUID() + suffixName;
            //设置图片映射路径
            url = mapPath + imgPath + activityArticleImgPath + fileName;
            //保存图片到指定文件夹,可能出现io异常
            try {
                //获取cookie
                Cookie[] cookies = request.getCookies();
                Cookie cookie = CookieUtil.findCookie(cookies, "articleId");

                //若id为articleId的cookie不为空且和当前文章id不同，则认为上次文章上传中断
                if (cookie != null && !cookie.getValue().equals(articleId)) {
                    String[] riceTextPictures = dataService.getRiceTextPictures(cookie.getValue() + "");
                    for (int i = 0; i < riceTextPictures.length; i++) {
                        //删除图片
                        deletePreviousPicture(riceTextPictures[i], filePath, imgPath + activityArticleImgPath);
                    }
                    //删除数据库对应数据
                    dataService.deleteRichTextPicture(cookie.getValue());
                }
                CookieUtil.saveCookie("articleId", articleId, response, survivalTime*3);

                //执行图片持久化操作
                dataService.addRichTextPicture(articleId, url);
                picture.transferTo(new File(filePath + imgPath + activityArticleImgPath + fileName));
                result.put("articleId", articleId);
                result.put("url", "http://" + IpAddress + url);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return result;
    }


    /***************************************************************  富文本文章 *************************************************************************************************************/

    //活动文章
    @RequestMapping("/uaa")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadActivityArticle(ActivityArticle aa, String[] activityTime, MultipartFile entryForm, MultipartFile[] posters, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        result.put("result", "0");

        //判断articleId是否已经存在，即此文章是否已经上传
        if (!dataService.existArticleId(aa.getArticleId())) {
            //设置活动开始和结束时间
            if (activityTime != null) {
                if (!activityTime[0].equals(""))
                    aa.setActivityStartTime(activityTime[0]);
                if (activityTime[1] != null && !activityTime[1].equals(""))
                    aa.setActivityEndTime(activityTime[1]);
            }

            try {
                //报名表处理
                if (!entryForm.isEmpty()) {
                    //获取报名表名
                    String fileName = aa.getActivityStartTime() + "~" + entryForm.getOriginalFilename();
                    //设置报名表映射路径
                    aa.setEntryFormUrl(mapPath + entryFormPath + fileName);
                    //将活动信息数据存进数据库
                    dataService.uploadActivityArticle(aa);
                    //保存图片到指定文件夹,可能出现io异常
                    entryForm.transferTo(new File(filePath + entryFormPath + fileName));
                }

                //海报处理
                for (MultipartFile poster : posters) {
                    if (!poster.isEmpty()) {
                        //获取文件名
                        String fileName = poster.getOriginalFilename();
                        //获取文件后缀名
                        String suffixName = fileName.substring(fileName.lastIndexOf("."));
                        //重新生成图片名
                        fileName = UUID.randomUUID() + suffixName;
                        //将图片保存进数据库
                        dataService.addRichTextPoster(aa.getArticleId(), mapPath + imgPath + activityArticleImgPath + fileName);
                        //保存图片到指定文件夹,可能出现io异常
                        poster.transferTo(new File(filePath + imgPath + activityArticleImgPath + fileName));

                        //获取cookie
                        Cookie[] cookies = request.getCookies();
                        Cookie cookie = CookieUtil.findCookie(cookies, "articleId");
                        //若id为articleId的cookie为不为空或者和当前文章id相同，则认为文章上传未中断,清除保存再cookie的文章id
                        if (cookie != null && cookie.getValue().equals(aa.getArticleId()))
                            CookieUtil.saveCookie("articleId", aa.getArticleId(), response, 0);
                    }
                }

                result.put("result", "1");
            } catch (IOException e) {
                e.printStackTrace();
                //回滚
                TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
            }
        }
        return result;
    }

    //假文章数据
    @RequestMapping("/gaa/{currentPage}/{pageSize}")
    @ResponseBody                               //当前页号                                                一页的数据量
    public List<ActivityArticle> getActivityArticle(@PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize, HttpServletRequest request) {

        String language = getLanguage(request);
        return dataService.getActivityArticle(language != null ? language : defaultLanguage, currentPage, pageSize);
//        List<ActivityArticle> activityArticleList=dataService.getActivityArticle();
//        List<FormData> formDataList = new ArrayList<>();
//        FormData formData = new FormData();
//        formData.setId("1");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//        formData.setId("2");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("3");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("4");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("5");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("6");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("7");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("8");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("9");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("10");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("11");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("12");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("13");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("14");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("15");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("16");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("17");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//
//        formData.setId("18");
//        formData.setTitle("结构化查询语言");
//        formData.setAuthor("百度词条");
//        formData.setDescription("结构化查询语言(Structured Query Language)简称SQL(发音：/ˈes kjuː ˈel/ \"S-Q-L\")，是一种特殊目的的编程语言，是一种数据库查询和程序设计语言，用于存取数据以及查询、更新和管理关系数据库系统；同时也是数据库脚本文件的扩展名。\n" +
//                "结构化查询语言是高级的非过程化编程语言，允许用户在高层数据结构上工作。它不要求用户指定对数据的存放方法，也不需要用户了解具体的数据存放方式，所以具有完全不同底层结构的不同数据库系统, 可以使用相同的结构化查询语言作为数据输入与管理的接口。结构化查询语言语句可以嵌套，这使它具有极大的灵活性和强大的功能。\n" +
//                "1986年10月，美国国家标准协会对SQL进行规范后，以此作为关系式数据库管理系统的标准语言（ANSI X3. 135-1986），1987年得到国际标准组织的支持下成为国际标准。不过各种通行的数据库系统在其实践过程中都对SQL规范作了某些编改和扩充。所以，实际上不同数据库系统之间的SQL不能完全相互通用。");
//        formData.setImePath("https://baike.baidu.com/pic/%E7%BB%93%E6%9E%84%E5%8C%96%E6%9F%A5%E8%AF%A2%E8%AF%AD%E8%A8%80/10450182/0/0b46f21fbe096b6340eee5740e338744ebf8ac1b?fr=lemma&ct=single");
//        formDataList.add(formData);
//
//        return formDataList;

    }



    //后台数据输入保存
    @RequestMapping("/set")
    @ResponseBody
    public Map<String, Integer> setData(FormData data, @RequestParam("fileUpload") MultipartFile fileUpload, HttpServletRequest request) {

        //用于返回保存结果,成功则返回1和昵称
        Map<String, Integer> result = new HashMap<>();
        //获取图片存储目录
        String uploadImageUrl = request.getContextPath() + "/img/";
        //获取文件名
        String fileName = fileUpload.getOriginalFilename();
        //获取文件后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //重新生成文件名
        fileName = UUID.randomUUID() + suffixName;
        uploadImageUrl += fileName;
        //设置图片URL
        data.setImePath(uploadImageUrl);
        //指定本地文件夹存储图片
        try {
            //将图片保存到img文件夹里
            fileUpload.transferTo(new File(filePath + fileName));
            //保存数据
            dataService.addData(data);
            result.put("result", 1);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
            result.put("result", 0);
            return result;
        }
    }

}

