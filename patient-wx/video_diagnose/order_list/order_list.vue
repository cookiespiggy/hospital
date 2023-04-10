<template>
	<view>
		<view class="banner" @click="registerHandle">
			<image :src="doctorImg" mode="widthFix"></image>
			<view>
				<view class="row-1">
					<text class="title">视频问诊</text>
					<text class="remark">接诊快</text>
				</view>
				<view class="row-2"><view class="btn">立即挂号</view></view>
				<view class="row-3">查找名医，远程诊断，预约挂号，不再等待</view>
			</view>
		</view>
		<view class="order" v-for="one of list" :key="one">
			<view class="row">
				<view @longtap="copyOutTradeNoHandle(one.outTradeNo)">
					<u-icon
						:label="`订单编号：${one.outTradeNoShort}`"
						color="#a0a0a0"
						labelColor="#a0a0a0"
						size="20"
						name="order"
					></u-icon>
				</view>
				<block v-if="one.paymentStatus != 3">
					<text
						class="status blue"
						v-if="one.status == '进入问诊室'"
						@tap="enterHandle(one.id, one.expectStart, one.expectEnd,one.doctorId)"
					>
						进入问诊室
					</text>
					<text
						class="status"
						v-if="one.status != '进入问诊室'"
						@tap="enterHandle(one.id, one.expectStart, one.expectEnd, one.doctorId)"
					>
						{{ one.status }}
					</text>
				</block>
			</view>
			<view class="row">
				<view>
					<text class="doctor-name">{{ one.doctorName }}</text>
					<text class="job">（{{ one.job }}）</text>
				</view>
				<view>
					<text class="unit">￥</text>
					<text class="amount">{{ one.amount }}</text>
				</view>
			</view>
			<view class="row">
				<u-icon
					:label="`${one.subName}（${one.expectStartShort} ~ ${one.expectEndShort}）`"
					color="#555"
					labelColor="#555"
					size="18"
					name="server-fill"
				></u-icon>
			</view>
			<view class="row">
				<view class="create-time">
					<u-icon
						:label="one.createTime"
						color="#a0a0a0"
						labelColor="#a0a0a0"
						size="20"
						name="calendar-fill"
						top="0"
					></u-icon>
				</view>
				<view class="btn green" v-if="one.paymentStatus == 1">未付款</view>
				<view class="btn orange" v-if="one.paymentStatus == 2">已付款</view>
				<view class="btn gray" v-if="one.paymentStatus == 3">已关闭</view>
			</view>
		</view>
		<view v-if="list.length == 0">
			<u-empty mode="order" text="没有问诊挂号记录" icon="http://cdn.uviewui.com/uview/empty/order.png"></u-empty>
		</view>
	</view>
</template>

<script>
const dayjs = require('dayjs');
export default {
	data() {
		return {
			doctorImg: `${this.patientUrl}/pic/pic-2.png`,
			page: 1,
			length: 50,
			list: [],
			isLastPage: false
		};
	},
	methods: {
    registerHandle: function() {
        uni.navigateTo({
            url: '../doctor_list/doctor_list'
        });
    },
    loadDataList: function(ref) {
        let data = {
            page: ref.page,
            length: ref.length
        };
        ref.ajax(ref.api.searchVideoDiagnoseByPage, 'POST', data, function(resp) {
            let result = resp.data.result;
            if (result.list == null || result.list.length == 0) {
                ref.isLastPage = true;
                ref.page = ref.page - 1;
                uni.showToast({
                    icon: 'none',
                    title: '已经到底了'
                });
            } else {
                let json = {
                    '1': '进入问诊室',
                    '2': '进入问诊室',
                    '3': '已结束'
                };
                for (let one of result.list) {
                    // console.log(one)
                    one.status = json[one.status + ''];
                    one.expectStartShort = dayjs(one.expectStart).format('HH:mm');
                    one.expectEndShort = dayjs(one.expectEnd).format('HH:mm');
                    one.outTradeNoShort = one.outTradeNo.substr(0, 15) + '…';
                    ref.list.push(one);
                }
            }
        });
    },
    copyOutTradeNoHandle: function(outTradeNo) {
        uni.setClipboardData({
            data: outTradeNo,
            showToast: false,
            success: function() {
                uni.showToast({
                    icon: 'success',
                    title: '已复制订单号'
                });
            }
        });
    },
    enterHandle: function(id, expectStart, expectEnd, doctorId) {
        uni.navigateTo({
            url: `../prepare_diagnose/prepare_diagnose?videoDiagnoseId=${id}&expectStart=${expectStart}&expectEnd=${expectEnd}&doctorId=${doctorId}`
        });
    },
	},
	onShow: function() {
	    let that = this;
	    that.page = 1;
	    that.list = [];
	    that.isLastPage = false;
	    that.loadDataList(that);
	},

	onReachBottom: function() {
	    let that = this;
	    console.log(that.isLastPage);
	    if (that.isLastPage) {
	        return;
	    }
	    that.page = that.page + 1;
	    that.loadDataList(that);
	}

};
</script>

<style lang="less">
@import url('order_list.less');
</style>
