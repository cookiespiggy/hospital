<template>
	<view>
		<view class="date-container">
			<view class="item" v-for="one in dateList">
				<text class="day">{{ one.day }}</text>
				<view :class="one.date == date ? 'selector active' : 'selector'" @tap="clickDateHandle(one.date)">
					<text class="date">{{ one.dateOfMonth }}</text>
					<text :class="one.status == '无号' ? 'status gray' : 'status'">{{ one.status }}</text>
				</view>
			</view>
		</view>
		<view class="doctor" v-for="one in doctorList">
			<u-avatar :src="one.photo" size="45"></u-avatar>
			<view class="info">
				<view class="row">
					<text class="name">{{ one.name }}</text>
					<text class="job">（{{ one.job }}）</text>
					<button class="btn" @tap="clickBtnHandle(one.id, date)">挂号</button>
				</view>
				<view class="row">
					<text class="num">总量:{{ one.maximum }}</text>
					<text class="price">￥{{ one.price }}</text>
				</view>
				<view class="row">
					<rich-text class="desc">{{ one.description }}</rich-text>
				</view>
			</view>
		</view>
		<u-empty
			v-if="showEmpty"
			mode="list"
			text="无医生出诊记录"
			width="150"
			icon="http://cdn.uviewui.com/uview/empty/order.png"
		></u-empty>
	</view>
</template>

<script>
let dayjs = require('dayjs');
export default {
	data() {
		return {
			deptSubId: null,
			showEmpty: false,
			date: dayjs().format('YYYY-MM-DD'),
			dateList: [],
			doctorList: []
		};
	},
	methods: {
    //查询某个诊室7天出诊情况
    searchCanRegisterInDateRange: function(ref, deptSubId) {
        let startDate = dayjs().format('YYYY-MM-DD');
        let endDate = dayjs().add(6, 'day').format('YYYY-MM-DD');
        let data = {
            deptSubId: deptSubId,
            startDate: startDate,
            endDate: endDate
        };
        ref.ajax(ref.api.searchCanRegisterInDateRange, 'POST', data,
            function(resp) {
                let result = resp.data.result;
                //定义工具数组
                let array = ['日', '一', '二', '三', '四', '五', '六'];
                for (let one of result) {
                    //把星期几的阿拉伯数字转换成汉字数字
                    let day = array[dayjs(one.date).day()];
                    one.day = day;
                    one.dateOfMonth = dayjs(one.date).date();
                }
                ref.dateList = result;
            },
            false
        );
    },
    
    //查找某天科室医生出诊列表
    searchDeptSubDoctorPlanInDay: function(ref) {
        let data = {
            deptSubId: ref.deptSubId,
            date: ref.date
        };
    
        ref.ajax(ref.api.searchDeptSubDoctorPlanInDay, 'POST', data,
            function(resp) {
                let result = resp.data.result;
                //把头像相对路径合成绝对路径
                for (let one of result) {
                    one.photo = `${ref.minioUrl}/${one.photo}`;
                }
                ref.doctorList = result;
                if (result.length == 0) {
                    ref.showEmpty = true;
                } else {
                    ref.showEmpty = false;
                }
            },
            false
        );
    },
    clickDateHandle: function(date) {
        let that = this;
        that.date = date;
        that.searchDeptSubDoctorPlanInDay(that);
    },
    clickBtnHandle: function(id, date) {
        //检查用户在当天有没有挂过该科室的号
        let that = this;
        let data = {
            deptSubId: that.deptSubId,
            date: that.date
        };
    
        that.ajax(
            that.api.checkRegisterCondition,
            'POST',
            data,
            function(resp) {
                let result = resp.data.result;
                if (result == '已经达到当天挂号上限') {
                    uni.showToast({
                        icon: 'none',
                        title: '每天只能挂号3次，您已达到当日上限'
                    });
                } else if (result == '已经挂过该诊室的号') {
                    uni.showToast({
                        icon: 'none',
                        title: '您已经挂过该诊室号了，不可重复挂号'
                    });
                } else if (result == '不存在面部模型') {
                    uni.showModal({
                        title: '提示消息',
                        content: '挂号需要人脸识别验证身份，你还没有录入面部信息，是否立即采集面部信息？',
                        confirmText: '录入',
                        success: function(resp) {
                            if (resp.confirm) {
                                //跳转到面部采集页面，录入面部信息
                                uni.navigateTo({
                                    url: '/user/face_camera/face_camera?mode=create'
                                });
                            }
                        }
                    });
                } else if (result == '当日没有人脸验证记录') {
                    uni.showModal({
                        title: '提示消息',
                        content: '每天第一次挂号之前，需要核验面部信息，以便确保是您本人挂号',
                        confirmText: '验证',
                        success: function(resp) {
                            if (resp.confirm) {
                                //跳转到面部采集页面，验证面部信息
                                uni.navigateTo({
                                    url: '/user/face_camera/face_camera?mode=verificate'
                                });
                            }
                        }
                    });
                } else {
                    //TODO 跳转到doctor_schedule页面
                uni.navigateTo({
                  url: `/registration/doctor_schedule/doctor_schedule?deptSubId=${that.deptSubId}&doctorId=${id}&date=${date}`
                                });
                            }
                        },
            false
        );
    },



		
	},
	onLoad: function(options) {
	    let that = this;
	    //取出URL参数
	    let deptSubId = options.deptSubId;
	    let deptSubName = options.deptSubName;
	
	    that.deptSubId = deptSubId;
	    //设置当前页面标题栏文字
	    uni.setNavigationBarTitle({
	        title: deptSubName
	    });
	  
	    that.searchCanRegisterInDateRange(that,deptSubId)
	    that.searchDeptSubDoctorPlanInDay(that);
	}

};
</script>

<style lang="less">
@import url(dept_sub_plan.less);
</style>
