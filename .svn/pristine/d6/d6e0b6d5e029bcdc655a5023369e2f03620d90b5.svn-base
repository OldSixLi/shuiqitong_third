<template>
  <div 
  class="icon-block text-center" 
  :class="{'is-circle':circle,'icon-disabled':disable}" 
  :style="{ width: width,height:height}"
  @click="handleClick" 
  :title="word">
    <img :src="src" :alt="word" v-if="src&&!loading" >
    <i class="el-icon-loading" v-if="loading"></i>
  </div>
</template>
<script>
  export default {
    name: "IconBtn",
    props: {
      //对外获取的数据
      circle: Boolean,
      src: String,
      width: String,
      height: String,
      word: String,
      disable: {
        type:Boolean,
        default:false
      },
      loading:{
        type:Boolean,
        default:false
      }
    },
    data: function () {
      //组件内数据部分
      return {}
    },
    mounted: function () {
      //组件生成时调用
    },
    methods: {
      handleClick(e) {
        if(!this.disable&&!this.loading){
          this.$emit("clicks",e);
        }
        
      }
    }
  }

</script>
<style scoped>
  .icon-block {
    width: 35px;
    height: 35px;
    display: inline-block;
    line-height: 31px;
    cursor: pointer;
    background: #fff;
    border: 1px solid #ddd;
    white-space: nowrap;
    border-radius: 4px;
    box-sizing: border-box;
    vertical-align: middle;

  }

  .icon-block img {
    width: 60%;
    margin-left: 22%;margin-top: 20%;margin-right: 20%;
  }

  .icon-block+.icon-block {
    margin-left: 10px;
  }

  .icon-block:hover {
    background-color: #ecf5ff;
  }

  .icon-block.is-circle {
    border-radius: 50%;
  }
  .icon-block.icon-disabled:hover {
    background-color: #fff;
  }

  .icon-block.icon-disabled img {
    filter: drop-shadow(0 0 0 gray);
    cursor: not-allowed;
    opacity: 0.3;
  }

  .el-icon-loading{
    font-size:20px;color:#9dbee0;vertical-align: middle;
  }

</style>
