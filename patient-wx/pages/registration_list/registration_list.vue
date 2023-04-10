<template>
	<view>
		<view class="registration" v-for="(one, index) of list">
			<view class="calendar">
				<view class="month">{{ one.month }}月</view>
				<view class="day">{{ one.day }}日</view>
			</view>
			<view class="content">
				<view class="row">
					<text class="deptSubName">{{ one.name }}（{{ one.job }}）</text>
					<view class="more" @tap="moreHandle(one.id)">
						<text>more</text>
						<u-icon name="arrow-right" color="#aaa" size="14"></u-icon>
					</view>
				</view>
				<view class="row">
					<text class="address">地址：{{ one.location }}</text>
				</view>
				<view class="row">
					<text class="datetime">时间：{{ one.date }} {{ one.time }}</text>
				</view>
				<view class="unpaid" v-if="one.paymentStatus == 1 && !one.discarded">
					<u-button type="primary" size="small" @click="payHandle(one.id, index)">待支付</u-button>
				</view>
				<view class="paid" v-if="one.paymentStatus == 2">已支付</view>
				<view class="refund" v-if="one.paymentStatus == 3">已退款</view>
				<view class="discarded" v-if="one.paymentStatus == 4">已过期</view>
			</view>
		</view>
	</view>
</template>

<script>
let dayjs = require('dayjs');
const isBetween = require('dayjs/plugin/isBetween');
dayjs.extend(isBetween);
export default {
	data() {
		return {
			list: [],
			page: 1,
			length: 50,
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
			reload: true
		};
	},
	methods: {
    loadData: function() {
        let that = this;
        let data = {
            page: that.page,
            length: that.length
        };
        that.ajax(
            that.api.searchRegistrationByPage,
            'POST',
            data,
            function(resp) {
                let result = resp.data.result;
                for (let one of result.list) {
                    if (['主任医师', '副主任医师'].includes(one.job)) {
                        one.job = '专家';
                    } else {
                        one.job = '普通';
                    }
    
                    let temp = dayjs(one.date);
                    one.month = temp.format('MM');
                    one.day = temp.format('DD');
                    one.time = that.json[one.slot];
                    let now = dayjs();
                    let rangeStart = dayjs(`${one.date} ${one.time}`);
                    let rangeEnd = rangeStart.add(30, 'minute');
                    //判断该挂号记录是否已过期
                    if (one.paymentStatus == 1 && (now.isBetween(rangeStart, rangeEnd) || now.isAfter(rangeEnd))) {
                        one.discarded = true;
                    } else {
                        one.discarded = false;
                    }
                }
                that.list = that.list.concat(result.list);
            },
            false
        );
    },
    onShow: function() {
    if (this.reload) {
        this.page = 1;
        this.list.length = 0;
        this.loadData();
    }
},

    payHandle: function(id, index) {
        let that = this;
        uni.showModal({
            title: '提示消息',
            content: '您确定支付该笔挂号费？',
            success: function(resp) {
                if (resp.confirm) {
                    let data = {
                        id: id
                    };
                    that.ajax(that.api.repayRegistration, 'POST', data, function(resp) {
                        let data = resp.data;
                        let outTradeNo = data.outTradeNo;
                        uni.requestPayment({
                            provider: 'wxpay',
                            timeStamp: data.timeStamp, //当前的时间
                            nonceStr: data.nonceStr, //随机字符串
                            package: data.package, //统一下单接口返回的 prepay_id 参数值
                            signType: data.signType, //签名算法，暂支持 MD5。
                            paySign: data.paySign,
                            success(resp) {
                                //让后端查询付款结果，更新订单状态
                                data = {
                                    outTradeNo: outTradeNo
                                };
                                that.ajax(that.api.searchPaymentResult, 'POST', data, function(resp) {
                                    if (resp.data.result) {
                                        uni.showToast({
                                            icon: 'success',
                                            title: '付款成功'
                                        });
                                    } else {
                                        uni.showToast({
                                            icon: 'error',
                                            title: '付款异常，请联系客服'
                                        });
                                    }
                                    that.list[index].paymentStatus = 2;
                                });
                            }
                        });
                    });
                }
            }
        });
    },

    moreHandle: function(id) {
        uni.navigateTo({
            url: '/registration/registration_info/registration_info?id=' + id
        });
    }



		
	},
	onShow: function() {
	    if (this.reload) {
	        this.page = 1;
	        this.list.length = 0;
	        this.loadData();
	    }
	},


	onReachBottom: function() {
	    that.page++;
	    that.loadData();
	}

};
</script>

<style lang="less">
@import url(registration_list.less);
</style>
