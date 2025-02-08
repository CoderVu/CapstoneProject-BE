package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.RateRequest;
import com.example.CapstoneProject.response.APIResponse;

public interface IRateService {

    APIResponse rateProduct(RateRequest request);
}
