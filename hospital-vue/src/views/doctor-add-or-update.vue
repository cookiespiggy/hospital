<template>
    <el-dialog
        :title="!dataForm.id ? '新增' : '修改'"
        v-if="isAuth(['ROOT', 'DOCTOR:INSERT', 'DOCTOR:UPDATE'])"
        :close-on-click-modal="false"
        v-model="visible"
        width="480px"
    >
        <el-scrollbar height="500px">
            <el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
                <el-form-item label="姓名" prop="name"><el-input v-model="dataForm.name" clearable /></el-form-item>
                <el-form-item label="身份证号" prop="pid"><el-input v-model="dataForm.pid" clearable /></el-form-item>
                <el-form-item label="性别">
                    <el-radio-group v-model="dataForm.sex">
                        <el-radio-button label="男" />
                        <el-radio-button label="女" />
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="出生日期" prop="birthday">
                    <el-date-picker
                        v-model="dataForm.birthday"
                        type="date"
                        placeholder="选择日期"
                        :editable="false"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%;"
                    />
                </el-form-item>
                <el-form-item label="毕业学校" prop="school">
                    <el-input v-model="dataForm.school" maxlength="50" clearable />
                </el-form-item>
                <el-form-item label="学历">
                    <el-radio-group v-model="dataForm.degree">
                        <el-radio-button label="博士" />
                        <el-radio-button label="研究生" />
                        <el-radio-button label="本科" />
                    </el-radio-group>
                </el-form-item>
                <el-form-item label="电话" prop="tel"><el-input v-model="dataForm.tel" clearable /></el-form-item>
                <el-form-item label="家庭住址" prop="address">
                    <el-input v-model="dataForm.address" maxlength="200" clearable />
                </el-form-item>
                <el-form-item label="电子信箱" prop="email">
                    <el-input v-model="dataForm.email" clearable />
                </el-form-item>
                <el-form-item label="职务" prop="job">
                    <el-select v-model="dataForm.job" clearable>
                        <el-option label="主任医师" value="主任医师"></el-option>
                        <el-option label="副主任医师" value="副主任医师"></el-option>
                        <el-option label="主治医师" value="主治医师"></el-option>
                        <el-option label="副主治医师" value="副主治医师"></el-option>
                    </el-select>
                </el-form-item>
                <el-form-item label="科室部门" prop="deptSub">
                    <el-cascader v-model="dataForm.deptSub" :options="dept" clearable />
                </el-form-item>
                <el-form-item label="备注信息" prop="remark">
                    <el-input v-model="dataForm.remark" maxlength="50" clearable />
                </el-form-item>
                <el-form-item label="医师介绍" prop="description">
                    <el-input
                        v-model="dataForm.description"
                        type="textarea"
                        :rows="6"
                        style="width:100%"
                        maxlength="350"
                        show-word-limit
                        clearable
                    />
                </el-form-item>
                <el-form-item label="入职日期" prop="hiredate">
                    <el-date-picker
                        v-model="dataForm.hiredate"
                        type="date"
                        placeholder="选择日期"
                        :editable="false"
                        format="YYYY-MM-DD"
                        value-format="YYYY-MM-DD"
                        style="width: 100%;"
                    />
                </el-form-item>
                <el-form-item label="标签">
                    <el-input v-model="newTag" @keyup.enter="inputTagHandle" clearable />
                    <p>
                        <el-tag
                            v-for="one in dataForm.tag"
                            closable
                            :disable-transitions="false"
                            @close="closeTagHandle(one)"
                        >
                            {{ one }}
                        </el-tag>
                    </p>
                </el-form-item>
                <el-form-item label="推荐等级">
                    <el-radio-group v-model="dataForm.recommended">
                        <el-radio-button label="推荐" />
                        <el-radio-button label="普通" />
                    </el-radio-group>
                </el-form-item>

                <el-form-item label="状态">
                    <el-radio-group v-model="dataForm.status">
                        <el-radio-button label="在职" />
                        <el-radio-button label="离职" />
                        <el-radio-button label="退休" />
                    </el-radio-group>
                </el-form-item>
            </el-form>
        </el-scrollbar>
        <template #footer>
            <span class="dialog-footer">
                <el-button @click="visible = false">取消</el-button>
                <el-button type="primary" @click="dataFormSubmit">确定</el-button>
            </span>
        </template>
    </el-dialog>
</template>

