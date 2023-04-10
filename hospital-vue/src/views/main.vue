<template>
	<div
		class="site-wrapper"
		:class="{ 'site-sidebar--fold': sidebarFold }"
		v-loading.fullscreen.lock="loading"
		element-loading-text="拼命加载中"
	>
		<nav class="site-navbar" :class="'site-navbar--' + navbarLayoutType">
			<div class="site-navbar__header">
				<h1 class="site-navbar__brand">
					<a class="site-navbar__brand-lg">神州医院管理系统</a>
					<a class="site-navbar__brand-mini">医院</a>
				</h1>
			</div>
			<div class="navbar-container" :class="'navbar-container--' + navbarLayoutType">
				<div class="switch" @click="handleSwitch"><SvgIcon name="zhedie" class="icon-svg" /></div>
				<div class="right-container">
					<div class="message">
						<el-badge value="0"><SvgIcon name="duanxin" class="icon-svg duanxin-svg" /></el-badge>
					</div>
					<el-dropdown>
						<span class="el-dropdown-link">{{ name }}超级管理员</span>
						<template #dropdown>
							<el-dropdown-menu>
								<el-dropdown-item @click="updatePasswordHandle()">修改密码</el-dropdown-item>
								<el-dropdown-item @click="logout">退出</el-dropdown-item>
							</el-dropdown-menu>
						</template>
					</el-dropdown>
				</div>
			</div>
			<update-password v-if="updatePasswordVisible" ref="updatePassword"></update-password>
		</nav>
		<aside class="site-sidebar site-sidebar--dark">
			<div class="site-sidebar__inner">
				<el-menu
					:default-active="menuActiveName || 'home'"
					:collapse="sidebarFold"
					:collapseTransition="false"
					class="site-sidebar__menu"
					background-color="#263238"
					active-text-color="#fff"
					text-color="#8a979e"
				>
					<el-menu-item index="Home" @click="$router.push({ name: 'Home' })">
						<SvgIcon name="home" class="icon-svg" />
						<span slot="title">首页</span>
					</el-menu-item>
					<el-sub-menu index="组织管理" :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'">
						<template #title>
							<SvgIcon name="users_fill" class="icon-svg" />
							<span slot="title">组织管理</span>
						</template>
						<el-menu-item
							index="部门管理"
							v-if="isAuth(['ROOT', 'DEPT:SELECT'])"
							@click="$router.push({ name: 'Dept' })"
						>
							<SvgIcon name="company_fill" class="icon-svg" />
							<span slot="title">部门管理</span>
						</el-menu-item>
						<el-menu-item
							index="医疗科室管理"
							v-if="isAuth(['ROOT', 'MEDICAL_DEPT:SELECT'])"
							@click="$router.push({ name: 'MedicalDept' })"
						>
							<SvgIcon name="company_fill" class="icon-svg" />
							<span slot="title">医疗科室管理</span>
						</el-menu-item>
						<el-menu-item
							index="医疗诊室管理"
							v-if="isAuth(['ROOT', 'MEDICAL_DEPT_SUB:SELECT'])"
							@click="$router.push({ name: 'MedicalDeptSub' })"
						>
							<SvgIcon name="company_fill" class="icon-svg" />
							<span slot="title">医疗诊室管理</span>
						</el-menu-item>
						<el-menu-item
							index="角色管理"
							v-if="isAuth(['ROOT', 'ROLE:SELECT'])"
							@click="$router.push({ name: 'Role' })"
						>
							<SvgIcon name="role_fill" class="icon-svg" />
							<span slot="title">角色管理</span>
						</el-menu-item>
						<el-menu-item
							index="用户管理"
							v-if="isAuth(['ROOT', 'USER:SELECT'])"
							@click="$router.push({ name: 'User' })"
						>
							<SvgIcon name="user_fill" class="icon-svg" />
							<span slot="title">用户管理</span>
						</el-menu-item>
					</el-sub-menu>
					<el-sub-menu index="医护管理" :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'">
						<template #title>
							<SvgIcon name="trust_fill" class="icon-svg" />
							<span slot="title">医护管理</span>
						</template>
						<el-menu-item
							index="医生管理"
							v-if="isAuth(['ROOT', 'DOCTOR:SELECT'])"
							@click="$router.push({ name: 'Doctor' })"
						>
							<SvgIcon name="doctor_fill" class="icon-svg" />
							<span slot="title">医生管理</span>
						</el-menu-item>
						<el-menu-item
							index="护士管理"
							v-if="isAuth(['ROOT', 'NURSE:SELECT'])"
							@click="$router.push({ name: 'Nurse' })"
						>
							<SvgIcon name="nurse_fill" class="icon-svg" />
							<span slot="title">护士管理</span>
						</el-menu-item>
						<el-menu-item
							index="护工管理"
							v-if="isAuth(['ROOT', 'NURSING_ASSISTANT:SELECT'])"
							@click="$router.push({ name: 'NursingAssistant' })"
						>
							<SvgIcon name="worker_fill" class="icon-svg" />
							<span slot="title">护工管理</span>
						</el-menu-item>
						<el-menu-item
							index="诊费设置"
							v-if="isAuth(['ROOT', 'DOCTOR_PRICE:SELECT'])"
							@click="$router.push({ name: 'DoctorPrice' })"
						>
							<SvgIcon name="money_fill" class="icon-svg" />
							<span slot="title">诊费设置</span>
						</el-menu-item>
					</el-sub-menu>
					<el-sub-menu index="出诊管理" :popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'">
						<template #title>
							<SvgIcon name="night_fill" class="icon-svg" />
							<span slot="title">出诊管理</span>
						</template>
						<el-menu-item
							index="门诊日程表"
							v-if="isAuth(['ROOT', 'SCHEDULE:SELECT'])"
							@click="$router.push({ name: 'MedicalDeptSubWorkPlan' })"
						>
							<SvgIcon name="calendar_fill" class="icon-svg" />
							<span slot="title">门诊日程表</span>
						</el-menu-item>
						<el-menu-item
							index="医生出诊表"
							v-if="isAuth(['ROOT', 'SCHEDULE:SELECT'])"
							@click="
								$router.push({
									name: 'DoctorSchedule',
									params: { deptName: 'NAN', deptSubId: 'NAN', date: 'NAN' }
								})
							"
						>
							<SvgIcon name="clock_fill" class="icon-svg" />
							<span slot="title">医生出诊表</span>
						</el-menu-item>
						<el-menu-item
							index="视频问诊"
							@click="$router.push({ name: 'VideoDiagnose' })"
							v-if="isAuth(['VIDEO_DIAGNOSE:SELECT'])"
						>
							<SvgIcon name="camera_fill" class="icon-svg" />
							<span slot="title">视频问诊</span>
						</el-menu-item>
					</el-sub-menu>
					<el-sub-menu
						index="系统设置"
						:popper-class="'site-sidebar--' + sidebarLayoutSkin + '-popper'"
						v-if="isAuth(['ROOT', 'SYSTEM:SELECT'])"
					>
						<template #title>
							<SvgIcon name="system_fill" class="icon-svg" />
							<span slot="title">系统设置</span>
						</template>
					</el-sub-menu>
				</el-menu>
			</div>
		</aside>

		<div class="site-content__wrapper" :style="{ 'min-height': documentClientHeight + 'px' }">
			<main class="site-content" :class="{ 'site-content--tabs': $route.meta.isTab }">
				<el-tabs
					v-if="$route.meta.isTab"
					v-model="mainTabsActiveName"
					:closable="true"
					@tab-click="selectedTabHandle"
					@tab-remove="removeTabHandle"
				>
					<el-tab-pane v-for="item in mainTabs" :label="item.title" :name="item.name">
						<el-card :body-style="siteContentViewHeight">
							<iframe
								v-if="item.type === 'iframe'"
								:src="item.iframeUrl"
								width="100%"
								height="100%"
								frameborder="0"
								scrolling="yes"
							></iframe>
							<router-view v-if="item.name === mainTabsActiveName" />
						</el-card>
					</el-tab-pane>
				</el-tabs>
				<el-card v-else :body-style="siteContentViewHeight"><router-view /></el-card>
			</main>
		</div>
	</div>
