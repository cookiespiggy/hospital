<template>
	<view>
		<view class="header-container">
			<view class="dept_sub">
				<text @click="showDeptSubPickerHandler">{{ deptSub }}</text>
				<u-icon name="arrow-down-fill" color="#b5b5b5" size="14"></u-icon>
			</view>
			<view class="job">
				<text @click="showJobPickerHandler">{{ job }}</text>
				<u-icon name="arrow-down-fill" color="#b5b5b5" size="14"></u-icon>
			</view>
		</view>
		<view class="doctor-container">
			<view class="doctor" v-for="one in doctor">
				<u-avatar :src="one.photo" size="45"></u-avatar>
				<view class="info">
					<view class="row">
						<text class="name">{{ one.name }}</text>
						<text class="job">{{ one.job }}</text>
						<button class="btn" @tap="createVideoDiagnoseHandle(one.doctorId)">问诊</button>
					</view>
					<view class="row">
						<text class="remark">{{ one.remark }}</text>
					</view>
					<view class="row">
						<rich-text class="desc">{{ one.description }}</rich-text>
					</view>
					<view class="row">
						<u-icon
							name="rmb-circle-fill"
							color="#ff0000"
							size="18"
							:label="one.price"
							labelColor="#ff0000"
							labelSize="18"
							top="1"
						></u-icon>

						<u-icon
							name="clock-fill"
							color="#a0a0a0"
							size="18"
							label="问诊15分钟"
							labelColor="#a0a0a0"
							labelSize="16"
							top="1"
						></u-icon>
					</view>
				</view>
			</view>
			<view v-if="doctor.length == 0">
				<u-empty mode="search" icon="http://cdn.uviewui.com/uview/empty/search.png"></u-empty>
			</view>
		</view>
		<u-picker
			:show="showDeptSubPicker"
			ref="DeptSubPicker"
			:columns="deptSubColumns"
			@confirm="confirmDeptSub"
			@change="changeDeptSub"
			@cancel="cancelDeptSub"
		></u-picker>

		<u-picker
			:show="showJobPicker"
			ref="JobPicker"
			:columns="jobColumns"
			@confirm="confirmJob"
			@cancel="cancelJob"
		></u-picker>
	</view>
</template>

<script>
let dayjs = require('dayjs');
export default {
	data() {
		return {
			deptSub: '全部诊室',
			showDeptSubPicker: false,
			deptSubColumns: [],
			deptSubColumnData: [],

			job: '全部医师',
			showJobPicker: false,
			jobColumns: [['全部医师', '主任医师', '副主任医师', '主治医师', '副主治医师']],

			doctor: []
		};
	},
	methods: {
    loadDeptAndSub: function() {
        let that = this;
        that.ajax(that.api.searchDeptAndSub, 'GET', null, function(resp) {
            let result = resp.data.result;
            let i = 0;
            let deptList = [];
            let firstSubList = [];
            let subList = [];
            for (let key in result) {
                ++i;
                if (i == 1) {
                    for (let item of result[key]) {
                        firstSubList.push(item.subName);
                    }
                }
                let temp = [];
                for (let item of result[key]) {
                    temp.push(item.subName);
                }
                deptList.push(key);
                subList.push(temp);
            }
    
            that.deptSubColumns = [deptList, firstSubList];
            that.deptSubColumnData = subList;
        });
    },
    showDeptSubPickerHandler: function() {
        this.showDeptSubPicker = true;
    },

    changeDeptSub(e) {
        const {
            columnIndex,
            value,
            values, // values为当前变化列的数组内容
            index,
            // 微信小程序无法将picker实例传出来，只能通过ref操作
            picker = this.$refs.DeptSubPicker
        } = e;
        // 当第一列值发生变化时，变化第二列(后一列)对应的选项
        if (columnIndex === 0) {
            // picker为选择器this实例，变化第二列对应的选项
            picker.setColumnValues(1, this.deptSubColumnData[index]);
        }
    },
    confirmDeptSub(e) {
        this.showDeptSubPicker = false;
        this.deptSub = e.value[1];
        //TODO 加载在线医生列表
         this.loadOnlineDoctorList();
    },
    cancelDeptSub: function() {
        this.showDeptSubPicker = false;
    },
    showJobPickerHandler: function() {
        this.showJobPicker = true;
    },
    confirmJob(e) {
        this.showJobPicker = false;
        this.job = e.value[0];
        //TODO 加载在线医生列表
        this.loadOnlineDoctorList();
    },
    cancelJob: function() {
        this.showJobPicker = false;
    },
    loadOnlineDoctorList: function() {
        let that = this;
        let data = {
            subName: that.deptSub == '全部诊室' ? null : that.deptSub,
            job: that.job == '全部医师' ? null : that.job
        };
        that.ajax(that.api.searchOnlineDoctorList, 'POST', data, function(resp) {
            let result = resp.data.result;
            for (let one of result) {
                one.photo = `${that.minioUrl}/${one.photo}`;
                one.price = one.price + '元';
            }
            that.doctor = result;
        });
    },
    createVideoDiagnoseHandle: function(doctorId) {
        let that = this;
        let data = {
            doctorId: doctorId
        };
        that.ajax(that.api.createVideoDiagnose, 'POST', data, function(resp) {
            let result = resp.data.result;
            let flag = result.flag;
            if (!flag) {
                uni.showToast({
                    icon: 'none',
                    title: '医生在问诊中，请稍后再试'
                });
            } else {
                uni.showToast({
                    icon: 'success',
                    title: '挂号成功请付款'
                });
                setTimeout(function() {
                    let outTradeNo = result.outTradeNo;
                    let videoDiagnoseId = result.videoDiagnoseId;
                    let expectStart = result.expectStart;
                    let expectEnd = result.expectEnd;
                    uni.requestPayment({
                        provider: 'wxpay',
                        timeStamp: result.timeStamp, //时间戳
                        nonceStr: result.nonceStr, //随机字符串
                        package: result.package, //prepay_id
                        signType: result.signType, //签名算法，暂支持 MD5。
                        paySign: result.paySign, //数字签名
                        success(resp) {
                            //TODO 主动查询付款结果
                            data = {
                            outTradeNo: outTradeNo
                        };
                        that.ajax(that.api.searchVideoDiagnosePaymentResult, 'POST', data, function(resp) {
                            if (resp.data.result) {
                                uni.showToast({
                                    icon: 'success',
                                    title: '付款成功'
                                });
                                setTimeout(function() {
                                    uni.navigateTo({
                                        url: `../prepare_diagnose/prepare_diagnose?videoDiagnoseId=${videoDiagnoseId}&expectStart=${expectStart}&expectEnd=${expectEnd}&doctorId=${doctorId}`
                                    });
                                }, 1500);
                            } else {
                                uni.showToast({
                                    icon: 'none',
                                    title: '付款异常，请联系客服'
                                });
                            }
                        });
                            
                        }
                    });
                }, 1500);
            }
        });
    }


		
	},
	onLoad: function() {
	    this.loadDeptAndSub();
	    //TODO 加载在线医生列表
      this.loadOnlineDoctorList();
	}

};
</script>

<style lang="less">
@import url('doctor_list.less');
</style>
