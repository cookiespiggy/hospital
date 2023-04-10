<template>
	<view>
		<text class="title">就诊须知</text>
		<text class="desc">发布单位：北京市神州互联网医院第一附属医院</text>
		<image :src="bannerUrl" mode="widthFix" class="banner"></image>
		<u-parse :content="content" :tagStyle="style"></u-parse>
		<view class="operate"><u-button size="large" type="primary" @click="acceptHandle">我已知晓</u-button></view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			bannerUrl: `${this.patientUrl}/page/registration/notice/banner.jpg`,
			content: `
				<h3>1.就诊提醒：</h3>
				<p>
					按照北京市新冠肺炎疫情防控指挥部要求，北京市涉疫区域人员（健康码黄码、封控区、管控区及隔离的人员）请先与北京市医政部门联系安排就诊医院，确需到我院就诊的，再由北京市医政部门安排转运。来自非中高风险地区、重点管控和重点关注地区的门诊就诊患者提供72小时核酸检测结果就诊，我院门诊严格执行实名预约挂号就诊制度，就诊患者请携带本人有效身份证件（身份证、医保卡等）。
				</p>
				<h3>2.温馨提示：</h3>
				<p>
					目前门诊就诊患者较多，一位患者只能一位陪同，请陪同人员完成预检筛查。并出示身份证等有效证件配合流调人员的工作，必要时做好行程轨迹查询，确保安全就诊。谢谢您的配合！
				</p>
			`,
			style: {
				h3: `
					color:#444444;
					margin:30rpx 45rpx 0 45rpx;
					line-height:1.8;
				`,
				p: `
				color:#444444;
				margin:10rpx 45rpx 10rpx 45rpx;
				line-height:1.8;
				`
			}
		};
	},
	methods: {
    acceptHandle: function() {
        let that = this;
        //检查用户是否登陆了
        let token = uni.getStorageSync('token');
        if (token == null || token.length == 0) {
            uni.showToast({
                icon: 'error',
                title: '请先登录小程序'
            });
            setTimeout(function() {
                uni.switchTab({
                    url: '/pages/mine/mine'
                });
            }, 2000);
        }
        //检查用户是否创建了就诊卡
        that.ajax(that.api.hasUserInfoCard,'GET',{},function(resp) {
                let result = resp.data.result;
                if (result) {
                    uni.redirectTo({
                        url: '/registration/medical_dept_list/medical_dept_list'});
                } else {
                    uni.showToast({
                        icon: 'error',
                        title: '请先创建就诊卡'
                    });
                    setTimeout(function() {
                        uni.switchTab({
                            url: '/pages/mine/mine'
                        });
                    }, 2000);
                }
            },
            false);
    }

		
	},
	onLoad: function() {}
};
</script>

<style lang="less">
@import url(notice.less);
</style>
