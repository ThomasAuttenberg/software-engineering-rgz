

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import vClickOutside from '@/shared/directives/clickOutside'

import App from './App.vue'
import router from './router'

const app = createApp(App)

app.use(createPinia())
app.directive('click-outside', vClickOutside)
app.use(router)

app.mount('#app')
