webpackJsonp([9],{BoFO:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"router-block"},[s("mt-header",{attrs:{fixed:"",title:""}},[s("mt-button",{attrs:{slot:"left",icon:"back"},on:{click:t.topBack},slot:"left"},[t._v("\n      问题详情\n    ")])],1),t._v(" "),s("div",{staticClass:"page-content"},[s("p",{staticClass:"ask-title"},[t._v(" "+t._s(t.detailObj.QUE_TITLE)+" ")]),t._v(" "),s("div",{staticClass:"ask-detail title-span-block"},[s("span",[t._v("问题描述")]),t._v(" "),s("p",[t._v(" "+t._s(t.detailObj.QUESTION)+" ")]),t._v(" "),t.detailObj.QUE_TIME?s("p",{staticClass:"text-left ask-time",staticStyle:{"margin-bottom":"0"}},[s("img",{staticClass:"time-icon",attrs:{src:a("xxCs")}}),t._v(" "),s("span",{directives:[{name:"show",rawName:"v-show",value:t.detailObj.QUE_TIME,expression:"detailObj.QUE_TIME"}]},[t._v(t._s(t._f("toTime")(t._f("toStamp")(t.detailObj.QUE_TIME))))])]):t._e()]),t._v(" "),s("div",{staticClass:"ask-answer title-span-block"},[s("span",[t._v("答案详情")]),t._v(" "),t.detailObj.RESPONSE?s("p",[t._v(t._s(t.detailObj.RESPONSE))]):t._e(),t._v(" "),t.detailObj.RESPONSE?t._e():s("p",{staticClass:"nomore-data"},[t._v("您的问题暂未被回复")]),t._v(" "),t.detailObj.RESPONSE?s("p",{staticClass:"text-left ask-time",staticStyle:{"margin-top":"1.2rem","margin-bottom":"0"}},[s("img",{staticClass:"time-icon",attrs:{src:a("Gvv4")}}),t._v(" "),s("span",[t._v(t._s(t.detailObj.resName))]),t._v(" "),s("img",{staticClass:"time-icon",staticStyle:{"margin-left":"2rem"},attrs:{src:a("xxCs")}}),t._v(" "),s("span",[t._v(" "+t._s(t._f("toTime")(t._f("toStamp")(t.detailObj.RES_TIME))))])]):t._e()]),t._v(" "),s("mt-button",{staticStyle:{float:"right"},attrs:{size:"small",type:"primary"},on:{click:t.topBack}},[t._v("返回列表")])],1)],1)},staticRenderFns:[]};var i=a("C7Lr")({name:"askDetail",props:{},data:function(){return{currentId:"",detailObj:{}}},beforeRouteEnter:function(t,e,a){a(function(e){e.currentId=t.params.id})},mounted:function(){},methods:{getDetail:function(t){var e=this;this.$get("/queres/detail",{qurResId:t}).then(function(t){t.success?e.detailObj=t.bean:e.$alert(t.message).then(function(t){e.$to("/ask/list")})})},topBack:function(){this.$to("/ask/list")},backList:function(){this.$back()}},watch:{currentId:function(t,e){this.getDetail(t)}}},s,!1,function(t){a("t+fN")},"data-v-05eff4af",null);e.default=i.exports},Gvv4:function(t,e,a){t.exports=a.p+"static/img/user.7565d1c.png"},"t+fN":function(t,e){}});