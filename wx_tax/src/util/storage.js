
/**
 * NOTE  现有项目为wx_tax和wx_web 两者之间共用localStorage会存在很大的缓存问题
 *       为了解决此问题,在本项目中,统一封装localStorage,key前边添加`wx_tax`的签名,以防冲突
 *       2018年12月3日16:09:19 mashaobo 新增
 */

/**
 * NOTE 现在项目中全局引用 localStorage 应通过mystorage 进行读取和存储
 */
window.mystorage = (function mystorage() {
  var storage = window.localStorage;
  if (!storage) {
    return false;
  }

  var set = function (key, value) {
    //存储
    storage.setItem(`wx_tax_${key}`, value);
  };

  var get = function (key) {
    //读取
    var mydata = storage.getItem(`wx_tax_${key}`);
    return mydata;
  };

  var clear = function(key){
      //清除对象
      storage.removeItem(`wx_tax_${key}`);
  };
  return {
    setItem: set,
    getItem: get,
    clear
  };

})();