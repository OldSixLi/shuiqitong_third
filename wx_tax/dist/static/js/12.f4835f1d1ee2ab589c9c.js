webpackJsonp([12],{"5IY1":function(t,s,e){"use strict";Object.defineProperty(s,"__esModule",{value:!0});var i={name:"QuestionDetail",data:function(){return{msgTitle:"为什么太平洋周围地震最多",msgContent:"这些消息的描述",answerUser:"张三丰",detailId:""}},created:function(){this.detailId=this.$param(this).id},methods:{topBack:function(){this.$to("/questionnaire/quesinfo")},backList:function(){this.$back()},showview:function(t){console.log(t)}}},a={render:function(){var t=this,s=t.$createElement,e=t._self._c||s;return e("div",{staticClass:"container"},[e("mt-header",{attrs:{fixed:"",title:""}},[e("mt-button",{attrs:{slot:"left",icon:"back"},on:{click:t.topBack},slot:"left"},[t._v(t._s("消息详情"))])],1),t._v(" "),e("mt-checklist",{attrs:{title:"复选框列表",options:["选项A","选项B","选项C"]},model:{value:t.value,callback:function(s){t.value=s},expression:"value"}}),t._v(" "),e("div",{staticClass:"content"},[e("p",{staticClass:"text-left question-title"},[e("span",[t._v("问卷标题：")]),e("span",{staticClass:"right"},[t._v(t._s(t.msgTitle))])]),t._v(" "),t._m(0),t._v(" "),t._m(1),t._v(" "),t._m(2),t._v(" "),t._m(3),t._v(" "),t._m(4),t._v(" "),t._m(5),t._v(" "),t._m(6),t._v(" "),t._m(7),t._v(" "),e("mt-button",{staticStyle:{float:"right"},attrs:{size:"small",type:"primary"},on:{click:t.backList}},[t._v("返回列表")])],1)],1)},staticRenderFns:[function(){var t=this.$createElement,s=this._self._c||t;return s("p",{staticClass:"text-left question-title"},[s("span",[this._v("添加时间：")]),s("span",{staticClass:"right"},[this._v("2018-09-11 17:13:04")])])},function(){var t=this.$createElement,s=this._self._c||t;return s("p",{staticClass:"text-left question-title"},[s("span",[this._v("添加人员：")]),s("span",{staticClass:"right"},[this._v("吴老六")])])},function(){var t=this.$createElement,s=this._self._c||t;return s("p",{staticClass:"text-left question-title"},[s("span",[this._v("最后修改时间：")]),s("span",{staticClass:"right"},[this._v("2018-09-11 17:13:04")])])},function(){var t=this.$createElement,s=this._self._c||t;return s("p",{staticClass:"text-left question-title"},[s("span",[this._v("最后修改人员：")]),s("span",{staticClass:"right"},[this._v("吴老六")])])},function(){var t=this.$createElement,s=this._self._c||t;return s("p",{staticClass:"text-left question-title"},[s("span",[this._v("问卷访问地址：")]),s("span",{staticClass:"right"},[s("a",{attrs:{href:"https://www.baidu.com"}},[this._v("https://www.baidu.com")])])])},function(){var t=this.$createElement,s=this._self._c||t;return s("p",{staticClass:"text-left question-title"},[s("span",[this._v("开始时间：")]),s("span",{staticClass:"right"},[this._v("2018-09-11 17:13:04")])])},function(){var t=this.$createElement,s=this._self._c||t;return s("p",{staticClass:"text-left question-title"},[s("span",[this._v("结束时间：")]),s("span",{staticClass:"right"},[this._v("2018-09-11 17:13:04")])])},function(){var t=this.$createElement,s=this._self._c||t;return s("p",{staticClass:"text-left question-title"},[s("span",[this._v("问卷状态：")]),s("span",{staticClass:"right"},[this._v("已开启")])])}]};var n=e("C7Lr")(i,a,!1,function(t){e("og56")},"data-v-100c56dd",null);s.default=n.exports},og56:function(t,s){}});