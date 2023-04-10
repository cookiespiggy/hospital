package com.example.hospital.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface VideoDiagnoseDao {
    public HashMap searchPaymentStatus(int id);
    public void closePayment(Map param);
    public HashMap searchVideoDiagnoseInfo(int diagnoseId);

    public void updateStatus(HashMap param);

    public void closeVideoDiagnose();

    public ArrayList<HashMap> searchStatisticsOfSevenDays(int doctorId);
    public ArrayList<HashMap> searchStatisticsOfYear(int doctorId);
}



