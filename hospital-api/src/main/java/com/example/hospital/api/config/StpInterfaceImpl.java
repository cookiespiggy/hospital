package com.example.hospital.api.config;

import cn.dev33.satoken.stp.StpInterface;
import com.example.hospital.api.db.dao.MisUserDao;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Resource
    private MisUserDao userDao;

    /**
     * 返回一个用户所拥有的权限集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginKey) {
        int userId = Integer.parseInt(loginId.toString());
        ArrayList<String> list = userDao.searchUserPermissions(userId);
        return list;
    }


    /**
     * 返回一个用户所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        return null;
    }

}