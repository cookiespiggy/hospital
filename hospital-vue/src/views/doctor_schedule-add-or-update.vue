<template>
    <el-dialog
        :title="dataForm.workPlanId==null?'新增':'修改'"
        v-if="isAuth(['ROOT', 'SCHEDULE:INSERT', 'SCHEDULE:UPDATE'])"
        :close-on-click-modal="false"
        v-model="visible"
        width="550px"
    >
        <el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
            <el-form-item label="出诊医生" prop="doctorId">
                <el-select v-model="dataForm.doctorId" :disabled="dataForm.workPlanId != null">
                    <el-option v-for="one in doctorList" :label="one.name" :value="one.id"></el-option>
                </el-select>
            </el-form-item>
            <el-form-item label="出诊时间">
                <div style="width: 100%;">
                    <el-checkbox v-model="checkAll" @change="checkAllChangeHandle">全选</el-checkbox>
                </div>
                <div style="width: 100%;">
                    <el-checkbox-group v-model="checkedSlot">
                        <el-checkbox
                            v-for="(one, index) in slotList"
                            :label="one"
                            :disabled="analyseCheckBoxDisable(index + 1)"
                        />
                    </el-checkbox-group>
                </div>
            </el-form-item>
            <el-form-item label="时段人数">
                <el-slider
                    v-model="dataForm.slotMaximum"
                    :min="1"
                    :max="10"
                    show-input
                    :disabled="dataForm.workPlanId != null"
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
import dayjs from 'dayjs';
import { ElMessage } from 'element-plus';
export default {
    data: function() {
        return {
            visible: false,
            doctorList: [],
            checkAll: false,
            slotList: [
                '08:00~08:30',
                '08:30~09:00',
                '09:00~09:30',
                '09:30~10:00',
                '10:00~10:30',
                '10:30~11:00',
                '11:00~11:30',
                '11:30~12:00',
                '13:00~13:30',
                '13:30~14:00',
                '14:00~14:30',
                '14:30~15:00',
                '15:00~15:30',
                '16:00~16:30',
                '16:30~17:00'
            ],
            checkedSlot: [],
            oldSlots: [],
            dataForm: {
                workPlanId: null,
                deptSubId: null,
                doctorId: null,
                date: new dayjs().format('YYYY-MM-DD'),
                slots: [],
                slotMaximum: 3
            },
            dataRule: {
                doctorId: [
                    {
                        required: true,
                        message: '出诊医生不能为空'
                    }
                ]
            }
        };
    },
    methods: {
      loadDoctorList: function() {
          let that = this;
          let data = {
              deptSubId: that.dataForm.deptSubId
          };
          that.$http('/doctor/searchByDeptSubId', 'POST', data, true, function(resp) {
              that.doctorList = resp.result;
          });
      },
      reset: function() {
          this.checkAll = false;
          this.checkedSlot = [];
          this.oldSlots = [];
          let dataForm = {
              deptSubId: null,
              doctorId: null,
              date: new dayjs().format('YYYY-MM-DD'),
              slots: [],
              slotMaximum: 3
          };
          this.dataForm = dataForm;
      },
      init: function(workPlanId, deptSubId, date) {
          let that = this;
          that.reset();
          that.dataForm.workPlanId = workPlanId;
          that.dataForm.deptSubId = deptSubId;
          that.dataForm.date = date;
          that.visible = true;
          that.$nextTick(() => {
              that.$refs['dataForm'].resetFields();
              that.loadDoctorList();
              if (workPlanId != null) {
                  //TODO 加载出诊计划
                let data = {workPlanId: workPlanId};
                that.$http('/doctor/work_plan/schedule/searchByWorkPlanId', 'POST', data, true, function(resp) {
                           let result = resp.result;
                           that.dataForm.doctorId = result.doctorId;
                           that.dataForm.slotMaximum = result.maximum;
                           that.oldSlots = result.slots;
                           for (let one of result.slots) {
                                  let slot = that.slotList[one.slot - 1];
                                  that.checkedSlot.push(slot);
                           }
                 });
              }
          });
      },
      checkAllChangeHandle: function(val) {
          this.checkedSlot = val ? this.slotList : [];
      },
      analyseCheckBoxDisable: function(slot) {
      },
      dataFormSubmit: function() {
          let that = this;
          that.$refs['dataForm'].validate(function(valid) {
              if (valid) {
                  if (that.checkedSlot.length == 0) {
                      ElMessage({
                          message: '出诊时间段没有选择',
                          type: 'warning'
                      });
                      return;
                  }
                  //新增数据
                  if (that.dataForm.workPlanId == null) {
                      //把选中的时段转换成具体编号
                      that.dataForm.slots.length = 0;
                      for (let one of that.checkedSlot) {
                          let index = that.slotList.indexOf(one) + 1;
                          that.dataForm.slots.push(index);
                      }
                      let data = {
                          deptSubId: that.dataForm.deptSubId,
                          doctorId: that.dataForm.doctorId,
                          date: that.dataForm.date,
                          slotMaximum: that.dataForm.slotMaximum,
                          totalMaximum: that.checkedSlot.length * that.dataForm.slotMaximum,
                          slots: that.dataForm.slots
                      };
                      that.$http('/medical/dept/sub/work_plan/insert', 'POST', data, true, function(resp) {
                          let result = resp.result;
                          if (result == '') {
                              ElMessage({
                                  message: '操作成功',
                                  type: 'success',
                                  duration: 1200
                              });
                              that.visible = false;
                              that.$emit('refreshDataList');
                          } else {
                              ElMessage({
                                  message: result,
                                  type: 'warning',
                                  duration: 1200
                              });
                          }
                      });
                  }
                  else {
                                  that.dataForm.slots.length = 0;
                                  //把选中的时间段转换成编号
                                  for (let one of that.checkedSlot) {
                                      let index = that.slotList.indexOf(one) + 1;
                                      that.dataForm.slots.push(index);
                                  }
                                  let array = [];
                                  //用新的时段与老的时段比较，哪些时段是新增的选中时段
                                  for (let one of that.dataForm.slots) {
                                      let temp = that.oldSlots.find(function(old) {
                                          return old.slot == one;
                                      });
                                      //判断是不是新增的时段
                                      if (typeof temp == 'undefined') {
                                          array.push({
                                              scheduleId: null,
                                              slot: one,
                                              maximum: that.dataForm.slotMaximum,
                                              operate: 'insert'
                                          });
                                      }
                                  }
                  
                                  //用老的时段与新的时段比较，哪些时段是要删除的
                                  for (let old of that.oldSlots) {
                                      let temp = that.dataForm.slots.find(function(one) {
                                          return old.slot == one;
                                      });
                                      //判断是不是删除的时段
                                      if (typeof temp == 'undefined') {
                                          array.push({
                                              scheduleId: old.scheduleId,
                                              slot: old.slot,
                                              maximum: that.dataForm.slotMaximum,
                                              operate: 'delete'
                                          });
                                      }
                                  }
                                  //如果既没有新增时段也没有取消时段，就弹出提示信息，没必要发起修改请求
                                  if (array.length == 0) {
                                      ElMessage({
                                          message: '请改动出诊日程',
                                          type: 'warning',
                                          duration: 1200
                                      });
                                      return;
                                  }
                                  let data = {
                                      workPlanId: that.dataForm.workPlanId,
                                      maximum: that.checkedSlot.length * that.dataForm.slotMaximum,
                                      slots: array
                                  };
                                  that.$http('/doctor/work_plan/schedule/updateSchedule', 'POST', data, true, function(resp) {
                                      ElMessage({
                                          message: '操作成功',
                                          type: 'success'
                                      });
                                      that.visible = false;
                                      that.$emit('refreshDataList');
                                  });
                              }
                          }
                      });
                  },
        analyseCheckBoxDisable: function(slot) {
            let temp = this.oldSlots.find(function(one) {
                //筛选某个时段的实际挂号患者数量是否大于0
                return one.slot == slot && one.num > 0;
            });
            //禁用患者挂号数量大于0的时间段复选框
            return typeof temp != 'undefined';
        },

    }
};
</script>

<style lang="less" scoped="scoped"></style>
