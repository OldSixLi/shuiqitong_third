webpackJsonp([5],{"2T1o":function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var s={name:"MessageList",props:{},data:function(){return{msgList:[],msgLists:[],loading:!1,currentPage:1,totalPage:1,loadingData:!1}},methods:{toDetail:function(){this.$to("/message/detail")},getList:function(){var t=this,e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:1;this.loading=!0,this.loadingData=!0,this.$get("/message/list",{currentPage:e,comId:this.comId,userId:"1"}).then(function(e){e.success?(t.currentPage=e.bean.pageNum,t.totalPage=e.bean.pageCount,t.currentPage>t.totalPage&&(t.currentPage=t.totalPage),e.bean&&e.bean.data&&e.bean.data.length>0&&(t.loading=0==t.totalPage||t.currentPage>=t.totalPage,t.msgList=t.msgList.concat(e.bean.data||[]))):(t.$tip("请求服务失败，请重试!"),t.totalPage=0,t.currentPage=0)},function(e){t.currentPage=0,t.totalPage=0}).finally(function(e){t.loadingData=!1})},toMsgDetail:function(t,e,a){if(t){var s=0;e&&(s=1),this.$to("/message/detail/"+t+"?isRead="+s),this.msgList[a].userId=1}},topBack:function(){this.$to("/")},loadMore:function(){this.loading=!0,this.currentPage++,this.getList(this.currentPage)}},beforeRouteEnter:function(t,e,a){a(function(t){t.msgList=[],t.getList(1)})},created:function(){var t=this;this.rightButtons=[{content:"标为已读",style:{background:"lightgray",color:"#fff"}},{content:"删除",style:{background:"red",color:"#fff"},handler:function(){return t.$confirm("确认删除吗?")}}]}},i={render:function(){var t=this,e=t.$createElement,s=t._self._c||e;return s("div",{staticClass:"router-block"},[s("mt-header",{attrs:{fixed:"",title:""}},[s("mt-button",{attrs:{slot:"left",icon:"back"},on:{click:t.topBack},slot:"left"},[t._v(t._s("消息列表"))])],1),t._v(" "),s("div",{staticClass:"page-content"},[[s("div",{directives:[{name:"infinite-scroll",rawName:"v-infinite-scroll",value:t.loadMore,expression:"loadMore"}],staticClass:"white",attrs:{"infinite-scroll-disabled":"loading","infinite-scroll-distance":"40"}},[t._l(t.msgList,function(e,i){return s("div",{staticClass:"msg-child",on:{click:function(a){t.toMsgDetail(e.id,e.userId,i)}}},[e.userId?s("img",{staticClass:"msg-img unread",attrs:{src:a("YyZC")}}):t._e(),t._v(" "),e.userId?t._e():s("img",{staticClass:"msg-img ",attrs:{src:a("5fgm")}}),t._v(" "),s("p",{staticClass:"title"},[t._v(t._s(e.title))]),t._v(" "),s("p",{staticClass:"time"},[s("img",{attrs:{src:a("xxCs")}}),s("span",[t._v(t._s(t._f("toTime")(t._f("toStamp")(e.createTime))))])])])}),t._v(" "),0==t.msgList.length&&0==t.totalPage&&0==t.currentPage?s("div",{staticClass:"text-center",staticStyle:{"margin-top":"5rem"}},[s("img",{attrs:{src:a("xoJX")}}),t._v(" "),s("p",{staticClass:"text-center no-msg"},[t._v("您暂未收到消息通知")])]):t._e(),t._v(" "),s("p",{directives:[{name:"show",rawName:"v-show",value:t.loadingData,expression:"loadingData"}],staticClass:"page-infinite-loading"},[s("mt-spinner",{attrs:{type:"fading-circle"}}),t._v("\n          加载中...\n        ")],1),t._v(" "),s("div",{directives:[{name:"show",rawName:"v-show",value:t.currentPage==t.totalPage&&t.currentPage>=1&&t.totalPage>=1,expression:"currentPage==totalPage&&currentPage>=1&&totalPage>=1"}],staticClass:"nomore-data"},[t._v("无更多内容")])],2)]],2)],1)},staticRenderFns:[]};var n=a("C7Lr")(s,i,!1,function(t){a("fW/X")},"data-v-01d7c9e4",null);e.default=n.exports},"5fgm":function(t,e,a){t.exports=a.p+"static/img/unread.bb8a173.png"},YyZC:function(t,e,a){t.exports=a.p+"static/img/readed.09bd6dd.png"},"fW/X":function(t,e){},xoJX:function(t,e,a){t.exports=a.p+"static/img/nocontent.2751829.png"}});