</template>

<script>
import SvgIcon from '../components/SvgIcon.vue';
import { isURL } from '../utils/validate';
import UpdatePassword from './update-password.vue';
import { ref, provide } from 'vue';
export default {
	components: { SvgIcon, UpdatePassword },
	data: function() {
		return {
			navbarLayoutType: '',
			sidebarFold: false,
			sidebarLayoutSkin: 'dark',
			name: '',
			photo: '',
			documentClientHeight: 0,
			siteContentViewHeight: {},
			height: null,
			mainTabs: [],
			mainTabsActiveName: 'dept',
			menuActiveName: '',
			updatePasswordVisible: false
		};
	},
	created() {
	    let that = this;
	    that.routeHandle(that.$route);
	    that.$options.sockets.onopen = function(resp) {
	        //立即发出的WebSocket消息
	        that.$socket.sendObj({ opt: 'connected', token: localStorage.getItem('token') });
	        //发送心跳检测，避免超时后服务端切断连接（必须带上Token）
	        setInterval(function() {
	            that.$socket.sendObj({ opt: 'ping', token: localStorage.getItem('token') });
	        }, 10 * 1000);
	    };
	},
	watch: {
		$route: {
			handler(to, from) {
				if (to.path != from.path) {
					// this.$router.push(to);
					this.routeHandle(to);
				}
			}
		}
	},
	methods: {
		handleSwitch: function() {
			if (this.sidebarFold) {
				this.navbarLayoutType = '';
			} else {
				this.navbarLayoutType = 'fold';
			}
			this.sidebarFold = !this.sidebarFold;
		},
		resetDocumentClientHeight: function() {
			this.documentClientHeight = document.documentElement['clientHeight'];
			window.onresize = () => {
				this.documentClientHeight = document.documentElement['clientHeight'];
				this.loadSiteContentViewHeight();
			};
		},
		loadSiteContentViewHeight: function() {
			let height = this.documentClientHeight - 50 - 30 - 2;
			if (this.$route.meta.isTab) {
				height -= 40;
				this.siteContentViewHeight = isURL(this.$route.meta.iframeUrl)
					? { height: height + 'px' }
					: { minHeight: height + 'px' };
				this.height = provide('height', { height: height - 40 + 'px' });
			}
			this.siteContentViewHeight = { minHeight: height + 'px' };
		},
		routeHandle: function(route) {
			//每次切换页面，重新计算页面高度和内容区高度
			this.resetDocumentClientHeight();
			this.loadSiteContentViewHeight();

			if (route.meta.isTab) {
				// tab选中, 不存在先添加
				var tab = this.mainTabs.filter(item => item.name === route.name)[0];
				if (!tab) {
					if (route.meta.isDynamic) {
						route = this.dynamicMenuRoutes.filter(item => item.name === route.name)[0];

						if (!route) {
							return console.error('未能找到可用标签页!');
						}
					}
					tab = {
						menuId: route.meta.menuId || route.name,
						name: route.name,
						title: route.meta.title,
						type: isURL(route.meta.iframeUrl) ? 'iframe' : 'module',
						iframeUrl: route.meta.iframeUrl || '',
						params: route.params,
						query: route.query
					};
					this.mainTabs = this.mainTabs.concat(tab);
				}
				this.menuActiveName = tab.menuId + '';
				this.mainTabsActiveName = tab.name;
			}
		},
		logout: function() {
			let that = this;
			that.$http('/mis_user/logout', 'GET', null, true, function(resp) {
				localStorage.removeItem('permissions');
				localStorage.removeItem('token');
				that.$router.push({ name: 'Login' });
			});
		},
		updatePasswordHandle: function() {
			this.updatePasswordVisible = true;
			this.$nextTick(() => {
				this.$refs.updatePassword.init();
			});
		},
		selectedTabHandle: function(tab, e) {
			tab = this.mainTabs.filter(item => item.name === tab.paneName);
			if (tab.length >= 1) {
				this.$router.push({
					name: tab[0].name,
					query: tab[0].query,
					params: tab[0].params
				});
			}
		},
		removeTabHandle: function(tabName) {
			this.mainTabs = this.mainTabs.filter(item => item.name !== tabName);
			if (this.mainTabs.length >= 1) {
				// 当前选中tab被删除
				if (tabName === this.mainTabsActiveName) {
					var tab = this.mainTabs[this.mainTabs.length - 1];
					this.$router.push({ name: tab.name, query: tab.query, params: tab.params }, () => {
						this.mainTabsActiveName = this.$route.name;
					});
				}
			} else {
				this.menuActiveName = '';
				this.$router.push({ name: 'Home' });
			}
		},
		// tabs, 关闭当前
		tabsCloseCurrentHandle: function() {
			this.removeTabHandle(this.mainTabsActiveName);
		},
		// tabs, 关闭其它
		tabsCloseOtherHandle: function() {
			this.mainTabs = this.mainTabs.filter(item => item.name === this.mainTabsActiveName);
		},
		// tabs, 关闭全部
		tabsCloseAllHandle: function() {
			this.mainTabs = [];
			this.menuActiveName = '';
			this.$router.push({ name: 'Home' });
		},
    logout: function() {
    let that = this;
    that.$http('/mis_user/logout', 'GET', null, true, function(resp) {
        localStorage.removeItem('permissions');
        localStorage.removeItem('token');
        that.$router.push({ name: 'Login' });
    });
},
messageHandle: function() {
    let that = this;

    that.$options.sockets.onmessage = function(resp) {
        console.log(resp.data);
        if (that.status != 'online') {
            return;
        }
        let data = resp.data;
        let message = data.split('#')[0];
        let param = data.split('#')[1];
        if (message == 'StartDiagnose') {
            that.currentStatus = 2;
            that.currentInfo.status = 2;
            that.countDown();
            that.roomId = param.split('&')[0];
            let videoDiagnoseId = param.split('&')[1];
            //TODO 根据挂号单ID获取患者上传的照片
            that.startDiagnose();
            ElMessage({
                message: '视频问诊开始',
                type: 'success',
                duration: 5000
            });
            let audio = new Audio('../../static/voice_1.mp3');
            //播放提示音频
            audio.play();
        } else if (message == 'EndDiagnose') {
            that.endDiagnose();
            ElMessage({
                message: '视频问诊结束',
                type: 'warning',
                duration: 5000
            });
            that.refreshInfo(false);
        } else if (message == 'RefreshDiagnose') {
            that.refreshInfo(false);
        }
    };
  
    
}


	},
	mounted: function() {
		let that = this;
		that.resetDocumentClientHeight();
		that.loadSiteContentViewHeight();
	}
};
</script>

<style lang="scss">
@import '../assets/scss/index.scss';
</style>
