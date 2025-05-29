package com.example.CapstoneProject.service.Interface;

import com.example.CapstoneProject.request.AddressRequest;
import com.example.CapstoneProject.request.UserRequest;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.UserResponse;

import java.util.List;

public interface IUserService {

    JwtResponse getUserInfo(String token);

    UserResponse getUserInfoById(String id);

    UserResponse getUserInfoByToken(String token);

    List<UserResponse> getAllUser();

    APIResponse updateAddress(AddressRequest addressRequest);
    APIResponse deleteAddress(String token, Long addressId);

    APIResponse updateUserInfo(String token, UserRequest userRequest);

    APIResponse deleteUserAccount(String userId);
}
