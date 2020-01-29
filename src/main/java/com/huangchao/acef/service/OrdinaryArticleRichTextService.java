package com.huangchao.acef.service;

import ch.qos.logback.classic.Logger;
import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.OrdinaryArticleRichTextMapper;
import com.huangchao.acef.dao.PictureMapper;
import com.huangchao.acef.entity.OrdinaryArticle;
import com.huangchao.acef.utils.CookieUtil;
import com.huangchao.acef.utils.SystemConfig;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.huangchao.acef.utils.Common.deletePreviousPicture;
import static com.huangchao.acef.utils.Common.getLanguage;

/**
 * 本类为普通文章操作类
 */
@Service
public class OrdinaryArticleRichTextService {

    //注入富文本活动文章操作类
    @Autowired
    private OrdinaryArticleRichTextMapper ordinaryArticleRichTextMapper;
    //注入图片操作类
    @Autowired
    private PictureMapper pictureMapper;
    @Autowired
    private SystemConfig systemConfig;
    private final static Logger logger = (Logger) LoggerFactory.getLogger(OrdinaryArticleRichTextService.class);

    //普通文章上传
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> uploadOrdinaryArticle(OrdinaryArticle oa, HttpServletRequest request, HttpServletResponse response) {
        Map<String, String> result = new HashMap<>();
        logger.info("上传文章的详细信息如下：\n{}", oa);
        //判断articleId是否已经存在，即此文章是否已经上传
        if (!this.existOrdinaryArticleId(oa.getArticleId())) {
            //获取cookie
            Cookie[] cookies = request.getCookies();
            Cookie cookie = CookieUtil.findCookie(cookies, "articleId");
            //若id为articleId的cookie为不为空或者和当前文章id相同，则认为文章上传未中断,清除保存在cookie的文章id
            if (cookie != null && cookie.getValue().equals(oa.getArticleId())) {
                logger.info("文章上传未中断,当前文章id为：{}", oa.getArticleId());
                CookieUtil.saveCookie("articleId", oa.getArticleId(), response, 0);
            }
            ordinaryArticleRichTextMapper.uploadOrdinaryArticle(oa);
            result.put("result", "1");
        } else {
            result.put("result", "0");
        }
        return result;
    }

    //根据普通文章id删除普通文章
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteOrdinaryArticle(String[] articleId) {
        logger.info("要删除的文章id集合为：{}", Arrays.toString(articleId));
        Map<String, String> result = new HashMap<>();

        if (articleId != null) {
            for (int i = 0; i < articleId.length; i++) {
                //删除文章中的图片
                String[] imgPaths = pictureMapper.getRiceTextPictures(articleId[i]);
                if (imgPaths != null) {
                    for (String imgUrl : imgPaths) {
                        //删除图片
                        deletePreviousPicture(imgUrl, systemConfig.filePath, systemConfig.richTextPath);
                        //删除数据库中保存的图片链接
                        pictureMapper.deleteRichTextPicture(articleId[i]);
                    }
                }
            }
            //删除文章
            ordinaryArticleRichTextMapper.deleteOrdinaryArticle(articleId);

        }
        result.put("result", "1");
        return result;
    }

    //根据普通文章id获取普通文章
    public OrdinaryArticle getOneOrdinaryArticle(String articleId) {
        logger.info("要获取的文章id为：{}", articleId);
        if (articleId != null) {
            OrdinaryArticle ordinaryArticle = ordinaryArticleRichTextMapper.getOneOrdinaryArticle(articleId);
            //截断时间
            ordinaryArticle.setDisplayTime(ordinaryArticle.getDisplayTime().substring(0, 10));
            return ordinaryArticle;
        } else
            return null;
    }

    //批量获取普通文章
    public PageInfo<OrdinaryArticle> getOrdinaryArticle(String language, int currentPage, int pageSize, String part, HttpServletRequest request) {
        //若未传来language，则为后台管理查询
        if (language == null || language.equals("")) {
            //获取用户设置的语言
            language = getLanguage(request);
        }
        language = language != null ? language : systemConfig.defaultLanguage;
        logger.info("批量获取普通文章的语言为：{}、主题为:{}", language, part);
        logger.info("当前页号为：{}，每页大小为：{}", currentPage, pageSize);
        if (language.equals("all"))
            language = null;

        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<OrdinaryArticle> page = PageHelper.startPage(currentPage, pageSize);
        List<OrdinaryArticle> ordinaryArticleList = ordinaryArticleRichTextMapper.getOrdinaryArticle(language, part);
        //截断时间
        for (OrdinaryArticle o : ordinaryArticleList) {
            o.setDisplayTime(o.getDisplayTime().substring(0, 10));
        }
        //用PageInfo对结果进行包装
//        PageInfo<OrdinaryArticle> pageInfo = (PageInfo<OrdinaryArticle>) page.toPageInfo();
        return page.toPageInfo();
    }

    //    判断articleId是否已经存在，即此普通文章是否已经上传
    public boolean existOrdinaryArticleId(String articleId) {
        return ordinaryArticleRichTextMapper.existOrdinaryArticleId(articleId) != null;
    }

    //修改普通文章
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> changeOrdinaryArticle(OrdinaryArticle oa) {
        Map<String, String> result = new HashMap<>();
        ordinaryArticleRichTextMapper.changeOrdinaryArticle(oa);
        result.put("result", "1");
        return result;
    }

}
