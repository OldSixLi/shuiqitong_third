webpackJsonp([19],{"+mLZ":function(e,t){},"0nxc":function(e,t){},"0oZw":function(e,t){},"6+EP":function(e,t){},"87HC":function(e,t){},"8O0P":function(e,t){},EknJ:function(e,t){},Gdeq:function(e,t){},Kzx6:function(e,t){},NHnr:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var o=n("hn2z"),a=n.n(o),r={render:function(){var e=this.$createElement,t=this._self._c||e;return t("div",{attrs:{id:"app"}},[t("router-view")],1)},staticRenderFns:[]};var i=n("C7Lr")({name:"App"},r,!1,function(e){n("dflY")},null,null).exports,u=n("IHPB"),c=n.n(u),s=n("pRNm"),l=n.n(s),m={render:function(){var e=this.$createElement,t=this._self._c||e;return t("keep-alive",[t("router-view")],1)},staticRenderFns:[]},f=n("C7Lr")(null,m,!1,null,null,null).exports,p={render:function(){var e=this.$createElement,t=this._self._c||e;return t("el-row",[t("div",{staticClass:"err-info-page"},[t("img",{staticStyle:{width:"60%"},attrs:{src:n("NeAH")}}),this._v(" "),t("h4",{staticStyle:{margin:"6rem 0"}},[this._v("对不起,您访问的路径不存在!")]),this._v(" "),t("mt-button",{attrs:{size:"large"},on:{click:this.backIndex}},[this._v("返回主页")])],1)])},staticRenderFns:[]},h=n("C7Lr")({name:"Lost",methods:{backIndex:function(){this.$to("/")}}},p,!1,null,null,null).exports,d=[{path:"/system",component:f,name:"系统管理",redirect:"",meta:{title:"系统管理",icon:"el-icon-setting",funPoint:"system",requireAuth:!0},children:[{path:"/system/role",name:"用户列表",component:function(){return n.e(14).then(n.bind(null,"oR2l"))},meta:{title:"用户列表",funPoint:"system-role"}},{path:"/system/user",name:"用户列表",component:function(){return n.e(15).then(n.bind(null,"m5kD"))},meta:{title:"用户列表",funPoint:"system-user"},children:[{path:"/system/user/depart/:departId",component:function(){return n.e(1).then(n.bind(null,"VecE"))}},{path:"",component:function(){return n.e(1).then(n.bind(null,"VecE"))}}]},{path:"/system/depart",name:"部门选择",component:function(){return n.e(1).then(n.bind(null,"VecE"))},meta:{title:"部门选择",funPoint:"system-depart"}}]}],v=[{path:"/ask",name:"问答功能",component:f,redirect:"noredirect",meta:{title:"问答功能",icon:"el-icon-document",funPoint:"ask"},children:[{path:"/ask/add",name:"添加问题",component:function(){return n.e(8).then(n.bind(null,"feip"))},meta:{title:"添加问题",funPoint:"ask-add"}},{path:"/ask/list",name:"问题列表",component:function(){return Promise.all([n.e(0),n.e(7)]).then(n.bind(null,"NqdR"))},meta:{title:"问题列表",funPoint:"ask-list"}},{path:"/ask/detail/:id",name:"问题详情",component:function(){return Promise.all([n.e(0),n.e(9)]).then(n.bind(null,"BoFO"))},meta:{title:"问题详情",funPoint:"ask-detail"}}]}],g=[{path:"/message",name:"消息通知",redirect:"noredirect",component:f,meta:{title:"消息通知",icon:"el-icon-edit",funPoint:"message"},children:[{path:"/message/detail/:id",name:"消息详情",component:function(){return Promise.all([n.e(0),n.e(12)]).then(n.bind(null,"K0af"))},meta:{title:"消息详情",funPoint:"message-detail"}},{path:"/message/list",name:"消息列表",component:function(){return Promise.all([n.e(0),n.e(5)]).then(n.bind(null,"2T1o"))},meta:{title:"消息列表",funPoint:"message-list"}}]}],I=[{path:"/questionnaire",name:"问卷功能",component:f,redirect:"noredirect",meta:{title:"问卷功能",icon:"el-icon-document",funPoint:"question"},children:[{path:"/questionnaire/detail/:id",name:"问卷详情",component:function(){return n.e(13).then(n.bind(null,"5IY1"))},meta:{title:"问卷详情",funPoint:"question-detail"}},{path:"/questionnaire/quesinfo/:id",name:"问卷信息",component:function(){return n.e(4).then(n.bind(null,"fevF"))},meta:{title:"问卷信息",funPoint:"question-info"}}]}];Vue.use(l.a);var V=[].concat(c()(d),c()(v),c()(g),c()(I)),k=new l.a({mode:"history",routes:[{path:"/",name:"Index",component:f,children:[{path:"/",name:"主页",component:function(){return n.e(3).then(n.bind(null,"8/c5"))}},{path:"/regist",name:"注册信息",component:function(){return n.e(6).then(n.bind(null,"mph0"))}},{path:"/infoChecking/:state",name:"检查结果",component:function(){return n.e(2).then(n.bind(null,"FjQO"))}},{path:"/auth",name:"授权登陆",component:function(){return n.e(17).then(n.bind(null,"Yoru"))}},{path:"/clear",name:"清除应用",component:function(){return n.e(11).then(n.bind(null,"5HbL"))}},{path:"/first",name:"获取信息",component:function(){return n.e(16).then(n.bind(null,"2PLt"))}}].concat(c()(V))},{path:"/noauth",name:"403",component:function(){return n.e(10).then(n.bind(null,"13Z5"))}},{path:"*",name:"404",component:h}]}),y=n("lRwf"),x=n.n(y),b=n("SJI6"),w=n.n(b),C=n("zsLt"),R=n.n(C),P=n("rVsN"),S=n.n(P),_=n("wXGk"),A=[].concat(c()(_.c)),$={state:{routers:T(V,A),roles:[]},mutations:{changeRole:function(e,t){e.roles=t},UPDATE_ROUTERS:function(e,t){e.routers=T(V,t)}},actions:{getAppMenu:function(e,t){var n=e.commit;return new S.a(function(e){n("UPDATE_ROUTERS",[].concat(c()(new R.a([].concat(c()(A),c()(t)))))),e()})}}};function T(e,t){return e.filter(function(e){return!!function(e,t){return!(!$||!$.state.roles.some(function(e){return"sys"==e.roleName}))||!t.meta||!t.meta.funPoint||e.some(function(e){return t.meta.funPoint==e})}(t,e)&&(e.children&&e.children.length&&(e.children=T(e.children,t)),!0)})}var U=$,q={departmentId:function(e){return e.departmentId},checkValue:function(e){return e.checkValue},companyTree:function(e){return e.companyTree},wxComTree:function(e){return e.wxComTree},roleId:function(e){return e.currentSelectRole},roleName:function(e){return e.currentSelectRoleName},userInfo:function(e){return e.userInfo},companyId:function(e){return e.companyId},comUserId:function(e){return e.comUserId},checkState:function(e){return e.checkState},token:function(e){return e.token},failReason:function(e){return e.failReason}},E=window.localStorage;x.a.use(w.a);var L=new w.a.Store({modules:{menu:U},state:{isLogin:!0,token:_.e||E.getItem("shuiqitong_token")||"",comInfo:{},userInfo:{},checkState:E.getItem("checkState")||4,failReason:"",departmentId:0,currentSelectRole:"",currentSelectRoleName:"",checkValue:"",companyId:E.getItem("currenComId")||_.f||"",comUserId:E.getItem("comUserId")||_.g||"",companyTree:[],wxComTree:[]},getters:q,mutations:{changeToken:function(e,t){t!=e.token&&(E.setItem("shuiqitong_token",t),axios.defaults.headers.common.token=t),e.token=t},changeCheckState:function(e,t){var n=arguments.length>2&&void 0!==arguments[2]?arguments[2]:"";2==t&&(e.failReason=n),E.setItem("checkState",t),e.checkState=t},changeActiveDepartId:function(e,t){e.departmentId=t},changeCheckValue:function(e,t){e.checkValue=t},changeCurrentSelectRole:function(e,t){e.currentSelectRole=t},changeRoleName:function(e,t){e.currentSelectRoleName=t},setComId:function(e,t){E.setItem("currenComId",t),e.companyId=t},setComUserId:function(e,t){E.setItem("comUserId",t),e.comUserId=t},setComTree:function(e,t){e.companyTree=t},setwxComTree:function(e,t){e.wxComTree=t},setwxUserInfo:function(e,t){e.userInfo=t}}}),N=n("E4C3"),O=n.n(N);n("ve9D");O.a.configure({showSpinner:!1}),k.beforeEach(function(e,t,n){O.a.start();L.state.checkState;var o=L.state.menu.routers,a=L.state.companyId,r=L.state.comUserId,i=L.state.token,u=L.state.menu.roles;if("清除应用"!=e.name)try{n(),function(e,t,n,o,a){e&&t||"获取信息"==n.name||"授权登陆"==n.name?(a||"获取信息"==n.name||"授权登陆"==n.name||(r={comId:L.state.companyId,userId:L.state.comUserId},Vue.prototype.$post("/ent/login/reLogin",r).then(function(e){if(e.success&&"0"!=e.bean.flag){var t=e.bean.userRight,n=[];t.forEach(function(e,t){n.push(e.rightName)}),L.commit("changeToken",e.bean.token),L.commit("changeRole",e.bean.roles),L.dispatch("getAppMenu",n)}}),o()),o()):o("/first?path="+n.fullPath);var r}(a,r,e,n,i),function(e,t,n){var o=L.state.checkState;"获取信息"==t.name||"授权登陆"==t.name?n():1!=o&&"检查结果"!=t.name?2!=o&&4!=o||"注册信息"!=t.name?n({path:"/infoChecking/"+o+"?redirect="+encodeURIComponent(t.path)}):n():"/regist"==t.path?Vue.prototype.$alert("您的企业已通过审核,不能重新提交审核信息").then(function(e){n("/")}):n()}(0,e,n),function(e,t,n,o){0==e.length&&"检查结果"!=t.name?n():function(e,t,n){if(e.meta&&e.meta.funPoint){var o=e.matched[e.matched.length-1].parent||null;o&&!function e(t,n){var o=n.some(function(n){return n.children?e(t,n.children):(n.meta&&n.meta.funPoint)==t.meta.funPoint});return o}(e,t)?n("/noauth"):n()}else n()}(t,o,n)}(u,e,n,o)}catch(e){console.log(e)}else n()}),k.afterEach(function(e,t){O.a.done(),"/"==e.path&&new S.a(function(e,t){Vue.prototype.$get("/ent/login/getRoleAndRight").then(function(t){t.success?(L.commit("changeRole",t.bean.roles),L.dispatch("getAppMenu",t.bean.right),e(!0)):e(!1)}).catch(function(t){e(!1)})})});n("miO0");var F=n("OTYB"),D=n.n(F),Y=(n("Phrt"),n("XyCN")),B=n.n(Y),H=(n("Kzx6"),n("cefO")),j=n.n(H),M=(n("0oZw"),n("Qh9b")),G=n.n(M),J=(n("YhU3"),n("nWv3")),z=n.n(J),K=(n("RUOp"),n("1nxA")),Z=n.n(K),Q=(n("Zjav"),n("XFAJ")),X=n.n(Q),W=(n("kMAb"),n("Occv")),ee=n.n(W),te=(n("uGcx"),n("hwfq")),ne=n.n(te),oe=(n("+mLZ"),n("nqp9")),ae=n.n(oe),re=(n("rcTB"),n("pIwM")),ie=n.n(re),ue=(n("87HC"),n("fmon")),ce=n.n(ue),se=(n("SUzx"),n("cfQL")),le=n.n(se),me=(n("EknJ"),n("8qk2")),fe=n.n(me),pe=(n("vKIO"),n("GQr1")),he=n.n(pe),de=(n("RTFJ"),n("s9Cd")),ve=n.n(de),ge=(n("pIlK"),n("0Qqo")),Ie=n.n(ge),Ve=(n("8O0P"),n("rxcl")),ke=n.n(Ve),ye=(n("0nxc"),n("99iF")),xe=n.n(ye),be={name:"HtButton",props:{type:{type:String,default:"default"},size:String,icon:{type:String,default:""},loading:Boolean,disabled:Boolean}},we={render:function(){var e=this.$createElement;return(this._self._c||e)("el-button",{attrs:{type:this.type}},[this._t("default")],2)},staticRenderFns:[]},Ce=n("C7Lr")(be,we,!1,null,null,null).exports,Re={render:function(){var e=this,t=e.$createElement;return(e._self._c||t)("el-date-picker",{attrs:{type:"datetimerange","start-placeholder":"开始日期","end-placeholder":"结束日期","default-time":["12:00:00"]},model:{value:e.value6,callback:function(t){e.value6=t},expression:"value6"}})},staticRenderFns:[]};var Pe=n("C7Lr")({name:"name",props:{},data:function(){return{value6:""}},mounted:function(){},methods:{}},Re,!1,function(e){n("6+EP")},"data-v-42f2b630",null).exports,Se=n("4YfN"),_e=["Aaron","Alden"],Ae={name:"DepartItem",props:{x:Object,activeArr:Array},computed:n.n(Se)()({},Object(b.mapGetters)(["departmentId","checkValue"]),{isShowUser:function(){return this.includeId(this.activeArr)&&0==this.activeArr.indexOf(this.x.id)}}),data:function(){return{isClicked:!1,userList:[],alphabet:[],userVal:""}},mounted:function(){},created:function(){var e=this;"ABCDEFGHIJKLMNOPQRSTUVWXYZ".split("").forEach(function(t){var n=_e.filter(function(e){return e[0]===t});e.alphabet.push({initial:t,cells:n})})},watch:{userVal:function(e,t){this.userVal&&this.$store.commit("changeCheckValue",this.userVal)},checkValue:function(){this.checkValue!=this.userVal&&this.userVal&&(this.userVal=""),""==this.checkValue&&(this.userVal="")}},methods:{includeId:function(e){return e.indexOf(this.x.id)>-1},liItemClick:function(e){var t=this,n=this.activeArr.indexOf(e);n>-1?this.$store.commit("changeActiveDepartId",this.activeArr[n+1]):(this.$store.commit("changeActiveDepartId",e),this.isClicked||(this.$get("/api/userlist/"+e).then(function(e){e.userlist.forEach(function(e){e.label=e.name,e.value=e.userid}),t.userList=e.userlist}),this.isClicked=!0))}}},$e={render:function(){var e=this,t=e.$createElement,n=e._self._c||t;return n("div",[n("li",{staticClass:"li-header",on:{click:function(t){e.liItemClick(e.x.id)}}},[e.includeId(e.activeArr)?n("span",[e._v("↓")]):e._e(),e._v(" "),e.includeId(e.activeArr)?e._e():n("span",[e._v("+")]),e._v("\n    "+e._s(""+e.x.name)+"  \n  ")]),e._v(" "),[n("ul",{directives:[{name:"show",rawName:"v-show",value:e.isShowUser,expression:"isShowUser"}],staticClass:"userList"},[n("mt-radio",{staticClass:"page-part",attrs:{name:"users",align:"right",title:"部门员工",options:e.userList},model:{value:e.userVal,callback:function(t){e.userVal=t},expression:"userVal"}})],1)],e._v(" "),e.x.children?[n("ul",{staticClass:"showChildList",class:{active:e.includeId(e.activeArr)}},e._l(e.x.children,function(t,o){return n("d-item",{key:o,attrs:{x:t,activeArr:e.activeArr}})}))]:e._e()],2)},staticRenderFns:[]};var Te=n("C7Lr")(Ae,$e,!1,function(e){n("Gdeq")},"data-v-92a277d8",null).exports;Vue.component("ht-button",Ce),Vue.component("ht-date",Pe),Vue.component("d-item",Te),Vue.component(xe.a.name,xe.a),Vue.component(ke.a.name,ke.a),Vue.component(Ie.a.name,Ie.a),Vue.component(ve.a.name,ve.a),Vue.component(he.a.name,he.a),Vue.component(fe.a.name,fe.a),Vue.component(le.a.name,le.a),Vue.component(ce.a.name,ce.a),Vue.component(ie.a.name,ie.a),Vue.component(ae.a.name,ae.a),Vue.component(ne.a.name,ne.a),Vue.component(ee.a.name,ee.a),Vue.component(X.a.name,X.a),Vue.component(Z.a.name,Z.a),Vue.component(z.a.name,z.a),Vue.component(G.a.name,G.a),Vue.component(j.a.name,j.a),Vue.use(B.a),Vue.use(D.a);var Ue=n("6iV/"),qe=n.n(Ue),Ee=window.localStorage;axios.defaults.baseURL=_.a,axios.defaults.headers.common.token=Ee.getItem("shuiqitong_token")||L.state.token;axios.interceptors.response.use(function(e){if(e.status&&e.status>=200&&e.status<=300){var t=e.data;return function(e){if(!e.success&&""!=e.message){var t=e.message;"-1"==t?(Ee.setItem("shuiqitong_token",""),Ee.setItem("comUserId",""),Ee.setItem("currenComId",""),"1"!=Ee.getItem("isTokenFail")&&(Ee.setItem("isTokenFail","1"),alert("您的登陆信息已失效,请重新登陆!"),Vue.prototype.$to("/first"))):"-3"==t||"-2"==t?Vue.prototype.$to("/infoChecking/4"):"-4"==t?Vue.prototype.$to("/infoChecking/2"):"-5"==t&&Vue.prototype.$to("/infoChecking/3")}}(t),t}return e},function(e){return e.response&&(504==e.response.status||404==e.response.status||e.response.status),S.a.reject(e)});Vue.prototype.$get=function(e,t){return axios.get(e,{params:t})},Vue.prototype.$post=function(e,t){return axios({method:"POST",url:e,data:t,transformRequest:[function(e){return qe.a.stringify(e)}],headers:{"Content-Type":"application/x-www-form-urlencoded;charset=utf-8"}})},Vue.prototype.$http=axios,Vue.prototype.$ajax=axios;n("n4oI"),n("PBmY");var Le=n("I4Nl"),Ne=n.n(Le),Oe=(n("yvAY"),n("fc0N")),Fe=n.n(Oe);S.a.prototype.finally=function(e){var t=this.constructor;return this.then(function(n){t.resolve(e()).then(function(){return n})},function(n){t.resolve(e()).then(function(){throw reason})})};function De(e){return e<10?"0"+e:e}Vue.prototype.$alert=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"";return Fe.a.alert(e)},Vue.prototype.$confirm=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"",t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"提示";return Fe.a.confirm(e,t)},Vue.prototype.$prompt=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"",t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"提示";return Fe.a.prompt(e,t)},Vue.prototype.$back=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:-1;k.go(e)},Vue.prototype.$tip=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"";Ne()({message:e,duration:3e3})},Vue.prototype.$to=function(){var e=arguments.length>0&&void 0!==arguments[0]?arguments[0]:"";k.push({path:e})},Vue.prototype.$param=function(e){return e.$route.params};Vue.filter("toTime",function(e){var t,n,o,a,r,i,u,c=new Date(parseInt(e));return n=(t=c).getFullYear(),o=t.getMonth()+1,a=t.getDate(),r=t.getHours(),i=t.getMinutes(),u=t.getSeconds(),[n,o,a].map(De).join("-")+" "+[r,i,u].map(De).join(":")}),Vue.filter("toStamp",function(e){return"NaN"==Date.parse(new Date(e)).toString()?0:Date.parse(new Date(e))}),Vue.config.productionTip=!1;new a.a;new Vue({el:"#app",router:k,store:L,components:{App:i},template:"<App/>"})},NeAH:function(e,t,n){e.exports=n.p+"static/img/404.50b4e4b.png"},PBmY:function(e,t){},Phrt:function(e,t){},RTFJ:function(e,t){},RUOp:function(e,t){},SJI6:function(e,t){e.exports=Vuex},SUzx:function(e,t){},ULuq:function(e,t){},YhU3:function(e,t){},Zjav:function(e,t){},dflY:function(e,t){},kMAb:function(e,t){},lRwf:function(e,t){e.exports=Vue},miO0:function(e,t){},n4oI:function(e,t){},pIlK:function(e,t){},pRNm:function(e,t){e.exports=VueRouter},rcTB:function(e,t){},uGcx:function(e,t){},vKIO:function(e,t){},ve9D:function(e,t){},wXGk:function(e,t,n){"use strict";n.d(t,"e",function(){return o}),n.d(t,"g",function(){return a}),n.d(t,"f",function(){return r}),n.d(t,"a",function(){return i}),n.d(t,"d",function(){return u}),n.d(t,"b",function(){return c}),n.d(t,"c",function(){return s});var o="",a="",r="",i="http://dev-ws.htyfw.com.cn:18300/",u="http://dev-ws.htyfw.com.cn:18003/auth",c="wx37df32191487c6e8",s=["message","message-list","message-detail","question","question-detail","question-info"]},yvAY:function(e,t){}},["NHnr"]);