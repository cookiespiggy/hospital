<template>
    <div v-if="isAuth(['ROOT', 'MEDICAL_DEPT:SELECT'])">
        <el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
            <el-form-item prop="name">
                <el-input v-model="dataForm.name" placeholder="科室名称" class="input" clearable="clearable" />
            </el-form-item>
            <el-form-item>
                <el-select v-model="dataForm.outpatient" class="input" placeholder="科室类型" clearable="clearable">
                    <el-option label="门诊" value="true" />
                    <el-option label="非门诊" value="false" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-select v-model="dataForm.recommended" class="input" placeholder="推荐级别" clearable="clearable">
                    <el-option label="优先" value="true" />
                    <el-option label="非优先" value="false" />
                </el-select>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="searchHandle()">查询</el-button>
                <el-button type="primary" :disabled="!isAuth(['ROOT', 'MEDICAL_DEPT:INSERT'])" @click="addHandle()">
                    新增
                </el-button>
                <el-button type="danger" :disabled="!isAuth(['ROOT', 'MEDICAL_DEPT:DELETE'])" @click="deleteHandle()">
                    批量删除
                </el-button>
            </el-form-item>
        </el-form>
        <el-table
            :data="dataList"
            border
            v-loading="dataListLoading"
            @selection-change="selectionChangeHandle"
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
                prop="name"
                header-align="center"
                align="center"
                label="科室名称"
                min-width="170"
                :show-overflow-tooltip="true"
            />
            <el-table-column prop="outpatient" header-align="center" align="center" label="科室类型" min-width="120" />
            <el-table-column header-align="center" align="center" label="诊室数量" min-width="120">
                <template #default="scope">
                    <span>{{ scope.row.subs }}个</span>
                </template>
            </el-table-column>
            <el-table-column header-align="center" align="center" label="医生数量" min-width="120">
                <template #default="scope">
                    <span>{{ scope.row.doctors }}人</span>
                </template>
            </el-table-column>
            <el-table-column prop="recommended" header-align="center" align="center" label="优先推荐" min-width="120" />
            <el-table-column
                prop="description"
                header-align="center"
                align="left"
                label="科室介绍"
                min-width="430"
                :show-overflow-tooltip="true"
            />
            <el-table-column header-align="center" align="center" width="150" label="操作">
                <template #default="scope">
                    <el-button
                        type="text"
                        :disabled="!isAuth(['ROOT', 'MEDICAL_DEPT:UPDATE'])"
                        @click="updateHandle(scope.row.id)"
                    >
                        修改
                    </el-button>
                    <el-button
                        type="text"
                        :disabled="!isAuth(['ROOT', 'MEDICAL_DEPT:DELETE']) || scope.row.subs > 0"
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
import AddOrUpdate from './medical_dept-add-or-update.vue';
export default {
    components: {
        AddOrUpdate
    },
    data: function() {
        return {
            dataForm: {
                name: null,
                outpatient: null,
                recommended: null
            },
            dataList: [],
            pageIndex: 1,
            pageSize: 10,
            totalCount: 0,
            dataListLoading: false,
            dataListSelections: [],
            dataRule: {
                name: [{ required: false, pattern: '^[a-zA-Z0-9\u4e00-\u9fa5]{1,10}$', message: '部门名称格式错误' }]
            }
        };
    },
    methods: {
      loadDataList: function() {
          let that = this;
          that.dataListLoading = true;
          let data = {
              name: that.dataForm.name == '' ? null : that.dataForm.name,
              outpatient: that.dataForm.outpatient == '' ? null : that.dataForm.outpatient,
              recommended: that.dataForm.recommended == '' ? null : that.dataForm.recommended,
              page: that.pageIndex,
              length: that.pageSize
          };
      
          that.$http('/medical/dept/searchByPage', 'POST', data, true, function(resp) {
              let result = resp.result;
              for (let one of result.list) {
                  one.outpatient = one.outpatient ? '门诊' : '非门诊';
                  one.recommended = one.recommended ? '推荐' : '普通';
              }
              that.dataList = result.list;
              that.totalCount = result.totalCount;
              that.dataListLoading = false;
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
                    if (this.pageIndex != 1) {
                        this.pageIndex = 1;
                    }
                    this.loadDataList();
                } else {
                    return false;
                }
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
            if (row.subs > 0) {
                //含有隶属诊室，该行科室记录的复选框被禁用
                return false;
            }
            return true;
        },
        deleteHandle: function(id) {
            let that = this;
            let ids = id ? [id] : that.dataListSelections.map(item => 
                      {
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
                    that.$http('/medical/dept/deleteByIds', 'POST', { ids: ids }, true, function(resp) {
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
        this.loadDataList();
    }

};
</script>

<style></style>
