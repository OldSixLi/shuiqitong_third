package com.greatchn.service.tax;

import org.springframework.stereotype.Service;

/**
 * TODO 我的企业控制层
 *
 * @author zy 2018-10-11
 * */
@Service
public class EnterpriseManangerSrv {

    /**
     * 根据条件检索企业
     *
     * */
    public void search(){
        // 企业名称

        // 企业联系人

        // 企业联系电话



    }

    /**
     * 查询所有分组
     *
     * */
    public void getAllGroup(){

        String sql="";
    }

    /**
     * 查询某个分组下的所有企业
     *
     * */
    public void getAllEnterpriseByGroup(){

    }

    /**
     * 新增/修改分组信息
     *
     * */
    public void saveOrUpdateGroup(){

        // 根据是否有id判断是否为修改

        // 若为修改，查询创建人，创建时间，放入分组信息中
    }

    /**
     * 将企业移动到另一个分组/取消分组信息
     *
     * */
    public void updateEnterpriseGroup(){

    }

    /**
     * 删除某个分组,分组中有信息不允许删除
     *
     * */
    public void deleteGroup(){

    }

    /**
     * 我管理的企业
     *
     * */
    public void myEnterprise(){

    }

    /**
     * 查询所有未分组的企业
     *
     * */
    public void getAllNonGrouping(){

    }

    /**
     * 将某个企业分配给某个员工
     *
     * */
    public void allocateEnterpriseToUser(){

    }

    /**
     *
     * 将某个分组分配给某个员工（即将某个分组下的所有企业分配给这个员工）
     *
     * */
    public void allocatedGroup(){

    }

    /**
     * 查询所有未分配的企业
     *
     * */
    public void getAllUnassigned(){

        // TODO  查询sql
        String sql="select * from enterprise_info ei left join tax_user_ent tue on ei.TAX_ID=tue.TAX_ID where ei.TAX_ID is null or TRIM(ei.TAX_ID)=''";


    }


}
