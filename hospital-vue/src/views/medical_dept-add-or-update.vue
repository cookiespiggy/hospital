<template>
	<el-dialog
		:title="!dataForm.id ? '新增' : '修改'"
		v-if="isAuth(['ROOT', 'MEDICAL_DEPT:INSERT', 'MEDICAL_DEPT:UPDATE'])"
		:close-on-click-modal="false"
		v-model="visible"
		width="450px"
		:destroy-on-close="true"
	>
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
			<el-form-item label="科室名称" prop="name">
				<el-input v-model="dataForm.name" style="width:100%" clearable />
			</el-form-item>
			<el-form-item label="科室类型" prop="outpatient">
				<el-select v-model="dataForm.outpatient" class="input" placeholder="科室类型" clearable="clearable">
					<el-option label="门诊" value="true" />
					<el-option label="非门诊" value="false" />
				</el-select>
			</el-form-item>
			<el-form-item label="推荐级别" prop="recommended">
				<el-select v-model="dataForm.recommended" class="input" placeholder="推荐级别" clearable="clearable">
					<el-option label="优先" value="true" />
					<el-option label="非优先" value="false" />
				</el-select>
			</el-form-item>
			<el-form-item label="科室介绍" prop="description">
				<el-input
					v-model="dataForm.description"
					type="textarea"
					:rows="8"
					style="width:100%"
					maxlength="500"
					show-word-limit
					clearable
				/>
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
				id: null,
				name: null,
				outpatient: null,
				recommended: null,
				description: null
			},
			dataRule: {
				name: [{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,10}$', message: '科室名称格式错误' }],
				outpatient: [{ required: true, message: '选择科室类型' }],
				recommended: [{ required: true, message: '选择推荐级别' }],
				description: [{ required: true, message: '填写科室介绍' }]
			}
		};
	},

	methods: {
    init: function(id) {
        let that = this;
        that.reset();
        that.dataForm.id = id || 0;
        that.visible = true;
        that.$nextTick(() => {
            that.$refs['dataForm'].resetFields();
            //TODO 查询科室用于修改业务
            
            if (id) {
                        that.$http('/medical/dept/searchById', 'POST', { id: id }, true, function(resp) {
                            that.dataForm.name = resp.name;
                            that.dataForm.outpatient = resp.outpatient + '';
                            that.dataForm.recommended = resp.recommended + '';
                            that.dataForm.description = resp.description;
                        });
                    }
        });
    },
    addHandle: function() {
        this.$nextTick(() => {
            this.$refs.addOrUpdate.init();
        });
    },
    dataFormSubmit: function() {
        let that = this;
        this.$refs['dataForm'].validate(valid => {
            if (valid) {
                that.$http(
                    `/medical/dept/${!that.dataForm.id ? 'insert' : 'update'}`,
                    'POST',
                    that.dataForm,
                    true,
                    function(resp) {
                        ElMessage({
                            message: '操作成功',
                            type: 'success'
                        });
                        that.visible = false;
                        that.$emit('refreshDataList');
                    }
                );
            }
        });
    },
    

		
	}
};
</script>

<style lang="less" scoped="scoped"></style>
