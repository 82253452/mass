<template>
  <div class="main anquanqi"/>
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
  computed: {}
}
</script>
<style>
  .main {
    height: 100%;
    width: 100%;
    background: #fff;
    text-align: center;
    border: 1px dashed #bfcbd9;
  }
  .anquanqi{
    background: url("../../assets/images/anquanqi.jpg")  no-repeat !important;
    background-size:100% !important;
  }
</style>
