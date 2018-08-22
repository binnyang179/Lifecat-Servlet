package com.ten.service.impl;

import com.ten.bean.entity.DiaryDO;
import com.ten.constant.Page;
import com.ten.dao.DAOFactory;
import com.ten.dao.DiaryDAO;
import com.ten.dao.jdbcimpl.JdbcDAOFactory;
import com.ten.service.DiaryUpdateService;
import com.ten.util.DateTimeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.SQLException;

/**
 * 日记内容更新
 *
 * @date 2018/5/24
 * @auther ten
 */
public class DiaryUpdateServiceImpl implements DiaryUpdateService {
    private Logger logger = LoggerFactory.getLogger(DiaryUpdateServiceImpl.class);

    public DiaryUpdateServiceImpl() {
    }

    @Override
    public ServiceResult execute(HttpServletRequest req, HttpServletResponse resp) {

        // form参数
        String diaryId = req.getParameter("diaryId");
        String diaryName = req.getParameter("diaryName");
        String diaryText = req.getParameter("diaryText");
        // 时间
        String dateTime = DateTimeUtil.getInstance().getCurrentTime();

        logger.debug("diary id:{} name:{} text:{} date:{}", diaryId, diaryName, diaryText, dateTime);
        assert diaryId != null;

        // 获取DAO实例
        DAOFactory factory = new JdbcDAOFactory();
        DiaryDAO dao = (DiaryDAO) factory.getDaoByTableName("diary");

        DiaryDO diaryDO = new DiaryDO();
        diaryDO.setDiaryId(Integer.valueOf(diaryId));
        diaryDO.setDiaryName(diaryName);
        diaryDO.setdiaryText(diaryText);
        diaryDO.setdiaryGmtModified(dateTime);

        boolean success = false;
        try {
            dao.updateDiary(diaryDO);
            success = true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (!success) {
            return new ServiceResult.Builder(false)
                    .errormsg("数据库更新异常")
                    .page(Page.PAGE_UPDIARY)
                    .build();
        }

        return new ServiceResult.Builder(true)
                .page(Page.PAGE_USERHOME).build();
    }

    @Override
    public void updateDiary(DiaryDO diaryDO) {

    }
}