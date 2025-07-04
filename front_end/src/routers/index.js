import { createRouter, createWebHistory } from "vue-router";
import Login from "../components/Login.vue";
import Home from "../components/Home.vue";
import Register from "../components/Register.vue";
import { getToken } from '../utils/token-utils';
import CourseList from "../components/CourseList.vue";
import MeetingManage from "../components/MeetingManage.vue"
import MeetingDetail from "../components/MeetingDetail.vue";
const routes = [
  { path: '/register', component: Register, name: "Register" },
  { path: '/home', component: Home, name: "Home", redirect:'/courseList' },
  { path: '/login', component: Login, name: "Login" },
  { path: '/courseList', component: CourseList, name: "CourseList" },
  { path: '/', redirect: '/login' },
  { path: '/meetingManage', component: MeetingManage, name: "MeetingManage"},
  {
  path: '/meetingDetail/:id',
  name: 'MeetingDetail',
  component: MeetingDetail  ,
  meta: { title: '会议详情' }
},
];

const router = createRouter({
  history: createWebHistory(),
  routes,
});

// 路由守卫
router.beforeEach((to, from, next) => {
  const token = getToken()
  if (!token && to.path !== '/login' && to.path !== '/register') {
    next({ path: '/login' })
  } else {
    next()
  }
})

export default router;
