<!-- 页面描述:带图标的文字-->
<!-- mashaobo2018年12月1日11:14:35 新增 -->
<template>
  <div class="icon-area">
      <img :src="src" class="time-icon">
      <span  class="icon-text">
          {{word}}
        <slot></slot></span>
  </div>
</template>
<script>
  export default {
    name: "IconWord",
    props: {
      src: {
        type: String,
        default: require('@/assets/img/no-result.png')
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
  .icon-text{
    color: #ab7f7f;
  }
  .icon-area{
    display: inline-block;
    position: relative;
  }
  .icon-area+.icon-area{
    margin-left: 2rem;
  }
  .time-icon{
    margin-right: 0;
  }
</style>
