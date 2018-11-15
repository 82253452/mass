<template>
  <div>
    <div v-if="!data.data || !data.data.imgs || !data.data.imgs.length">
      <el-button slot="append" icon="el-icon-plus" @click="plus"></el-button>
    </div>
    <draggable v-model="data.data.imgs" :options="{group:'scrollImgEdit'}" @start="drag=true" @end="drag=false">
      <div v-for="(element,index) in data.data.imgs" :key="index">
        <el-input v-model="element.src" placeholder="输入图片地址" class="input-with-select">
          <template slot="prepend">链接</template>
          <el-button slot="append" icon="el-icon-minus" @click="minus(index)"></el-button>
          <el-button slot="append" icon="el-icon-plus" @click="plus"></el-button>
        </el-input>
      </div>
    </draggable>
  </div>
</template>
<script>
  import draggable from 'vuedraggable'

  export default {
    components: {
      draggable,
    },
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
          this.$emit('update', val)
        }
      }
    },
    data() {
      return {
        msg: 'hello vue',
      }
    },
    methods: {
      plus() {
        this.data.data.imgs.push({})
      },
      minus(n) {
        this.data.data.imgs.splice(n, 1)
      },
    }
  }
</script>
