import './assets/main.scss'
import router from './router'
import { createApp } from 'vue'
import ElementPlus from 'element-plus'
import App from './App.vue'
import { createPinia } from 'pinia'
import { createPersistedState } from 'pinia-persistedstate-plugin'
import locale from 'element-plus/dist/locale/zh-cn'
const app = createApp(App)
const pinia = createPinia()
const persist = createPersistedState()
app.use(pinia)
pinia.use(persist)
app.use(ElementPlus,{locale})
app.use(router)
app.mount('#app')
    