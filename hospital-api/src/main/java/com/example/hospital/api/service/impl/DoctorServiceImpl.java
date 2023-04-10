package com.example.hospital.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.hospital.api.common.PageUtils;
import com.example.hospital.api.db.dao.DoctorDao;
import com.example.hospital.api.db.dao.MedicalDeptSubAndDoctorDao;
import com.example.hospital.api.db.pojo.DoctorEntity;
import com.example.hospital.api.db.pojo.MedicalDeptSubAndDoctorEntity;
import com.example.hospital.api.exception.HospitalException;
import com.example.hospital.api.service.DoctorService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
public class DoctorServiceImpl implements DoctorService {
    @Resource
    private DoctorDao doctorDao;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Resource
    private MedicalDeptSubAndDoctorDao medicalDeptSubAndDoctorDao;

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = doctorDao.searchCount(param);
        if (count > 0) {
            list = doctorDao.searchByPage(param);
        } else {
            list = new ArrayList<>();
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    public HashMap searchContent(int id) {
        HashMap map = doctorDao.searchContent(id);
        JSONArray tag = JSONUtil.parseArray(MapUtil.getStr(map, "tag"));
        map.replace("tag", tag);
        return map;
    }

    @Override
    @Transactional
    public void updatePhoto(MultipartFile file, Integer doctorId) {
        try {
            String filename = "doctor-" + doctorId + ".jpg";
            //在Minio中保存医生照片
            MinioClient client = new MinioClient.Builder().endpoint(endpoint)
                    .credentials(accessKey, secretKey).build();

            client.putObject(PutObjectArgs.builder().bucket("hospital")
                    .object("doctor/" + filename)
                    .stream(file.getInputStream(), -1, 5 * 1024 * 1024)
                    .contentType("image/jpeg").build());

            //更新医生表photo字段
            doctorDao.updatePhoto(new HashMap() {{
                put("id", doctorId);
                put("photo", "/doctor/" + filename);
            }});
        } catch (Exception e) {
            log.error("保存医生照片失败", e);
            throw new HospitalException("保存医生照片失败");
        }
    }

    @Override
    public void insert(Map param) {
        //保存医生记录
        DoctorEntity entity_1 = BeanUtil.toBean(param, DoctorEntity.class);
        doctorDao.insert(entity_1);

        //根据uuid查询医生主键值
        String uuid = entity_1.getUuid();
        Integer doctorId = doctorDao.searchIdByUuid(uuid);

        //保存医生诊室记录
        int subId = MapUtil.getInt(param, "subId");
        MedicalDeptSubAndDoctorEntity entity_2 = new MedicalDeptSubAndDoctorEntity();
        entity_2.setDeptSubId(subId);
        entity_2.setDoctorId(doctorId);
        medicalDeptSubAndDoctorDao.insert(entity_2);
    }

    @Override
    public HashMap searchById(int id) {
        HashMap map = doctorDao.searchById(id);
        String tag = MapUtil.getStr(map, "tag");
        JSONArray array = JSONUtil.parseArray(tag);
        map.replace("tag", array);
        return map;
    }

    @Override
    @Transactional
    public void update(Map param) {
        doctorDao.update(param);
        param = MapUtil.renameKey(param, "id", "doctorId");
        medicalDeptSubAndDoctorDao.updateDoctorSubDept(param);
    }

    @Override
    @Transactional
    public void deleteByIds(Integer[] ids) {
        doctorDao.deleteByIds(ids);
    }

    @Override
    public ArrayList<HashMap> searchByDeptSubId(int deptSubId) {
        ArrayList<HashMap> list = doctorDao.searchByDeptSubId(deptSubId);
        return list;
    }

}
