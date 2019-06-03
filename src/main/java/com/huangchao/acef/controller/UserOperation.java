package com.huangchao.acef.controller;

import com.github.pagehelper.PageInfo;
import com.huangchao.acef.entity.*;
import com.huangchao.acef.global.Common;
import com.huangchao.acef.service.UserService;
import com.huangchao.acef.utils.CookieUtil;
import com.huangchao.acef.utils.Md5;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.interceptor.TransactionAspectSupport;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.huangchao.acef.global.Common.getLanguage;


/**
 * 本类人员信息相关处理类
 */

@Controller
@RequestMapping("/user")
public class UserOperation {

    //获取业务层操作类
    @Autowired
    private UserService userService;
    //统一cookie存活基准时间
    @Value("${survivalTime}")
    private int survivalTime;
    //注入默认界面语言
    @Value("${defaultLanguage}")
    String defaultLanguage;
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

    /*********************************************************** 语言 *********************************************************************/

//    //设置界面语言

//    @RequestMapping("/language")
//    @ResponseBody
//    public Map<String, String> setLanguage(String language, HttpServletResponse response, HttpServletRequest request) {
//        //用于返回设置结果
//        Map<String, String> result = new HashMap<>();
//        result.put("result", language);
//        //保存语言到cookie
//        CookieUtil.saveCookie("language", language, response, survivalTime);
//        return result;
//    }

    /*********************************************************** 成员信息 *********************************************************************/

