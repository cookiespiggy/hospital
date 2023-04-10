<template>
	<view>
		<view class="main">
			<view class="left" :style="height">
				<view
					:class="one.id == deptId ? 'item active' : 'item'"
					v-for="one in deptList"
					@tap="clickDeptHandle(one.id)"
				>
					{{ one.name }}
				</view>
			</view>
			<view class="right">
				<view class="item" v-for="one in sub" @tap="clickSubHandle(one.id, one.name)">
        {{ one.name }}
        </view>
			</view>
		</view>
	</view>
</template>

<script>
export default {
	data() {
		return {
			deptId: null,
			sub: [],
			height: 0,
			deptList: []
		};
	},
	methods: {
    //查询科室列表（有门诊的科室）
    searchMedicalDeptList: function(ref) {
        let data = {
            outpatient: true
        };
        ref.ajax(
            ref.api.searchMedicalDeptList,
            'POST',
            data,
            function(resp) {
                ref.deptList = resp.data.result;
                //取出第一个科室的ID
                ref.deptId = ref.deptList[0].id;
                //根据第一个科室ID查找该科室的诊室列表
                ref.searchMedicalDeptSubList(ref, ref.deptId);
            },
            false
        );
    },
    
    //查询诊室列表
    searchMedicalDeptSubList: function(ref, deptId) {
        let data = { deptId: deptId };
        ref.ajax(
            ref.api.searchMedicalDeptSubList,
            'POST',
            data,
            function(resp) {
                ref.sub = resp.data.result;
            },
            false
        );
    },

    clickDeptHandle: function(deptId) {
        this.deptId=deptId
    	  this.searchMedicalDeptSubList(this, deptId);
    },
    clickSubHandle: function(deptSubId, deptSubName) {
        uni.navigateTo({
            url: `/registration/dept_sub_plan/dept_sub_plan?deptSubId=${deptSubId}&deptSubName=${deptSubName}`
        });
    }

		
	},
	onLoad: function() {
	    let that = this;
	    let info = uni.getSystemInfoSync();
	    let height = info.windowHeight;
	    //View标签适配的高度=屏幕高度-小程序标题栏高度-小程序底部导航栏高度
	    that.height = `height:${height - 15 - 44}px`;
	
	    that.searchMedicalDeptList(that);
	}

};
</script>

<style lang="less">
@import url(medical_dept_list.less);
</style>
