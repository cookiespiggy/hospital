<template>
    <el-dialog
        :title="!dataForm.id ? '新增' : '修改'"
        v-if="isAuth(['ROOT', 'MEDICAL_DEPT_SUB:INSERT', 'MEDICAL_DEPT_SUB:UPDATE'])"
        :close-on-click-modal="false"
        v-model="visible"
        width="450px"
    >
        <el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
            <el-form-item label="诊室名称" prop="name">
                <el-input v-model="dataForm.name" style="width:100%" clearable />
            </el-form-item>
            <el-form-item label="隶属科室" prop="deptId">
                <el-select v-model="dataForm.deptId" class="input" placeholder="选择科室" clearable="clearable">
                    <el-option v-for="one in deptList" :label="one.name" :value="one.id" />
                </el-select>
            </el-form-item>
            <el-form-item label="诊室地址" prop="location">
                <el-input
                    v-model="dataForm.location"
                    type="textarea"
                    :rows="2"
                    style="width:100%"
                    maxlength="50"
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
            deptList: [],
            dataForm: {
                id: null,
                name: null,
                deptId: null,
                location: null
            },
            dataRule: {
                name: [{ required: true, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{2,10}$', message: '诊室名称格式错误' }],
                deptId: [{ required: true, message: '选择科室' }],
                location: [{ required: true, message: '填写诊室地址' }]
            }
        };
    },

    methods: {
        reset: function() {
            let dataForm = {
                id: null,
                name: null,
                deptId: null,
                location: null
            };
            this.dataForm = dataForm;
        },
        loadDeptList: function() {
            let that = this;
            that.$http('/medical/dept/searchAll', 'GET', {}, true, function(resp) {
                let result = resp.result;
                that.deptList = result;
            });
        },
        init: function(id) {
            let that = this;
            that.reset();
            that.dataForm.id = id || 0;
            that.visible = true;
            that.$nextTick(() => {
                that.$refs['dataForm'].resetFields();
                that.loadDeptList();
                //TODO 查询诊室数据
                if (id) {
                            that.$http('/medical/dept/sub/searchById', 'POST', { id: id }, true, function(resp) {
                                that.dataForm.name = resp.name;
                                that.dataForm.deptId = resp.deptId;
                                that.dataForm.location = resp.location;
                            });
                        }
                    });
                },
        dataFormSubmit: function() {
            let that = this;
            this.$refs['dataForm'].validate(valid => {
                if (valid) {
                    that.$http(
                        `/medical/dept/sub/${!that.dataForm.id ? 'insert' : 'update'}`,
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
        }
    
    }
};
</script>

<style lang="less" scoped="scoped"></style>
