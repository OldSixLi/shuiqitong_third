webpackJsonp([13],{K0af:function(t,e,s){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var a={name:"MessageDetail",data:function(){return{detailId:"",obj:{},userId:"1",isRead:!1}},created:function(){this.detailId=this.$param(this).id},beforeRouteEnter:function(t,e,s){s(function(e){e.detailId=t.params.id,e.isRead=t.query&&"1"==t.query.isRead,e.obj={},e.getDetail(e.detailId)})},methods:{topBack:function(){this.$to("/message/list")},sendBackInfo:function(){var t=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"",e=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"";this.$post("/message/receipt",{messageId:t,userId:e}).then(function(t){console.log(t)})},getDetail:function(t){var e=this;this.$get("/message/detail",{messageId:t}).then(function(s){s.success?(e.obj=s.bean,e.isRead||e.sendBackInfo(t,e.userId)):e.$alert(s.message).then(function(t){e.$to("/message/list")})})}},watch:{detailId:function(t,e){this.getDetail(t)}},beforeRouteUpdate:function(t,e,s){this.obj={},s()}},i={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"router-block"},[a("mt-header",{attrs:{fixed:"",title:""}},[a("mt-button",{attrs:{slot:"left",icon:"back"},on:{click:t.topBack},slot:"left"},[t._v(t._s("消息详情"))])],1),t._v(" "),a("div",{staticClass:"page-content"},[a("p",{staticClass:"text-left question-title"},[t._v(t._s(t.obj.title||""))]),t._v(" "),a("p",{staticClass:"text-left ask-time"},[a("img",{staticClass:"time-icon",attrs:{src:s("xxCs")}}),t._v(" "),t.obj.createTime?a("span",[t._v(" 发布时间 : "+t._s(t._f("toTime")(t._f("toStamp")(t.obj.createTime))))]):t._e()]),t._v(" "),a("div",{staticClass:"answer-area"},[t._v(t._s(t.obj.content))]),t._v(" "),a("mt-button",{staticStyle:{float:"right"},attrs:{size:"small",type:"primary"},on:{click:t.topBack}},[t._v("返回列表")])],1)],1)},staticRenderFns:[]};var n=s("C7Lr")(a,i,!1,function(t){s("QOsb")},"data-v-97e38bf6",null);e.default=n.exports},QOsb:function(t,e){}});