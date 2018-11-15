<template>
  <el-carousel height="150px" indicator-position="outside" style="width: 100%">
    <el-carousel-item v-for="(item,index) in cData.imgs" :key="index">
      <img :src="item.src" style="width:100%;height: 150px"/>
    </el-carousel-item>
  </el-carousel>
</template>
<script>
  export default {
    props: {
      data: {
        type: Object,
        default: {}
      },
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
        msg: 'hello vue'
      }
    }
  }
</script>
