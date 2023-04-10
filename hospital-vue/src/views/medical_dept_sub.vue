<template>
    <div v-if="isAuth(['ROOT', 'MEDICAL_DEPT_SUB:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
            <el-form-item prop="name">
                <el-input v-model="dataForm.name" placeholder="诊室名称" class="input" clearable="clearable" />
            </el-form-item>
            <el-form-item>
                <el-select v-model="dataForm.deptId" class="input" placeholder="隶属科室" clearable="clearable">
                    <el-option v-for="one in deptList" :label="one.name" :value="one.id" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="searchHandle()">查询</el-button>
                <el-button type="primary" :disabled="!isAuth(['ROOT', 'MEDICAL_DEPT_SUB:INSERT'])" @click="addHandle()">
                    新增
                </el-button>
                <el-button
                    type="danger"
                    :disabled="!isAuth(['ROOT', 'MEDICAL_DEPT_SUB:DELETE'])"
                    @click="deleteHandle()"
                >
                    批量删除
                </el-button>
            </el-form-item>
        </el-form>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandle"
            @sort-change="orderHandle"
            :cell-style="{ padding: '3px 0' }"
            style="width: 100%;"
        >
            <el-table-column
                type="selection"
                :selectable="selectable"
                header-align="center"
                align="center"
                width="50"
            />
            <el-table-column type="index" header-align="center" align="center" width="100" label="序号">
                <template #default="scope">
                    <span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
                </template>
            </el-table-column>
            <el-table-column
                prop="subName"
                header-align="center"
                align="center"
                label="诊室名称"
                min-width="170"
                :show-overflow-tooltip="true"
            />
            <el-table-column
                prop="deptName"
                header-align="center"
                align="center"
                label="隶属科室"
                sortable
                min-width="120"
            />
            <el-table-column header-align="center" align="center" label="医生数量" min-width="120">
                <template #default="scope">
                    <span>{{ scope.row.doctors }}人</span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" label="专家医师" min-width="120">
                <template #default="scope">
                    <span>{{ scope.row.masterDoctors }}人</span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" label="普通医师" min-width="120">
                <template #default="scope">
                    <span>{{ scope.row.generalDoctors }}人</span>
                </template>
            </el-table-column>
            <el-table-column
                prop="location"
                header-align="center"
                align="center"
                label="诊室地址"
                min-width="350"
                :show-overflow-tooltip="true"
            />
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        :disabled="!isAuth(['ROOT', 'MEDICAL_DEPT_SUB:UPDATE'])"
                        @click="updateHandle(scope.row.id)"
                    >
                        修改
                    </el-button>
                    <el-button
                        type="text"
                        :disabled="!isAuth(['ROOT', 'MEDICAL_DEPT_SUB:DELETE']) || scope.row.emps > 0"
                        @click="deleteHandle(scope.row.id)"
                    >
                        删除
                    </el-button>
                </template>
            </el-table-column>
        </el-table>
        <el-pagination
            @size-change="sizeChangeHandle"
            @current-change="currentChangeHandle"
            :current-page="pageIndex"
            :page-sizes="[10, 20, 50]"
            :page-size="pageSize"
            :total="totalCount"
            layout="total, sizes, prev, pager, next, jumper"
        ></el-pagination>
        <add-or-update ref="addOrUpdate" @refreshDataList="loadDataList"></add-or-update>
    </div>
</template>

<script>
import AddOrUpdate from './medical_dept_sub-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
    data: function() {
        return {
            deptList: [],
            dataForm: {
                name: null,
                deptId: null,
                order: null
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            dataRule: {
                name: [{ required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,10}$', message: '诊室名称格式错误' }]
            }
        };
    },
    methods: {
        loadDeptList: function() {
            let that = this;
            that.$http('/medical/dept/searchAll', 'GET', {}, true, function(resp) {
                let result = resp.result;
                that.deptList = result;
            });
        },
        sizeChangeHandle: function(val) {
            this.pageSize = val;
            this.pageIndex = 1;
            this.loadDataList();
        },
        currentChangeHandle: function(val) {
            this.pageIndex = val;
            this.loadDataList();
        },
        searchHandle: function() {
            this.$refs['dataForm'].validate(valid => {
                if (valid) {
                    this.$refs['dataForm'].clearValidate();
                    if (this.dataForm.name == '') {
                        this.dataForm.name = null;
                    }
                    if (this.dataForm.deptId == '') {
                        this.dataForm.deptId = null;
                    }
                    if (this.pageIndex != 1) {
                        this.pageIndex = 1;
                    }
                    this.loadDataList();
                } else {
                    return false;
                }
            });
        },
        addHandle: function() {
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init();
            });
        },
        updateHandle: function(id) {
            this.$nextTick(() => {
                this.$refs.addOrUpdate.init(id);
            });
        },
        selectionChangeHandle: function(val) {
            this.dataListSelections = val;
        },
        selectable: function(row, index) {
            if (row.emps > 0) {
                return false;
            }
            return true;
        },
        deleteHandle: function(id) {
            let that = this;
            let ids = id
                ? [id]
                : that.dataListSelections.map(item => {
                      return item.id;
                  });
            if (ids.length == 0) {
                ElMessage({
                    message: '没有选中记录',
                    type: 'warning',
                    duration: 1200
                });
            } else {
                ElMessageBox.confirm('确定要删除选中的记录？', '提示信息', {
                    confirmButtonText: '确定',
                    cancelButtonText: '取消',
                    type: 'warning'
                }).then(() => {
                    that.$http('/medical/dept/sub/deleteByIds', 'POST', { ids: ids }, true, function(resp) {
                        ElMessage({
                            message: '操作成功',
                            type: 'success',
                            duration: 1200,
                            onClose: () => {
                                that.loadDataList();
                            }
                        });
                    });
                });
            }
        },


    },
   created: function() {
       this.loadDeptList();
       this.loadDataList();
   }

};
</script>

<style></style>
