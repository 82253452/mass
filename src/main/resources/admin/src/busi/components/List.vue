<template>
  <div class="main">
    <div v-for="element in 8" :key="element" style="float: left;width: 32%">
      <el-card style="height: 80px;margin:3%;" :body-style="{ padding: '0px' }">
        <div style="text-align: center">
          <i class="el-icon-picture" style="width: 40px;height: 40px;padding-top:20px;"></i>
          <div style="padding-top: 10px;">
            <span>标题</span>
          </div>
        </div>
      </el-card>
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
    watch: {},
    data() {
      return {
        msg: 'hello vue'
      }
    }
  }
</script>
<style>
  .main {
    width: 100%;
    text-align: center;
    padding-left: 5%;;
    border: 1px dashed #bfcbd9
  }
</style>
