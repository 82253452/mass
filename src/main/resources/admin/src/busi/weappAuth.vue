<template>
  <div class="app-container">
    <a :href="authUrl">点我授权</a>
  </div>
</template>

<script>
import {
  getAuthUrl
} from '@/api/busiApp'

export default {
  name: 'WeappAuth',
  data() {
    return {
      authUrl: {}
    }
  },
  created() {
    this.getAuthUrlInit()
  },
  methods: {
    getAuthUrlInit() {
      getAuthUrl().then(resp => {
        this.authUrl = resp.data.url
      })
    }
  }
}
</script>
