<template>
  <div>
    <el-form ref="form" :model="data.data" label-width="120px">
      <el-form-item label="页面名称：">
        <el-input v-model="data.pageName"/>
      </el-form-item>
      <el-form-item label="分享标题：">
        <el-input v-model="data.shareTitle"/>
      </el-form-item>
      <el-form-item label="标题栏文字：">
        <el-input v-model="data.pageTitle"/>
      </el-form-item>
      <el-form-item label="导航图标：">
        <i class="iconfont icon-homepage" style="font-size:30px;" @click="selectIcon('homepage')"/>
        <i class="iconfont icon-integral" style="font-size:30px;" @click="selectIcon('integral')"/>
        <i class="iconfont icon-label" style="font-size:30px;" @click="selectIcon('label')"/>
        <i class="iconfont icon-message" style="font-size:30px;" @click="selectIcon('message')"/>
        <i class="iconfont icon-setup" style="font-size:30px;" @click="selectIcon('setup')"/>
        <i class="iconfont icon-search" style="font-size:30px;" @click="selectIcon('search')"/>
        <i class="iconfont icon-manage" style="font-size:30px;" @click="selectIcon('manage')"/>
        <i class="iconfont icon-people" style="font-size:30px;" @click="selectIcon('people')"/>
        <i class="iconfont icon-more" style="font-size:30px;" @click="selectIcon('more')"/>
        <i class="iconfont icon-group" style="font-size:30px;" @click="selectIcon('group')"/>
      </el-form-item>
    </el-form>
  </div>
</template>
<script>
export default {
  props: {
    data: {
      type: Object,
      default: {}
    }
  },
  data() {
    return {
      msg: 'hello vue'
    }
  },
  methods: {
    selectIcon(name) {
      this.$set(this.data, 'iconName', name)
      // this.data.iconName = name
    }
  }
}
</script>
