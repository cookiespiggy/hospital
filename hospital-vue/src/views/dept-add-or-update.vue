<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT', 'DEPT:INSERT', 'DEPT:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="420px"
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

<style lang="less" scoped="scoped"></style>
