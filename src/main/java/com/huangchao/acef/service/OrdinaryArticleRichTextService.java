package com.huangchao.acef.service;

import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import com.huangchao.acef.dao.OrdinaryArticleRichTextMapper;
import com.huangchao.acef.dao.PictureMapper;
import com.huangchao.acef.entity.OrdinaryArticle;
import com.huangchao.acef.utils.CookieUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static com.huangchao.acef.global.Common.deletePreviousPicture;

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
    //注入图片保存路径
    @Value("${filePath}")
    String filePath;
    //注入图片保存路径，相对根路径
    @Value("${imgPath}")
    String imgPath;
    //注入文章图片/海报保存路径
    @Value("${activityArticleImgPath}")
    String activityArticleImgPath;



    //普通文章上传
    public void uploadOrdinaryArticle(OrdinaryArticle oa, HttpServletRequest request, HttpServletResponse response) {
        ordinaryArticleRichTextMapper.uploadOrdinaryArticle(oa);
        //获取cookie
        Cookie[] cookies = request.getCookies();
        Cookie cookie = CookieUtil.findCookie(cookies, "articleId");
        //若id为articleId的cookie为不为空或者和当前文章id相同，则认为文章上传未中断,清除保存再cookie的文章id
        if (cookie != null && cookie.getValue().equals(oa.getArticleId()))
            CookieUtil.saveCookie("articleId", oa.getArticleId(), response, 0);
    }

    //根据普通文章id删除普通文章
    public void deleteOrdinaryArticle(String[] articleId) throws IOException {
        if (articleId != null){
            for (int i = 0; i <articleId.length; i++) {
                //删除文章中的图片
                String[] imgPaths = pictureMapper.getRiceTextPictures(articleId[i]);
                if (imgPaths != null) {
                    for (String imgUrl : imgPaths) {
                        //删除图片
                        deletePreviousPicture(imgUrl, filePath, imgPath + activityArticleImgPath);
                        //删除数据库中保存的图片链接
                        pictureMapper.deleteRichTextPicture(articleId[i]);
                    }
                }
            }
            //删除文章
            ordinaryArticleRichTextMapper.deleteOrdinaryArticle(articleId);

        }
    }

    //根据普通文章id获取普通文章
    public OrdinaryArticle getOneOrdinaryArticle(String articleId) {
        if (articleId!=null)
            return ordinaryArticleRichTextMapper.getOneOrdinaryArticle(articleId);
        else
            return null;
    }

    //批量获取普通文章
    public PageInfo<OrdinaryArticle> getOrdinaryArticle(String language, int currentPage, int pageSize,String part) {

        if (language.equals("all"))
            language = null;
        //对数据库的操作必须在此定义的下一条语句执行，且只有一条语句有分页效果，若要多条语句都有分页效果，需写多条本语句
        Page<?> page = PageHelper.startPage(currentPage, pageSize);
        ordinaryArticleRichTextMapper.getOrdinaryArticle(language,part);
        //用PageInfo对结果进行包装
        PageInfo<OrdinaryArticle> pageInfo = (PageInfo<OrdinaryArticle>) page.toPageInfo();
        return pageInfo;
    }

    //    判断articleId是否已经存在，即此普通文章是否已经上传
    public boolean existOrdinaryArticleId(String articleId) {
        return ordinaryArticleRichTextMapper.existOrdinaryArticleId(articleId) != null;
    }

    //修改普通文章
    public void changeOrdinaryArticle(OrdinaryArticle oa) {
        ordinaryArticleRichTextMapper.changeOrdinaryArticle(oa);
    }

}
