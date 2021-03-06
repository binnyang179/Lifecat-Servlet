package com.ten.service.impl;

import com.ten.bean.entity.UserIcon;
import com.ten.bean.vo.UserVO;
import com.ten.constant.Directory;
import com.ten.constant.Page;
import com.ten.dao.JdbcDAOFactory;
import com.ten.dao.UserIconDAO;
import com.ten.service.UserIconUpdateService;
import com.ten.util.DateTimeUtil;
import com.ten.util.ImageWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;

/**
 * 用户头像上传
 * <p>
 * 失败 Page.PAGE_USERHOME
 * 成功 Page.PAGE_USERHOME
 *
 * @date 2018/5/24
 * @auther ten
 */
public class UserIconUpdateServiceImpl implements UserIconUpdateService {

    private Logger logger = LoggerFactory.getLogger(UserIconUpdateServiceImpl.class);

    private UserIconDAO dao;
    private ImageWriter writer;

    public UserIconUpdateServiceImpl() {
        dao = (UserIconDAO) JdbcDAOFactory.getDaoByTableName("user_icon");
    }

    /**
     * 上传icon到服务器并写入数据库
     *
     * @param req  file 二进制请求
     * @param resp 响应
     */
    @Override
    public ServiceResult execute(HttpServletRequest req, HttpServletResponse resp) {

        UserVO userDTO = (UserVO) req.getSession().getAttribute("user");
        Integer userId = userDTO.getUserId();
        String dateTime = DateTimeUtil.getInstance().getCurrentTime();

        String iconPath = Directory.IMAGE_PATH + String.valueOf(userId) + "/icon/"
                + dateTime;
        writer = ImageWriter.newImageWriter(iconPath);

        // 将图片数据流写入磁盘
        writeUserIcon(req);

        UserIcon userIcon = new UserIcon();
        userIcon.setUserId(userId);
        userIcon.setIconPath(iconPath);
        userIcon.setCreateTime(dateTime);
        userIcon.setUpdateTime(dateTime);

        updateUserIcon(userIcon);

        return new ServiceResult.Builder(true).page(Page.PAGE_USERHOME).build();
    }

    @Override
    public void updateUserIcon(UserIcon userIcon) {
        try {
            dao.insertUserIcon(userIcon);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeUserIcon(HttpServletRequest req) {
        try {
            writer.writeImage(req.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
