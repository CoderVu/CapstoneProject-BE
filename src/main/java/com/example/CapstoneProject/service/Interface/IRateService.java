package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.RateRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.PaginatedResponse;
import com.example.CapstoneProject.response.RateResponse;
import org.springframework.data.domain.Pageable;

public interface IRateService {

    APIResponse rateProduct(RateRequest request);

    PaginatedResponse<RateResponse> getRates(Pageable pageable, String productId);
}
