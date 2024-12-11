import { createRouter, createWebHistory } from 'vue-router'
import HomeView from '../views/HomeView.vue'
import { useTableStore } from '@/shared/stores/currentTable'

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: '/',
      name: 'home',
      component: HomeView,
    },
    {
      path: '/table/:name',
      name: 'about',
      beforeEnter: (to)=>{
        useTableStore().setCurrentTable(to.params.name as string);
      },
      // route level code-splitting
      // this generates a separate chunk (About.[hash].js) for this route
      // which is lazy-loaded when the route is visited.
      component: () => import('../views/TableView.vue'),
    },
  ],
})

export default router
