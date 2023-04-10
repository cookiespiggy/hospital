package com.example.hospital.api.service;

import java.util.ArrayList;
import java.util.HashMap;

public interface VideoDiagnoseService {
    public void online(int userId);
    public boolean offline(int userId);
    public void updateOpenFlag(int userId, boolean open);

    public HashMap searchVideoDiagnoseInfo(int userId);
    public HashMap refreshInfo(int userId);

    public ArrayList<String> searchImageByVideoDiagnoseId(int videoDiagnoseId);

    public HashMap searchMyStatistics(int userId);
}
