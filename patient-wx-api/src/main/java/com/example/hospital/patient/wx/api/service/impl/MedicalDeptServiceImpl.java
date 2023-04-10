package com.example.hospital.patient.wx.api.service.impl;

import cn.hutool.core.map.MapUtil;
import com.example.hospital.patient.wx.api.db.dao.MedicalDeptDao;
import com.example.hospital.patient.wx.api.service.MedicalDeptService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

@Service
public class MedicalDeptServiceImpl implements MedicalDeptService {
    @Resource
    private MedicalDeptDao medicalDeptDao;

    @Override
    public ArrayList<HashMap> searchMedicalDeptList(Map param) {
        ArrayList<HashMap> list = medicalDeptDao.searchMedicalDeptList(param);
        return list;
    }

    @Override
    public HashMap searchDeptAndSub() {
        //查询科室和诊室列表
        ArrayList<HashMap> list = medicalDeptDao.searchDeptAndSub();
        //LinkedHashMap可以记录数据添加的顺序
        LinkedHashMap map = new LinkedHashMap();

        for (HashMap one : list) {
            Integer deptId = MapUtil.getInt(one, "deptId");
            Integer subId = MapUtil.getInt(one, "subId");
            String deptName = MapUtil.getStr(one, "deptName");
            String subName = MapUtil.getStr(one, "subName");
            //map中是否含有当前这条记录的科室
            if (map.containsKey(deptName)) {
                //从map中取出诊室列表
                ArrayList<HashMap> subList = (ArrayList<HashMap>) map.get(deptName);
                //向诊室列表添加诊室记录
                subList.add(new HashMap() {{
                    put("subId", subId);
                    put("subName", subName);
                }});
            }
            //map中没有当前记录的科室
            else {
                //创建科室列表保存到map
                map.put(deptName, new ArrayList() {{
                    add(new HashMap() {{
                        put("subId", subId);
                        put("subName", subName);
                    }});
                }});
            }
        }
        return map;
    }
}
