webpackJsonp([4],{"3cXf":function(t,e,a){t.exports={default:a("RJ+u"),__esModule:!0}},"RJ+u":function(t,e,a){var s=a("AKd3"),n=s.JSON||(s.JSON={stringify:JSON.stringify});t.exports=function(t){return n.stringify.apply(n,arguments)}},YRoe:function(t,e){},fevF:function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s=a("rVsN"),n=a.n(s),i=a("3cXf"),r=a.n(i),o={name:"singleQus",props:{obj:Object,oldans:Object},computed:{askType:function(){return this.obj.qustion.type},choose:function(){var t=this.obj.options||[];return t.forEach(function(t){t.label=t.content,t.value=t.id}),t},qusId:function(){return this.obj&&this.obj.qustion&&this.obj.qustion.id||""},selectType:function(){var t=this.obj.qustion.type;return"1"==t||"3"==t?"单选":"2"==t||"4"==t?"多选":""},areaDisable:function(){return"other"==this.radioVal&&("1"==this.askType||"3"==this.askType)||(this.checkVal.indexOf("other")>-1&&("2"==this.askType||"4"==this.askType)||"5"==this.askType)}},data:function(){return{radioVal:"",checkVal:[],areaVal:""}},mounted:function(){},methods:{getOptValue:function(t){var e=null;switch(t.qustion.type){case"1":e=this.radioVal;break;case"2":e=this.checkVal.join(",");break;case"3":e=this.radioVal;break;case"4":e=this.checkVal.join(",");break;case"5":e="";break;default:e=""}return e},getRealVal:function(t){},emitParent:function(){this.$emit("preValueChange",this.obj.qustion.id,this.getOptValue(this.obj),this.areaVal)}},watch:{obj:function(t,e){if(""!=this.qusId&&t.qustion&&e.qustion&&t.qustion.id!=e.qustion.id){this.$emit("preValueChange",e.qustion.id,this.getOptValue(e),this.areaVal);var a=void 0!=this.oldans,s=t.qustion.type;this.areaVal=a&&this.oldans.textAnswer||"","1"==s||"3"==s?(this.radioVal=a&&this.oldans.optAnswer||"","3"==s&&"other"!=t.options[t.options.length-1].id&&t.options.push({content:"其他",id:"other"}),this.checkVal=[]):"2"!=s&&"4"!=s||(this.checkVal=a?(this.oldans.optAnswer||"").split(","):[],this.radioVal="","4"==s&&"other"!=t.options[t.options.length-1].id&&t.options.push({content:"其他",id:"other"}))}},radioVal:function(){"other"!=this.radioVal&&"3"==this.askType&&(this.areaVal=""),this.emitParent()},checkVal:function(){this.checkVal.indexOf("other")<0&&"4"==this.askType&&(this.areaVal=""),this.emitParent()},areaVal:function(){this.emitParent()}}},c={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"single-qus"},[t.obj.qustion?[a("p",{staticClass:"ask-title"},[t._v(" "+t._s(t.obj.qustion.stem)+"\n      "),t.selectType?a("span",{staticClass:"select-type"},[t._v(t._s(t.selectType))]):t._e()]),t._v(" "),1==t.askType?a("div",[a("mt-radio",{attrs:{options:t.choose},model:{value:t.radioVal,callback:function(e){t.radioVal=e},expression:"radioVal"}})],1):t._e(),t._v(" "),2==t.askType?a("div",[a("mt-checklist",{attrs:{options:t.choose},model:{value:t.checkVal,callback:function(e){t.checkVal=e},expression:"checkVal"}})],1):t._e(),t._v(" "),3==t.askType?a("div",[a("mt-radio",{attrs:{options:t.choose},model:{value:t.radioVal,callback:function(e){t.radioVal=e},expression:"radioVal"}}),t._v(" "),a("div",{staticClass:"area-block"},[a("textarea",{directives:[{name:"model",rawName:"v-model.trim",value:t.areaVal,expression:"areaVal",modifiers:{trim:!0}}],attrs:{maxlength:"1000",disabled:!t.areaDisable,rows:"4",cols:"30",placeholder:"请在此输入您的答案"},domProps:{value:t.areaVal},on:{input:function(e){e.target.composing||(t.areaVal=e.target.value.trim())},blur:function(e){t.$forceUpdate()}}}),t._v(" "),a("p",{staticClass:"count-character"},[t._v("还剩"+t._s(500-t.areaVal.length)+"字")])])],1):t._e(),t._v(" "),4==t.askType?a("div",[a("mt-checklist",{attrs:{options:t.choose},model:{value:t.checkVal,callback:function(e){t.checkVal=e},expression:"checkVal"}}),t._v(" "),a("div",{staticClass:"area-block"},[a("textarea",{directives:[{name:"model",rawName:"v-model.trim",value:t.areaVal,expression:"areaVal",modifiers:{trim:!0}}],attrs:{maxlength:"1000",disabled:!t.areaDisable,rows:"4",cols:"30",placeholder:"请在此输入您的答案"},domProps:{value:t.areaVal},on:{input:function(e){e.target.composing||(t.areaVal=e.target.value.trim())},blur:function(e){t.$forceUpdate()}}}),t._v(" "),a("p",{staticClass:"count-character"},[t._v("还剩"+t._s(500-t.areaVal.length)+"字")])])],1):t._e(),t._v(" "),5==t.askType?a("div",{staticClass:"area-block"},[a("p",{staticClass:"p-area"},[t._v("请输入您的回答")]),t._v(" "),a("textarea",{directives:[{name:"model",rawName:"v-model.trim",value:t.areaVal,expression:"areaVal",modifiers:{trim:!0}}],attrs:{maxlength:"1000",disabled:!t.areaDisable,rows:"4",cols:"30",placeholder:"请在此输入您的答案"},domProps:{value:t.areaVal},on:{input:function(e){e.target.composing||(t.areaVal=e.target.value.trim())},blur:function(e){t.$forceUpdate()}}}),t._v(" "),a("p",{staticClass:"count-character"},[t._v("还剩"+t._s(500-t.areaVal.length)+"字")])]):t._e()]:t._e()],2)},staticRenderFns:[]};var l={name:"quesInfo",components:{question:a("C7Lr")(o,c,!1,function(t){a("gmIb"),a("fh8B")},"data-v-37898cae",null).exports},methods:{btnSubmit:function(){var t=this;if(this.askList.length!=this.answerList.length)return this.$alert("您还有问题未回答,请完成问卷后再进行提交!"),!1;this.answerList.forEach(function(e){e.optAnswer=t.filterOther(e.optAnswer)}),this.$post("/question/saveAnswer",{qnaId:this.detailId,answer:r()(this.answerList)}).then(function(e){e.success?t.$alert("提交问卷成功,感谢您的回答！").then(function(){t.$to("/")}):t.$alert("提交问卷失败，请重试！").then(function(){t.$to("/")})})},filterOther:function(t){var e=new RegExp(/(other,)|(,other)|(other)/,"gm");return t.toString().replace(e,"")},btnBegin:function(){this.currentIndex=1},btnBackStart:function(){this.currentIndex=0},spanClick:function(t){this.currentIndex=t},setQusVal:function(t,e,a){var s=-1;if("other"!=e&&""!=e||""!=a){var n=this.getValInAnswers(t);void 0!=n?(n.textAnswer=a,n.optAnswer=e):this.answerList.push({queId:t,textAnswer:a,optAnswer:e})}else this.answerList.some(function(e,a){return e.queId==t&&(s=a),e.queId==t})&&s>=0&&this.answerList.splice(s,1)},getValInAnswers:function(t){var e=-1;return this.answerList.some(function(a,s){return a.queId==t&&(e=s),a.queId==t})?this.answerList[e]:void 0},toMsgDetail:function(t,e){t&&(this.msgList[e].isread=1,this.$to("/questionnaire/detail/"+t))},btnPrev:function(){1!=this.currentIndex&&this.currentIndex--},btnNext:function(){this.currentIndex!=this.askList.length&&this.currentIndex++},getQuanDetail:function(t){var e=this;this.$post("/question/getQnaState",{qnaId:t}).then(function(t){return t.success?n.a.resolve(!0):n.a.reject(t.message)}).then(function(){return e.$post("/question/detail",{qnaId:t})}).then(function(t){t.success?(e.obj=t.bean.qna,e.askList=t.bean.questions):e.$alert("获取问卷信息失败，请重试！").then(function(t){e.$to("/")})}).catch(function(t){console.log(t),e.$alert(t).then(function(t){e.$to("/")})})}},data:function(){return{detailId:"",type:1,radio:"",radio1:[],introduction:"",color:"#888",isAnswered:!1,askList:[],currentIndex:0,answerList:[],oldAns:void 0,obj:{title:"",content:""}}},computed:{askObj:function(){var t,e=(t=this.askList.length>0&&this.askList[this.currentIndex-1]||{}).qustion&&t.qustion.id||"";return this.oldAns=e?this.getValInAnswers(e):void 0,t}},beforeRouteEnter:function(t,e,a){a(function(e){var a=t.params&&t.params.id;a&&(e.detailId=a,e.getQuanDetail(a))})},watch:{}},u={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{directives:[{name:"show",rawName:"v-show",value:t.obj.title,expression:"obj.title"}],staticClass:"container"},[a("h3",{staticClass:"text-center"},[t._v(" "+t._s(t.obj.title))]),t._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:0==t.currentIndex,expression:"currentIndex==0"}],staticClass:"ask-answer title-span-block"},[a("span",[t._v("问卷介绍")]),t._v(" "),a("p",[t._v(t._s(t.obj.content))])]),t._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:t.currentIndex>0,expression:"currentIndex>0"}],staticClass:"title-span-block"},[a("span",{staticStyle:{padding:"0.2rem 1rem"}},[t._v("\n      "+t._s(t.currentIndex)+" / "+t._s(t.askList.length)+"\n    ")]),t._v(" "),a("question",{attrs:{obj:t.askObj,oldans:t.oldAns},on:{preValueChange:t.setQusVal}})],1),t._v(" "),a("div",{staticClass:"end-area"}),t._v(" "),a("div",{directives:[{name:"show",rawName:"v-show",value:t.askList.length>0&&t.currentIndex>0,expression:"askList.length>0&&currentIndex>0"}],staticClass:"tit-count spans"},t._l(t.askList,function(e,s){return a("mt-badge",{key:s,staticClass:"index-span",class:t.getValInAnswers(e.qustion.id)?"span-full":"span-empty",attrs:{size:"small"},nativeOn:{click:function(e){t.spanClick(s+1)}}},[t._v(t._s(s+1))])})),t._v(" "),a("div",{staticClass:"bottom-btn-area"},[0==t.currentIndex?a("mt-button",{staticClass:"next bottom-btn",staticStyle:{width:"100%"},attrs:{type:"primary",size:"large"},on:{click:t.btnBegin}},[t._v("开始答题")]):t._e(),t._v(" "),1==t.currentIndex?a("mt-button",{staticClass:"next bottom-btn",attrs:{type:"primary",size:"large"},on:{click:t.btnBackStart}},[t._v("返回")]):t._e(),t._v(" "),t.currentIndex>1?a("mt-button",{staticClass:"next bottom-btn",attrs:{type:"primary",size:"large"},on:{click:t.btnPrev}},[t._v("上一题")]):t._e(),t._v(" "),t.currentIndex<t.askList.length&&t.currentIndex>0?a("mt-button",{staticClass:"next bottom-btn",attrs:{type:"primary",size:"large"},on:{click:t.btnNext}},[t._v("下一题")]):t._e(),t._v(" "),t.currentIndex==t.askList.length?a("mt-button",{staticClass:"next bottom-btn",attrs:{type:"primary",size:"large"},on:{click:t.btnSubmit}},[t._v("提交")]):t._e()],1),t._v(" "),a("div",{staticClass:"zhanwei"})])},staticRenderFns:[]};var h=a("C7Lr")(l,u,!1,function(t){a("YRoe")},"data-v-5e97ab63",null);e.default=h.exports},fh8B:function(t,e){},gmIb:function(t,e){}});