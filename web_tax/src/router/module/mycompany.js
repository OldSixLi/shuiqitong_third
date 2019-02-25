/*
 * 企业信息管理模块路由注册
 * @Author:马少博 (ma.shaobo@qq.com)
 * @Date: 2018年8月31日15:07:07
 * @Last Modified by: 马少博
 * @Last Modified time:2018年9月7日14:42:31
 */
/* jshint esversion: 6 */
"use strict"

import Empty from "@/page/Empty.vue";

export default [{
    path: "/company",
    name: "我的企业",
    component: Empty, 
    redirect: "noredirect",
    meta: {
      title: "我的企业",
      icon: "el-icon-document",
      funPoint: "company"
    },
    children: [{
        path: "/company/checked",
        name: "企业列表",
        component: () => import("@/page/mycompany/checkedList.vue"),
        meta: {
          title: "企业列表",
          funPoint: "company-checked"
        },
      },
      {
        path: "/company/checking",
        name: "待审核管理",
        component: () => import("@/page/company/checkList.vue"),
        meta: {
          title: "待审核管理",
          // funPoint: "company-checking"
        }
      }
    ]
  }

]
