<template>
  <div class="upload-container">
    <el-upload
      :data="dataObj"
      :multiple="false"
      :show-file-list="false"
      :before-upload="beforeUpload"
      :headers="headers"
      :on-success="handleImageScucess"
      class="image-uploader"
      drag
      action="//up-z1.qiniu.com/">
      <div :style="bgStyle">
        <i class="el-icon-upload"/>
        <div class="el-upload__text">将文件拖到此处，或<em>点击上传</em></div>
      </div>
    </el-upload>
  </div>
</template>

<script>
// 预览效果见付费文章
import { getToken } from '@/api/qiniu'

export default {
  name: 'QiniuImage',
  props: {
    value: {
      type: String,
      default: ''
    },
    domain: {
      type: String,
      default: 'http://images.fast4ward.cn/'
    }
  },
  data() {
    return {
      dataObj: { token: '' },
      headers: {},
      url: ''
    }
  },
  computed: {
    bgStyle() {
      if (this.value) {
        return {
          background: 'url(' + this.value + ') no-repeat',
          backgroundSize: '100% 100%',
          height: '100%'
        }
      }
      return {
        height: '100%'
      }
    }
  },
  methods: {
    emitInput() {
      this.$emit('input', this.url)
    },
    handleImageScucess(data) {
      this.url = this.domain + data.key
      this.emitInput()
    },
    beforeUpload() {
      const _self = this
      return new Promise((resolve, reject) => {
        getToken().then(data => {
          const token = data.uptoken
          _self.dataObj.token = token
          _self.headers = {
            'Authorization': 'UpToken ' + token
          }
          resolve(true)
        }).catch(err => {
          reject(false)
        })
      })
    }
  }
}
</script>

<style rel="stylesheet/scss" lang="scss" scoped>
  @import "src/styles/mixin.scss";

  .upload-container {
    width: 100%;
    position: relative;
  @include clearfix;

  .image-uploader {
    width: 60%;
    float: left;
  }

  .image-preview {
    width: 200px;
    height: 200px;
    position: relative;
    border: 1px dashed #d9d9d9;
    float: left;
    margin-left: 50px;

  .image-preview-wrapper {
    position: relative;
    width: 100%;
    height: 100%;

  img {
    width: 100%;
    height: 100%;
  }

  }
  .image-preview-action {
    position: absolute;
    width: 100%;
    height: 100%;
    left: 0;
    top: 0;
    cursor: default;
    text-align: center;
    color: #fff;
    opacity: 0;
    font-size: 20px;
    background-color: rgba(0, 0, 0, .5);
    transition: opacity .3s;
    cursor: pointer;
    text-align: center;
    line-height: 200px;

  .el-icon-delete {
    font-size: 36px;
  }

  }
  &
  :hover {

  .image-preview-action {
    opacity: 1;
  }

  }
  }
  }
</style>
