<template>
	<view>
		<view class="top-container" v-if="flag">
			<image :src="img.logo" mode="widthFix" class="logo"></image>
			<text class="title">北京市神州互联网医院</text>
			<view class="qrcode-container">
				<view class="qrcode"><uqrcode ref="qrcode" canvas-id="qrcode" size="140" :value="outTradeNo"></uqrcode></view>
				<text>就诊请出示二维码</text>
			</view>
			<view class="step-container">
				<view class="step">
					<view class="icon-1"></view>
					<text>在门诊台取号</text>
				</view>
				<view class="line"></view>
				<view class="step">
					<view class="icon-2"></view>
					<text>安静排队候诊</text>
				</view>
				<view class="line"></view>
				<view class="step">
					<view class="icon-3"></view>
					<text>就诊出示二维码</text>
				</view>
			</view>
		</view>
		<view class="registration-container">
			<view class="title"><text>挂号信息</text></view>
			<view class="registration">
				<u-cell-group :border="false">
					<u-cell title="就诊科室" :border="false" :value="subDeptName"></u-cell>
					<u-cell title="就诊医生" :border="false" :value="doctorName"></u-cell>
					<u-cell title="挂号类型" :border="false" :value="type"></u-cell>
					<u-cell title="门诊位置" :border="false" :value="location"></u-cell>
					<u-cell title="就诊时间" :border="false" :value="datetime"></u-cell>
					<u-cell title="挂号人" :border="false" :value="patientName"></u-cell>
					<u-cell title="挂号费" :border="false" :value="amount"></u-cell>
           <u-cell v-if="prescriptionId != null" title="电子处方" :border="false" isLink :url="`/registration/prescription/prescription?registrationId=${id}`"></u-cell>
				</u-cell-group>
			</view>
		</view>
	</view>
</template>

<script>
let dayjs = require('dayjs');

export default {
	data() {
		return {
			img: {
				logo: `${this.patientUrl}/page/registration/registration_info/logo.png`
			},
			id: null,
			outTradeNo: null,
			patientName: '',
			subDeptName: '',
			doctorName: '',
			location: '',
			date: '',
			slot: '',
			job: '',
			type: '',
			paymentStatus: null,
			datetime: '',
			amount: '',
			json: {
				'1': '08:00',
				'2': '08:30',
				'3': '09:00',
				'4': '09:30',
				'5': '10:00',
				'6': '10:30',
				'7': '11:00',
				'8': '11:30',
				'9': '13:00',
				'10': '13:30',
				'11': '14:00',
				'12': '14:30',
				'13': '15:00',
				'14': '15:30',
				'15': '16:00'
			},
			flag: false,
      prescriptionId: null
		};
	},
	methods: {},
	onLoad: function(options) {
	    let that = this;
	    let id = options.id;
	    that.id = id;
	    let data = {
	        id: id
	    };
	    that.ajax(
	        that.api.searchRegistrationInfo,
	        'POST',
	        data,
	        function(resp) {
	            let data = resp.data;
	            that.outTradeNo = data.outTradeNo;
	            that.patientName = data.patientName;
	            that.subDeptName = data.subDeptName;
	            that.doctorName = data.doctorName;
	            that.location = data.location;
	            that.job = data.job;
	            if (['主任医师', '副主任医师'].includes(that.job)) {
	                that.type = '专家号';
	            } else {
	                that.type = '普通号';
	            }
	            that.date = data.date;
	            that.slot = data.slot;
	            that.datetime = data.date + ' ' + that.json[data.slot];
	            that.amount = data.amount + ' 元';
	            that.paymentStatus = data.paymentStatus;
	            let today = dayjs().format('YYYY-MM-DD');
	            if (today == that.date && that.paymentStatus == 2) {
	                that.flag = true;
	            } else {
	                that.flag = false;
	            }
              if (data.hasOwnProperty('prescriptionId')) {
                              that.prescriptionId = data.prescriptionId;
                          }
	        },
	        false
	    );
	},

	onUnload: function() {
	    let pages = getCurrentPages();
	    let prevPage = pages[pages.length - 2];
	    prevPage.$vm['reload'] = false;
	}

};
</script>

<style lang="less">
@import url('registration_info.less');
</style>
