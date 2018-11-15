<template>
  <div>
    <el-form ref="form" :model="data.data" label-width="120px">
      <el-form-item label="每行数量：">
        <el-input v-model="data.data.num"></el-input>
      </el-form-item>
    </el-form>
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
    data() {
      return {
        msg: 'hello vue',
      }
    },
    methods: {}
  }
</script>
