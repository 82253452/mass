<template>
  <div>
    <el-form ref="form" :model="data.data" label-width="120px">
      <el-form-item label="页面名称：">
        <el-input v-model="data.pageName"></el-input>
      </el-form-item>
      <el-form-item label="分享标题：">
        <el-input v-model="data.shareTitle"></el-input>
      </el-form-item>
      <el-form-item label="标题栏文字：">
        <el-input v-model="data.pageTitle"></el-input>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
    export default{
      props: {
        data: {
          type: Object,
          default: {}
        },
      },
        data(){
            return{
                msg:'hello vue'
            }
        },
    }
</script>
