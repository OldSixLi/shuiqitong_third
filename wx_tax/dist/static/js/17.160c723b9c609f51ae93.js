webpackJsonp([17],{Yoru:function(e,t,o){"use strict";Object.defineProperty(t,"__esModule",{value:!0});o("miO0");var a=o("OTYB"),c=o.n(a),n={name:"Auth",data:function(){return{code:"",state:"",data:{}}},mounted:function(){c.a.open()},methods:{getInfo:function(){var e=this;try{var t=this.code;this.$post("/tax/login/login",{code:t}).then(function(t){if(e.data=t,t.success){c.a.close();try{var o=t.bean.flag||"";switch(o){case"0":alert("请求错误:访问微信接口失败");break;case"1":alert("请求错误:您不是该企业的成员");break;case"2":alert("请求错误:您还未添加企业");break;case"3":e.$store.commit("changeCheckState",4);break;case"4":e.$store.commit("changeCheckState",0);break;case"5":e.$store.commit("changeCheckState",1);break;case"6":e.$store.commit("changeCheckState",2,t.bean.reason);break;default:console.log("访问微信接口失败，错误代号:"+o)}o>=3&&(e.$store.commit("setwxUserInfo",t.bean),e.$store.commit("setComUserId",t.bean.userId),e.$store.commit("setComId",t.bean.corpId),e.$store.commit("changeToken",t.bean.token),e.state?e.$to(unescape(e.state)):e.$to("/"))}catch(e){console.log(e)}}else c.a.close(),e.$alert("获取企业信息失败，请重试!")},function(e){console.log(e)}).catch(function(e){console.log(e)})}catch(e){console.log(e)}},urlSearch:function(){for(var e,t,o=location.href,a=o.indexOf("?"),c={},n=(o=o.substr(a+1)).split("&"),s=0;s<n.length;s++)(a=n[s].indexOf("="))>0&&(e=n[s].substring(0,a),t=n[s].substr(a+1),c[e]=t);return c}},beforeRouteEnter:function(e,t,o){o(function(e){try{var t=e.urlSearch();e.code=t.code||"",e.state=t.state||"",e.code&&e.getInfo()}catch(e){}})}},s={render:function(){var e=this.$createElement;return(this._self._c||e)("div",{staticClass:"router-block"})},staticRenderFns:[]},r=o("C7Lr")(n,s,!1,null,null,null);t.default=r.exports}});