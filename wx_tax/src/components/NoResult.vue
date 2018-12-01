<!-- 页面描述:无结果组件 -->
<!-- mashaobo 2018年12月1日10:51:24 新增 -->
<template>
  <div>
    <div class="text-center">
      <img :src="src">
      <h4 style="color:#696464;margin:0;" v-if="title">{{title}}</h4>
      <p v-if="word" >word</p>
    </div>
  </div>
</template>
<script>
  export default {
    name: "noResult",
    props: {
     
      src: {
        type: String,
        default: require('@/assets/img/no-result.png')
      },
      title: {
        type: String,
        default:"未搜索到结果"
      },
      word:String
    },
    methods: {

    },
    watch: {

    }
  }
</script>
<style scoped>
  
</style>
