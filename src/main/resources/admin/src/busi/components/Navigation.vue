<template>
  <div :style="bgStyle">
    <div style="margin-top:40%" v-for="(obj,index) in list" :key="index">
      <div style="float: left;
      margin-left: 9%;
      width: 37%;
      background: #000000;
      opacity: 0.3;
      color: #ffffff;
      text-align: center;
      margin-top: 20px;
      padding: 20px;
      font-size: 0.8rem;">{{obj}}</div>
    </div>
  </div>
</template>
<script>
  export default {
    props: {
      data: {
        type: Object,
        default: {}
      },
    },
    computed: {
      bgStyle() {
        if (this.cData.bgImage) {
          return {
            background: 'url(' + this.cData.bgImage + ') no-repeat',
            backgroundSize: '100% 100%',
            height: '100%',
            width:'100%'
          }
        }
        return {
          height: '100%',
          width:'100%'
        }
      }
    },
    watch: {
      data: {
        deep: true,
        handler(val) {
          this.cData = val
        }
      }
    },
    data() {
      return {
        cData: this.data,
        list: ['产品介绍','案例展示','新闻中心','欢迎加入','客服电话','关于我们']
      }
    }
  }
</script>
