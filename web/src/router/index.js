import Vue from 'vue'
import VueRouter from 'vue-router'
import Login from '../views/Login.vue'
import Home from '../views/Home.vue'

Vue.use(VueRouter);

export const constantRouterMap = [
    { path: '/', component: Login},
    { path: '/home', component: Home},

]

export default new VueRouter({
    // mode: 'history',
    scrollBehavior: () => ({ y: 0 }),
    routes: constantRouterMap 
})
