<template>
	<view class="main">
		<view class="hospital">北京市神州网络医院</view>
		<view class="title">电子处方笺</view>
		<view class="qrcode">
			<uqrcode ref="qrcode" canvas-id="qrcode" size="140" :value="uuid"></uqrcode>
		</view>
		<view class="info-container">
			<view class="row">
				<text>姓名：{{ patient.name }}</text>
				<text>性别：{{ patient.sex }}</text>
				<text>年龄：{{ patient.age }}岁</text>
			</view>
			<view class="row">
				<text>诊室：{{ deptSub }}</text>
			</view>
			<view class="row">
				<text>临床诊断：{{ diagnosis }}</text>
			</view>
		</view>
		<view class="rp-container">
			<view class="rp-title">Rp:</view>
			<view class="rp" v-for="one of rpList" :key="one">
				<view class="row">
					<text class="name">1. {{ one.name }}</text>
					<text class="num">×{{ one.num }}</text>
				</view>
				<view class="row">
					<text class="spec">{{ one.spec }}</text>
				</view>
				<view class="row">
					<text class="method">{{ one.method }}</text>
				</view>
			</view>
		</view>
		<view class="responsible-person">
			<view>
				<text class="person">处方医师：{{ doctor }}</text>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			uuid: '',
			patient: {
				name: null,
				sex: null,
				age: null
			},
			deptSub: null,
			diagnosis: null,
			rpList: [],
			doctor: null
		};
	},
	methods: {},
	onLoad(options) {
	    let that = this;
	    let registrationId = options.registrationId;
	    let data = {
	        registrationId: registrationId
	    };
	    that.ajax(that.api.searchPrescriptionByRegistrationId, 'POST', data, function(resp) {
	        let result = resp.data.result;
	        that.uuid = result.uuid;
	        that.patient.name = result.patientName;
	        that.patient.sex = result.patientSex;
	        that.patient.age = result.patientAge;
	        that.deptSub = result.deptSub;
	        that.diagnosis = result.diagnosis;
	        that.rpList = result.rp;
	        that.doctor = result.doctorName;
	    });
	}

};
</script>

<style lang="less">
@import url('prescription.less');
</style>
