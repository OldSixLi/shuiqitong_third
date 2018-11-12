package com.greatchn.service;

import com.greatchn.common.dao.BaseDao;
import com.greatchn.po.ExceptionLog;
import com.greatchn.po.OperateLog;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

/**
 * 日志业务层
 *
 * @author zy 2018-9-12
 */
@Service
public class LogService {

    @Resource
    BaseDao baseDao;

    /**
     * 保存访问日志
     *
     * @param operateLog 日志对象
     * @author zy 2018-9-12
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveLog(OperateLog operateLog) {
        baseDao.save(operateLog);
    }

    /**
     * 有异常更新访问日志的异常信息
     *
     * @param operateLog  日志对象
     * @param exceptionId 异常日志id
     * @author zy 2018-9-12
     */
    @Transactional(rollbackFor = Exception.class)
    public void updateOpreatLog(OperateLog operateLog, Integer exceptionId) {
        operateLog.setExceptionId(exceptionId);
        baseDao.update(operateLog);
    }

    /**
     * 保存异常日志
     *
     * @param exceptionLog 日志对象
     * @author zy 2018-9-12
     */
    @Transactional(rollbackFor = Exception.class)
    public void saveExceptionLog(ExceptionLog exceptionLog) {
        baseDao.save(exceptionLog);
    }

}
