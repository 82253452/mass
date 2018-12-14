<template>
  <div class="main">
    <h1 style="margin-top:40%;">黄历</h1>
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
      temp: ''
    }
  },
  computed: {

  }
}
</script>
<style>
  .main {
    height: 100%;
    width: 100%;
    background: #fff;
    text-align: center;
    border: 1px dashed #bfcbd9
  }
</style>
