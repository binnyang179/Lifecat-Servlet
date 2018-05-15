package com.wang.servicemodel;

import com.wang.bean.User;
import com.wang.daomodel.DAOModelFactory;
import com.wang.daomodel.UserDAOModel;
import com.wang.util.HOST;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;
import java.util.logging.Logger;

/**
 * UpUserPswModel: 修改密码
 * <p>
 * 访问范围: 全局
 * 调用者: Servlet
 * 异常检测: try-catch异常处理层
 * <p>
 * 1. 通过DAOModel获取DAO, 获取Session user
 * 2. 向数据库user更新password
 * <p>
 * ps: updateUserPsw(old,new)负责进行原密码校验
 *
 * @auther ten
 */
class UpUserPswModel implements ServiceModel {

    private Logger logger;

    private UpUserPswModel() {
        logger = Logger.getLogger("UpUserPswModel");

    }

    static ServiceModel getModel() {
        return new UpUserPswModel();
    }

    @Override
    public ModelResult execute(HttpServletRequest req, HttpServletResponse resp) {
        //Form中检测两次密码一致性
        String oldpassword = req.getParameter("oldpassword");
        String newpassword1 = req.getParameter("newpassword1");
        String newpassword2 = req.getParameter("newpassword2");

        logger.info("oldpassword " + oldpassword);
        logger.info("newpassword1 " + newpassword1);
        logger.info("newpassword2 " + newpassword2);

        //获取会话user
        User user = (User) req.getSession().getAttribute("user");

        UserDAOModel daoModel = (UserDAOModel) DAOModelFactory.getDAOModelByName(user.getId(), "UserDAOModel");

        try {
            //更新密码: updateUserPsw()负责进行校验
            daoModel.updateUserPsw(oldpassword, newpassword1);

            //更新user
            user = daoModel.queryUser();
        }/*
         * 更新异常:
         *
         * 1. 原密码校验失败
         * 2. 跳转page: userhome
         */ catch (IllegalArgumentException e) {
            e.printStackTrace();
            return new ModelResult.Builder(true).errormsg("原密码错误").page(HOST.PAGE_USERHOME).build();
        } catch (SQLException e) {
            e.printStackTrace();
            return new ModelResult.Builder(true).errormsg("SQLException").page(HOST.PAGE_USERHOME).build();
        }

        /*
         * 更新成功:
         *
         * 1. 更新Session: user
         * 2. 跳转page: userhome
         */
        req.getSession().setAttribute("user", user);
        return new ModelResult.Builder(false).page(HOST.PAGE_USERHOME).build();
    }
}

