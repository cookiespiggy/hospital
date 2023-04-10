<template>
	<view>
		<view class="doctor">
			<u-avatar :src="photo" size="45"></u-avatar>
			<view class="info">
				<view class="row">
					<text class="name">{{ name }}</text>
					<text class="job">（{{ job }}）</text>
				</view>
				<view class="row">
					<text class="remark">{{ remark }}</text>
				</view>
				<view class="row">
					<rich-text class="desc">{{ description }}</rich-text>
				</view>
			</view>
		</view>
		<view class="schedule-container">
			<view>
				<text class="title">选择挂号时间</text>
				<u-grid :border="false" col="4">
					<u-grid-item v-for="one in schedule" :key="one.slot">
						<text
							:class="one.style"
							@click="clickScheduleHandler(one.workPlanId, one.scheduleId, one.slot)"
						>
							{{ one.range }}
						</text>
					</u-grid-item>
				</u-grid>
			</view>
			<u-parse :content="notice"></u-parse>
			<u-button type="primary" size="large" @click="submitHandler">挂号费{{ price }}元，去支付</u-button>
		</view>
	</view>
</template>

<script>
const dayjs = require('dayjs');
const isBetween = require('dayjs/plugin/isBetween');
dayjs.extend(isBetween);
export default {
	data() {
		return {
			workPlanId: null,
			scheduleId: null,
			date: '',
			deptSubId: null,
			doctorId: null,
			name: '',
			photo: '',
			job: '',
			remark: '',
			description: '',
			price: '',
			slot: null,
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
			schedule: [],
			notice: `
				<ol class='notice-list'>
					<li class='notice-item'>挂号平台提供次日起七天的预约服务，用户可预约中医院的大部分科室次日起至七天的就诊号源。</li>
					<li class='notice-item'>因为医生工作繁忙，可能更改或者取消预约时间，届时会第一时间通知您，请留意短信通知。</li>
					<li class='notice-item'>如果您在就诊当天不能前往医院取号就诊，请提前通过挂号平台取消预约，否则会因造成号源的浪费，请您谅解。</li>
				</ol>
			`
		};
	},
	methods: {
    searchDoctorInfoById: function(that) {
        let data = {
            id: that.doctorId
        };
        that.ajax(
            that.api.searchDoctorInfoById,
            'POST',
            data,
            function(resp) {
                that.name = resp.data.name;
                that.photo = `${that.minioUrl}/${resp.data.photo}`;
                that.job = resp.data.job;
                that.remark = resp.data.remark;
                that.description = resp.data.description;
                that.price = resp.data.price;
            },
            false
        );
    },
    
    searchDoctorWorkPlanSchedule: function(that) {
        let data = {
            date: that.date,
            doctorId: that.doctorId
        };
        that.ajax(
            that.api.searchDoctorWorkPlanSchedule,
            'POST',
            data,
            function(resp) {
                let result = resp.data.result;
                let schedule = [];
                let now = dayjs();
                let date = now.format('YYYY-MM-DD');
                for (let one in that.json) {
                    let rangeStart = dayjs(`${date} ${that.json[one]}`);
    
                    let item = result.find(function(element) {
                        return element.slot + '' == one;
                    });
                    let style = null;
                    //如果挂今天的时段，如果该出诊时段已经过期了，就使用禁用效果的CSS样式
                    if (that.date == date && now.isAfter(rangeStart)) {
                        style = 'item disable';
                    } 
                    //如果该时段医生不出诊，使用禁用效果的CSS样式
                    else if (item == undefined) {
                        style = 'item disable';
                    } 
                    //如果该时段挂号人数已达最大值，使用禁用效果的CSS样式
                    else if (item.num == item.maximum) {
                        style = 'item disable';
                    } 
                    //如果可以挂号，该时段使用正常CSS样式
                    else {
                        style = 'item';
                    }
                  
                    schedule.push({
                        workPlanId: item != undefined ? item.workPlanId : null,
                        scheduleId: item != undefined ? item.scheduleId : null,
                        slot: one,
                        range: that.json[one],
                        style: style
                    });
                }
                that.schedule = schedule;
            },
            false
        );
    },
    clickScheduleHandler: function(workPlanId, scheduleId, slot) {
        let that = this;
        that.workPlanId = workPlanId;
        that.scheduleId = scheduleId;
        for (let one of that.schedule) {
            if (one.style == 'item disable') {
                // that.slot = null;
                continue;
            }
            one.style = 'item';
            if (one.slot == slot) {
                one.style = 'item active';
                that.slot = slot;
            }
        }
    },
    submitHandler: function() {
        let that = this;
        if (that.slot == null) {
            uni.showToast({
                icon: 'error',
                title: '选择预约时间'
            });
            return;
        }
        uni.showModal({
            title: '提示信息',
            content: '确定付款挂号？',
            success: function(resp) {
                if (resp.confirm) {
                    let data = {
                        workPlanId: that.workPlanId,
                        scheduleId: that.scheduleId,
                        date: that.date,
                        doctorId: that.doctorId,
                        deptSubId: that.deptSubId,
                        amount: that.price,
                        slot: that.slot
                    };
                    that.ajax(that.api.registerMedicalAppointment, 'POST', data, function(resp) {
                        let data = resp.data;
                        if (!data.hasOwnProperty('outTradeNo')) {
                            uni.showToast({
                                icon: 'none',
                                title: '该时段已经不可挂号'
                            });
                            setTimeout(function() {
                                that.searchDoctorInfoById(that);
                                that.searchDoctorWorkPlanSchedule(that);
                            }, 1500);
                        }
                        let outTradeNo = data.outTradeNo;
                        uni.requestPayment({
                            provider: 'wxpay',
                            timeStamp: data.timeStamp, //当前的时间
                            nonceStr: data.nonceStr, //随机字符串
                            package: data.package, //统一下单接口返回的 prepay_id 参数值
                            signType: data.signType, //签名算法，暂支持 MD5。
                            paySign: data.paySign,
                            success(resp) {
                                //这里是新添加的代码
                                data = {
                                    outTradeNo: outTradeNo
                                };
                                that.ajax(that.api.searchRegisterPaymentResult, 'POST', data, function(resp) {
                                    if (resp.data.result) {
                                        uni.showToast({
                                            icon: 'success',
                                            title: '付款成功'
                                        });
                                    } else {
                                        uni.showToast({
                                            icon: 'none',
                                            title: '付款异常，请联系客服'
                                        });
                                    }
                                    setTimeout(function() {
                                        uni.switchTab({
                                            url: '/pages/registration_list/registration_list'
                                        });
                                    }, 1500);
                                });
                            }
                        });
                    });
                }
            }
        });
    },
	
	},
	onLoad: function(options) {
	    let that = this;
	    that.date = options.date;
	    that.doctorId = options.doctorId;
	    that.deptSubId = options.deptSubId;
	
	    that.searchDoctorInfoById(that);
	    that.searchDoctorWorkPlanSchedule(that);
	}

};
</script>

<style lang="less">
@import url(doctor_schedule.less);
</style>
