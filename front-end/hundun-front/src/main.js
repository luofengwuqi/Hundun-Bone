import { createApp } from 'vue'
import { createPinia } from 'pinia'
import ElementPlus from 'element-plus'

import App from './App.vue'
import router from './router'
//引入全局css
import '@/assets/css/base.css'
import 'element-plus/dist/index.css'

const app = createApp(App)


app.use(createPinia())
app.use(router)
app.use(ElementPlus)

app.mount('#app')
