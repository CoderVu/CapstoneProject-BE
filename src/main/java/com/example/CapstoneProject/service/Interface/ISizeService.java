package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.SizeRequest;
import com.example.CapstoneProject.response.SizeResponse;

import java.util.List;

public interface ISizeService {
    List<SizeResponse> getAllSizes();

    boolean addSize(SizeRequest request);

    boolean updateSize(String id, SizeRequest request);

    boolean deleteSize(String id);
}
