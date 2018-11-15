<template>
  <div>
    <el-form ref="form" :model="data.data" label-width="120px">
      <el-form-item label="背景图片">
        <qiniuImage @successUpload="getUrl"></qiniuImage>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
  import draggable from 'vuedraggable'
  import qiniuImage from './qiniuImage'

  export default {
    components: {
      draggable,
      qiniuImage,
    },
    props: {
      data: {
        type: Object,
        default: {}
      },
    },
    data() {
      return {
        msg: 'hello vue',
      }
    },
    methods: {
      getUrl(url){
        this.$set(this.data.data,'bgImage',url)
      }
    }
  }
</script>