<script>
import dayjs from 'dayjs';
export default {
    data: function() {
        return {
            visible: false,
            newTag: null,
            dept: [],

            dataForm: {
                id: null,
                name: null,
                pid: null,
                sex: '男',
                photo: null,
                birthday: null,
                school: null,
                degree: '博士',
                tel: null,
                address: null,
                email: null,
                job: null,
                deptSub: null,
                deptSubId: null,
                remark: null,
                description: null,
                hiredate: null,
                tag: [],
                recommended: '普通',
                status: '在职'
            },
            dataRule: {
                name: [
                    { required: true, message: '姓名不能为空' },
                    {
                        pattern: '^[\\u4e00-\\u9fa5]{2,20}$',
                        message: '姓名格式错误'
                    }
                ],
                pid: [
                    {
                        required: true,
                        message: '身份证号不能为空'
                    },
                    {
                        pattern:
                            '^[1-9]\\d{5}(18|19|([23]\\d))\\d{2}((0[1-9])|(10|11|12))(([0-2][1-9])|10|20|30|31)\\d{3}[0-9Xx]$',
                        message: '身份证号不正确'
                    }
                ],
                birthday: [
                    {
                        required: true,
                        message: '出生日期不能为空'
                    }
                ],
                school: [
                    {
                        required: true,
                        message: '毕业学校不能为空'
                    }
                ],
                tel: [
                    { required: true, message: '电话不能为空' },
                    {
                        pattern: '^1[1-9][0-9]{9}$',
                        message: '电话格式错误'
                    }
                ],
                address: [
                    {
                        required: true,
                        message: '家庭住址不能为空'
                    }
                ],
                email: [
                    {
                        required: true,
                        message: '电子信箱不能为空'
                    },
                    {
                        pattern: '^([a-zA-Z]|[0-9])(\\w|\\-)+@[a-zA-Z0-9]+\\.([a-zA-Z]{2,4})$',
                        message: '电子信箱格式错误'
                    }
                ],
                job: [
                    {
                        required: true,
                        message: '职务不能为空'
                    }
                ],
                deptSub: [
                    {
                        required: true,
                        message: '科室部门不能为空'
                    }
                ],
                remark: [
                    {
                        required: true,
                        message: '备注信息不能为空'
                    }
                ],
                description: [
                    {
                        required: true,
                        message: '医师介绍不能为空'
                    }
                ],
                hiredate: [{ required: true, message: '入职日期不能为空' }]
            }
        };
    },
    methods: {
        loadDeptAndSub: function() {
            let that = this;
            that.$http('/medical/dept/searchDeptAndSub', 'GET', {}, false, function(resp) {
                let result = resp.result;
                let dept = [];
                for (let one in result) {
                    let array = [];
                    for (let sub of result[one]) {
                        array.push({
                            value: sub.subId,
                            label: sub.subName
                        });
                    }
                    dept.push({
                        value: one,
                        label: one,
                        children: array
                    });
                }
                that.dept = dept;
            });
        },
        reset: function() {
            let dataForm = {
                id: null,
                name: null,
                pid: null,
                sex: '男',
                photo: null,
                birthday: null,
                school: null,
                degree: '博士',
                tel: null,
                address: null,
                email: null,
                job: null,
                deptSub: null,
                deptSubId: null,
                remark: null,
                description: null,
                hiredate: null,
                tag: [],
                recommended: '普通',
                status: '在职'
            };   
            this.dataForm = dataForm;
            this.newTag = null;
        },
        init: function(id) {
            let that = this;
            //重置表单控件
            that.reset();
            //如果id是undefined，就对模型层id变量赋值为0
            that.dataForm.id = id || 0;
            //显示对话框
            that.visible = true;
          
            //DOM渲染操作要放在$nextTick函数中执行，例如加载数据
            that.$nextTick(() => {
                //清理前端验证结果
                that.$refs['dataForm'].resetFields();
                //加载二级列表数据
                that.loadDeptAndSub();
                
                
               //加载医生详情信息 
              if (that.dataForm.id) {
                          that.$http('/doctor/searchById', 'POST', { id: id }, true, function(resp) {
                              let json = {
                                  '1': '在职',
                                  '2': '离职',
                                  '3': '退休'
                              };
                              that.dataForm.name = resp.name;
                              that.dataForm.pid = resp.pid;
                              that.dataForm.sex = resp.sex;
                              that.dataForm.birthday = resp.birthday;
                              that.dataForm.school = resp.school;
                              that.dataForm.degree = resp.degree;
                              that.dataForm.tel = resp.tel;
                              that.dataForm.address = resp.address;
                              that.dataForm.email = resp.email;
                              that.dataForm.job = resp.job;
                              that.dataForm.remark = resp.remark;
                              that.dataForm.description = resp.description;
                              that.dataForm.hiredate = resp.hiredate;
                              that.dataForm.recommended = resp.recommended ? '推荐' : '不推荐';
                              that.dataForm.tag = resp.tag;
                              that.dataForm.status = json[resp.status + ''];
                              that.dataForm.deptSub = [resp.deptName, resp.deptSubId];
                          });
                      }
                  });
              },
        inputTagHandle: function() {
            if (this.newTag != null && this.newTag != '') {
                if (this.dataForm.tag.includes(this.newTag)) {
                    ElMessage({
                        message: '不能添加重复标签',
                        type: 'warning',
                        duration: 1200
                    });
                } else {
                    this.dataForm.tag.push(this.newTag);
                    this.newTag = null;
                }
            }
        },
        closeTagHandle: function(tag) {
            let i = this.dataForm.tag.indexOf(tag);
            this.dataForm.tag.splice(i, 1);
        },
        dataFormSubmit: function() {
            let that = this;
            that.$refs['dataForm'].validate(function(valid) {
                if (valid) {
                    that.dataForm.deptSubId = that.dataForm.deptSub[1];
                    let json = {
                        在职: 1,
                        离职: 2,
                        退休: 3
                    };
                    let data = {
                        id: that.dataForm.id,
                        name: that.dataForm.name,
                        pid: that.dataForm.pid,
                        sex: that.dataForm.sex,
                        birthday: that.dataForm.birthday,
                        school: that.dataForm.school,
                        degree: that.dataForm.degree,
                        tel: that.dataForm.tel,
                        address: that.dataForm.address,
                        email: that.dataForm.email,
                        job: that.dataForm.job,
                        remark: that.dataForm.remark,
                        description: that.dataForm.description,
                        hiredate: dayjs(that.dataForm.hiredate).format('YYYY-MM-DD'),
                        tag: that.dataForm.tag,
                        recommended: that.dataForm.recommended == '推荐' ? 1 : 2,
                        status: json[that.dataForm.status],
                        subId: that.dataForm.deptSubId
                    };
                    that.$http(`/doctor/${!that.dataForm.id ? 'insert' : 'update'}`, 'POST', data, true, function(
                        resp
                    ) {
                        ElMessage({
                            message: '操作成功',
                            type: 'success'
                        });
                        that.visible = false;
                        that.$emit('refreshDataList');
                    });
                }
            });
        }



    }
};
</script>

<style lang="less" scoped="scoped"></style>
