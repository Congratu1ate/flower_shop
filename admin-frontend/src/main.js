import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import zhCn from 'element-plus/dist/locale/zh-cn.mjs'
import 'element-plus/dist/index.css'
import * as Icons from '@element-plus/icons-vue'
import router from './router'

const app = createApp(App)
app.use(ElementPlus, { locale: zhCn })
Object.entries(Icons).forEach(([name, comp]) => app.component(name, comp))
app.use(router)
app.mount('#app')
