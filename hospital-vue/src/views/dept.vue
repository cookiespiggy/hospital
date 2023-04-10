<template>
	<div v-if="isAuth(['ROOT', 'DEPT:SELECT'])"></div>
</template>

<script>
import AddOrUpdate from './dept-add-or-update.vue';
export default {
	components: {
		AddOrUpdate
	},
	data: function() {
		return {};
	},
	methods: {},
	created: function() {}
};
</script>

<style lang="less" scoped="scoped"></style>
