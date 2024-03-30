<script setup>
import { useDark, useToggle } from '@vueuse/core'

useDark({
  selector: 'html',
  attribute: 'class',
  valueDark: 'dark',
  valueLight: 'light'
})

useDark({
  onChanged(dark) { useToggle(dark) }
})

setTimeout(() => {
  window.L2Dwidget.init({
    pluginRootPath: 'static/live2dw/live2d2',
    pluginJsPath: 'lib/',
    pluginModelPath: 'live2d-widget-model-wanko/assets/',
    tagMode: false,
    debug: false,
    model: { jsonPath: 'src/static/live2dw/live2dw/live2d-widget-model-wanko/assets/wanko.model.json' },
    display: { position: 'right', width: 150, height: 300 },
    mobile: { show: true },
    log: false,
    touch: true
  })
}, 1000)

</script>

<template>
  <header>
    <div class="wrapper">
      <router-view/>
    </div>
  </header>
</template>

<style scoped>
header {
  line-height: 1.5;
}
</style>
