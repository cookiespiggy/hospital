<template>
	<el-dialog
		title="修改"
		v-if="isAuth(['ROOT', 'DOCTOR_PRICE:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="450px"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="100px">
			<el-form-item label="门诊挂号费" prop="price_1">
				<el-input v-model="dataForm.price_1" style="width:100%" clearable />
			</el-form-item>
			<el-form-item label="视频问诊费" prop="price_2">
				<el-input v-model="dataForm.price_2" style="width:100%" clearable />
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button @click="visible = false">取消</el-button>
				<el-button type="primary" @click="dataFormSubmit">确定</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			dataForm: {
				doctorId: null,
				price_1: null,
				price_2: null,
				level: null
			},
			dataRule: {
				price_1: [
					{ required: true, message: '必须填写金额' },
					{ pattern: '^[1-9]\\d*\\.\\d{1,2}$|^0\\.\\d{1,2}$|^[1-9]\\d*$', message: '金额格式错误' }
				],
				price_2: [
					{ required: true, message: '必须填写金额' },
					{ pattern: '^[1-9]\\d*\\.\\d{1,2}$|^0\\.\\d{1,2}$|^[1-9]\\d*$', message: '金额格式错误' }
				]
			}
		};
	},

	methods: {
		
	}
};
</script>

<style lang="less" scoped="scoped"></style>
