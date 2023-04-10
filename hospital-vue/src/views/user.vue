<template>
	<div v-if="isAuth(['ROOT', 'USER:SELECT'])"></div>
</template>

<script>
import AddOrUpdate from './user-add-or-update.vue';
export default {
	components: {
		AddOrUpdate
	},
	data() {
		return {};
	},
	methods: {},
	created: function() {}
};
</script>

<style lang="less" scoped="scoped"></style>
