<template>
	<view class="page">
		<u-swiper height="230" interval="10000" :list="list1" easingFunction="linear" radius="0" indicator></u-swiper>
		<view class="top-panel">
			<image :src="img['icon-1']" mode="widthFix" class="icon" @tap="toPage('/registration/notice/notice')" />
			<view class="info">
				<view class="row" @tap="toPage('/registration/notice/notice')">
					<text class="title">预约挂号</text>
					<view class="tag">免排队</view>
					<view class="tag">免挂号单</view>
				</view>
				<view class="row"><view class="link">疫情期间到院就诊指南？</view></view>
			</view>
		</view>
		<view class="navigator-container">
			<view>
				<u-grid :border="false" col="4">
					<u-grid-item>
						<button open-type="contact" session-from="sessionFrom" plain="true" class="btn">
							<view class="navigator-icon-1"></view>
							<text class="title">导诊问询</text>
							<text class="desc">回答患者咨询</text>
						</button>
					</u-grid-item>
					<u-grid-item>
						<view class="navigator-icon-2"></view>
						<text class="title">找医生</text>
						<text class="desc">查找医院科室</text>
					</u-grid-item>
					<u-grid-item @click="toPage('/registration/notice/notice')">
						<view class="navigator-icon-3"></view>
						<text class="title">线上挂号</text>
						<text class="desc">在线预约挂号</text>
					</u-grid-item>
					<u-grid-item>
						<view class="navigator-icon-4"></view>
						<text class="title">电子处方</text>
						<text class="desc">药物治疗信息</text>
					</u-grid-item>
				</u-grid>
			</view>
		</view>
		<view class="department-container">
			<u-grid :border="false" @click="click" col="5">
				<u-grid-item>
					<view class="department-icon-1" />
					<text class="title">口腔科</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-2" />
					<text class="title">眼科</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-3" />
					<text class="title">耳鼻喉</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-4" />
					<text class="title">内科</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-5" />
					<text class="title">外科</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-6" />
					<text class="title">皮肤科</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-7" />
					<text class="title">妇科</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-8" />
					<text class="title">儿科</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-9" />
					<text class="title">神经科</text>
				</u-grid-item>
				<u-grid-item>
					<view class="department-icon-10" />
					<text class="title">更多</text>
				</u-grid-item>
			</u-grid>
			<image :src="otherBannerUrl" mode="widthFix" class="banner"></image>
		</view>
		<view class="publicity-container"><image :src="publicityBannerUrl" mode="widthFix" class="banner" /></view>
		<view class="doctor-container">
			<view class="title-row">
				<text class="title">名医专家</text>
				<u-icon label="更多" labelPos="left" size="15" name="arrow-right"></u-icon>
			</view>
			<u-tabs :list="tab.list" @click="clickTabHandle"></u-tabs>
			<view class="panel">
				<view class="doctor" v-for="one in doctor">
					<u-avatar :src="one.photo" size="45"></u-avatar>
					<view class="info">
						<view class="row">
							<text class="name">{{ one.name }}</text>
							<text class="job">{{ one.job }}</text>
							<button class="btn">挂号</button>
						</view>
						<view class="row">
							<text class="remark">{{ one.remark }}</text>
						</view>
						<view class="row">
							<rich-text class="desc">{{ one.description }}</rich-text>
						</view>
					</view>
				</view>
			</view>
		</view>
		<view class="ad-container"><image :src="adBannerUrl" mode="widthFix" class="banner" /></view>
		<view class="rescue-container">
			<view class="title-row">
				<text class="title">必备急救宝典</text>
				<u-icon label="更多" labelPos="left" size="15" name="arrow-right"></u-icon>
			</view>
			<u-grid :border="false" @click="click" col="3">
				<u-grid-item><image :src="rescue[0]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[1]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[2]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[3]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[4]" mode="widthFix" class="pic"></image></u-grid-item>
				<u-grid-item><image :src="rescue[5]" mode="widthFix" class="pic"></image></u-grid-item>
			</u-grid>
		</view>
	</view>
</template>

<script>
  
export default {
	data() {
		return {
			img: {
				'icon-1': `${this.patientUrl}/page/index/icon-1.png`
			},
			list1: [
				`${this.patientUrl}/swiper/swiper-1.jpg`,
				`${this.patientUrl}/swiper/swiper-2.jpg`,
				`${this.patientUrl}/swiper/swiper-3.jpg`,
				`${this.patientUrl}/swiper/swiper-4.jpg`
			],
			otherBannerUrl: `${this.patientUrl}/banner/banner-1.jpg`,
			publicityBannerUrl: `${this.patientUrl}/banner/banner-2.jpg`,
			adBannerUrl: `${this.patientUrl}/banner/banner-7.jpg`,
			tab: {
				list: [],
				current: 0,
				swipperStyle: ''
			},
			doctor: [],
			rescue: [
				`${this.patientUrl}/rescue/rescue-1.jpg`,
				`${this.patientUrl}/rescue/rescue-2.jpg`,
				`${this.patientUrl}/rescue/rescue-3.jpg`,
				`${this.patientUrl}/rescue/rescue-4.jpg`,
				`${this.patientUrl}/rescue/rescue-5.jpg`,
				`${this.patientUrl}/rescue/rescue-6.jpg`
			]
		};
	},
	onLoad() {

	},
	methods: {
		
	}
};
</script>

<style lang="less">
@import url(index.less);
</style>