    //成员信息上传
    @RequestMapping("/mis")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, Object> memberIntroductionSave(MemberIntroduction memberIntroduction, MultipartFile picture) {
        //用于返回保存结果
        Map<String, Object> result = new HashMap<>();

        try {
            if (picture != null && !picture.isEmpty()) {
                //图片处理
                memberIntroduction = pictureDispose(memberIntroduction, picture);
            }
            //保存数据到数据库，可能出现异常
            userService.addShowMember(memberIntroduction);
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

    //前端展示成员信息获取
    @RequestMapping("/pgami/{currentPage}/{pageSize}")
    @ResponseBody
    public PageInfo<GetMemberIntroduction> getAllShowMemberIntroduction(
            //当前页号                                                一页的数据量
            @PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize, HttpServletRequest request) {
        String language = getLanguage(request);
        return userService.getAllShowMemberIntroduction(language != null ? language : defaultLanguage, currentPage, pageSize);
    }

    //后台管理展示成员信息获取
    @RequestMapping("/bgami/{currentPage}/{pageSize}")
    @ResponseBody
    //当前页号                                                一页的数据量
    public PageInfo<MemberIntroduction> getAllManageMemberIntroduction(@PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize) {
        return userService.getAllManageMemberIntroduction(currentPage, pageSize);
    }

    //后台管理单个成员信息获取
    @RequestMapping("/bgomi/{id}")
    @ResponseBody
    public MemberIntroduction getOneManageMemberIntroduction(@PathVariable(value = "id") int id) {
        return userService.getOneManageMemberIntroduction(id);
    }

    //后台管理成员信息删除
    @RequestMapping("/bdmi")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> deleteMemberIntroduction(int[] idList, String[] imgPaths) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();

        try {
            //执行删除操作
            userService.deleteMemberIntroduction(idList);
            if (imgPaths != null) {
                for (String s : imgPaths) {
                    //删除图片
                    Common.deletePreviousPicture(s,filePath,imgPath);
                }
            }

            result.put("result", "1");
        } catch (Exception e) {
            result.put("result", "0");
            e.printStackTrace();
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //后台管理成员信息修改
    @RequestMapping("/bcmi")
    @ResponseBody
    @Transactional(rollbackFor = {Exception.class}) //所有异常都回滚
    public Map<String, String> changeMemberIntroduction(MemberIntroduction memberIntroduction, MultipartFile picture) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();
        try {
            //若重新上传了图片
            if (picture != null && !picture.isEmpty()) {
                //删除图片
                Common.deletePreviousPicture(memberIntroduction.getImgPath(),filePath,imgPath);
                //图片处理
                memberIntroduction = pictureDispose(memberIntroduction, picture);

            } else {
                //设置图片映射路径为空，即不更新
                memberIntroduction.setImgPath(null);
            }
            //执行修改操作
            userService.changeMemberIntroduction(memberIntroduction);
            result.put("result", "1");

        } catch (Exception e) {
            result.put("result", "0");
            e.printStackTrace();
            //回滚
            TransactionAspectSupport.currentTransactionStatus().setRollbackOnly();
        }
        return result;
    }

    //图片保存
    private MemberIntroduction pictureDispose(MemberIntroduction memberIntroduction, MultipartFile picture) throws IOException {
        //获取文件名
        String fileName = picture.getOriginalFilename();
        //获取文件后缀名
        String suffixName = fileName.substring(fileName.lastIndexOf("."));
        //重新生成图片名
        fileName = UUID.randomUUID() + suffixName;
        //设置图片映射路径
        memberIntroduction.setImgPath(mapPath + imgPath + fileName);
        //保存图片到指定文件夹,可能出现io异常
        picture.transferTo(new File(filePath + imgPath + fileName));
        return memberIntroduction;
    }

    /*********************************************************** 意见反馈 *********************************************************************/
    //意见反馈上传接口
    @RequestMapping("/fb")
    @ResponseBody
    public Map<String, String> setFeedback(Feedback feedback) {
        //用于保存保存结果
        Map<String, String> result = new HashMap<>();
        try {
            userService.setFeedback(feedback);
            result.put("result", "1");

        } catch (Exception e) {
            result.put("result", "0");
            e.printStackTrace();
        }
        return result;
    }

    //意见反馈信息获取接口
    @RequestMapping("/gfb/{currentPage}/{pageSize}")
    @ResponseBody
    public PageInfo<Feedback> getFeedback(@PathVariable(value = "currentPage") int currentPage, @PathVariable(value = "pageSize") int pageSize) {
        return userService.getFeedback(currentPage, pageSize);

    }

    //后台意见反馈信息批量删除
    @RequestMapping("/bdfb")
    @ResponseBody
    public Map<String, String> deleteFeedback(int[] idList) {
        //用于返回结果
        Map<String, String> result = new HashMap<>();
        try {
            userService.deleteFeedback(idList);
            result.put("result", "1");
        } catch (Exception e) {
            result.put("result", "0");
            e.printStackTrace();
        }
        return result;
    }

    /******************************************************** 登录 ************************************************************************/
    //登录状态查询
    @RequestMapping("/isLogin")
    @ResponseBody
    public Map<String, String> isLogin(HttpServletRequest request) {
        //用于返回查询结果,成功则返回1和昵称
        Map<String, String> result = new HashMap<>();

        //获取从客户端携带过来的cookie
        Cookie[] cookies = request.getCookies();
        //从cookie的数组中查找指定名称的cookie
        Cookie cookie = CookieUtil.findCookie(cookies, "loginUserId");
        //若存在
        if (cookie != null) {
            //如果服务器本次会话保存有登陆状态，判断loginUserId是否和cookie中的id一致
            String loginUserId = (String) request.getSession().getAttribute("loginUserId");
            if (loginUserId != null) {
                //若一致则cookie登陆有效
                if (loginUserId.equals(cookie.getValue()))
                    result.put("result", "1");
                else
                    result.put("result", "0");
            } else {
                //若为空，则在数据库查询，查询成功后保存到本次会话session中
                User user = userService.findUserById(cookie.getValue());
                if (user.getId() != null) {
                    request.getSession().setAttribute("loginUserId", user.getId());
                    result.put("result", "1");
                } else
                    result.put("result", "0");
            }
        } else {
            //若cookie为空则是未登录
            result.put("result", "0");
        }

        return result;
    }

    //管理员登陆
    @RequestMapping("/login")
    @ResponseBody
    public Map<String, String> login(User user, HttpServletRequest request, HttpServletResponse response) {
        //用于返回登录结果
        Map<String, String> result = new HashMap<>();

        //检查用户是否存在
        //从数据库获取登录用户账户信息
        User userFinded;
        if (user.getEmailAccount() != null && user.getPassword() != null && !user.getEmailAccount().equals("") && !user.getPassword().equals(""))
            userFinded = userService.findUserByEmailAccount(user.getEmailAccount());
        else {
            result.put("result", "4");
            return result;
        }
        if (userFinded != null) {
            //判断密码是否正确
            if (userFinded.getPassword() != null && Md5.encode(user.getPassword()).equals(userFinded.getPassword())) {
                //身份确认
                result.put("result", "1");
                //获取管理员id
                String id = userService.findIdByEmailAccount(user.getEmailAccount());
                //本次会话保存登录状态
                request.getSession().setAttribute("loginUserId", id);
                //存储cookie,完成记住用户登录状态功能:
                CookieUtil.saveCookie("loginUserId", id, response, survivalTime);
                return result;
            } else {
                //密码错误
                result.put("result", "2");
                return result;
            }
        } else {
            //未找到该用户
            result.put("result", "3");
            return result;
        }
    }

}

