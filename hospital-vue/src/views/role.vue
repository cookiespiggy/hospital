<template>
	<div v-if="isAuth(['ROOT', 'ROLE:SELECT'])">
		
	</div>
</template>

<script>
import AddOrUpdate from './role-add-or-update.vue';
export default {
	components: {
		AddOrUpdate
	},
	data: function() {
		return {
			
		};
	},
	methods: {
		
	},
	created: function() {
	}
};
</script>

<style></style>
