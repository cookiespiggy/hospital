<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT', 'USER:INSERT', 'USER:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="450px"
	></el-dialog>
</template>

<script>
import dayjs from 'dayjs';
export default {
	data: function() {
		return {};
	},
	methods: {}
};
</script>

<style lang="less" scoped="scoped"></style>
