<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT', 'ROLE:INSERT', 'ROLE:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		custom-class="dialog"
		width="692px"
	></el-dialog>
</template>

<script>
export default {
	data: function() {
		return {};
	},

	methods: {}
};
</script>

<style lang="less" scoped="scoped">
.note {
	margin-left: 20px;
	color: #999;
}
</style>
