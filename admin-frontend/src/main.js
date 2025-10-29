import { createApp } from 'vue'
import './style.css'
import App from './App.vue'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as Icons from '@element-plus/icons-vue'
import router from './router'

const app = createApp(App)
app.use(ElementPlus)
Object.entries(Icons).forEach(([name, comp]) => app.component(name, comp))
app.use(router)
app.mount('#app')
