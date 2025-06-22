package com.example.CapstoneProject.service.Interface;

import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;

public interface IImageService {
    @Transactional
    boolean sendVectorToAPI(String url, String primaryKey, String imageId, String imageUrl, List<Double> vector, boolean isInsert);

    @Transactional
    Map<String, List<Double>> getVectorsFromZilliz(List<String> primaryKeys);

    @Transactional
    boolean upsertVectorToImage(String imageId, List<Double> vector, boolean isUpdate);

    @Transactional
    boolean deleteAllVectorsFromZilliz();
}