package com.example.hospital.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface MisUserDao {

    public Integer login(Map param);


    public ArrayList<String> searchUserPermissions(int userId);

    public HashMap searchRefId(int id);

    public Integer searchUserId(Map param);
}



