import {
	createRouter,
	createWebHashHistory
}
from 'vue-router'
import Login from '../views/login.vue'
import Main from "../views/main.vue"
import Home from "../views/home.vue"
import Role from "../views/role.vue"
import User from "../views/user.vue"
import Dept from "../views/dept.vue"

import MedicalDept from "../views/medical_dept.vue"
import MedicalDeptSub from "../views/medical_dept_sub.vue"
import MedicalDeptSubWorkPlan from "../views/medical_dept_sub_work_plan.vue"
import Doctor from "../views/doctor.vue"
import DoctorPrice from "../views/doctor_price.vue"
import DoctorSchedule from "../views/doctor_schedule.vue"
import VideoDiagnose from "../views/video_diagnose.vue"
import NotFound from "../views/404.vue"



const routes = [{
		path: '/login',
		name: 'Login',
		component: Login
	},
	{
		path: '/',
		name: 'Main',
		component: Main,
		children: [{
				path: '/home',
				name: 'Home',
				component: Home,
				meta: {
					title: '首页',
				}
			},
			{
				path: "/role",
				name: "Role",
				component: Role,
				meta: {
					title: "角色管理",
					isTab: true
				}
			},
			{
				path: '/user',
				name: 'User',
				component: User,
				meta: {
					title: '用户管理',
					isTab: true
				}
			},
			{
				path: '/dept',
				name: 'Dept',
				component: Dept,
				meta: {
					title: '部门管理',
					isTab: true
				}
			},
			{
				path: '/medical_dept',
				name: 'MedicalDept',
				component: MedicalDept,
				meta: {
					title: '医疗科室管理',
					isTab: true
				}
			},
			{
				path: '/medical_dept_sub',
				name: 'MedicalDeptSub',
				component: MedicalDeptSub,
				meta: {
					title: '医疗诊室管理',
					isTab: true
				}
			},
			{
				path: '/doctor',
				name: 'Doctor',
				component: Doctor,
				meta: {
					title: '医生管理',
					isTab: true
				}
			},
			{
				path: '/doctor_price',
				name: 'DoctorPrice',
				component: DoctorPrice,
				meta: {
					title: '诊费设置',
					isTab: true
				}
			},
			{
				path: '/medical_dept_sub_work_plan',
				name: 'MedicalDeptSubWorkPlan',
				component: MedicalDeptSubWorkPlan,
				meta: {
					title: '门诊日程表',
					isTab: true
				}
			},

			{
				path: '/doctor_schedule/:deptName/:deptSubId/:date',
				name: 'DoctorSchedule',
				component: DoctorSchedule,
				meta: {
					title: '医生出诊表',
					isTab: true
				}
			},
			{
				path: '/video_diagnose',
				name: 'VideoDiagnose',
				component: VideoDiagnose,
				meta: {
					title: '视频问诊',
					isTab: true
				}
			},


		]
	},
	{
		path: "/404",
		name: "NotFound",
		component: NotFound
	},
	{
		path: '/:pathMatch(.*)*',
		redirect: "/404"
	}
]

const router = createRouter({
	history: createWebHashHistory(),
	routes
})
router.beforeEach((to, from, next) => {
	if (to.name != "Login") {
		let permissions = localStorage.getItem("permissions")
		let token = localStorage.getItem("token")
		if (permissions == null || permissions == "" || token == null || token == "") {
			next({
				name: 'Login'
			})
		}
	}
	return next()
})

export default router
