package com.example.CapstoneProject.service.Implement;

import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.AddressRepository;
import com.example.CapstoneProject.repository.RateRepository;
import com.example.CapstoneProject.repository.UserRepository;
import com.example.CapstoneProject.request.AddressRequest;
import com.example.CapstoneProject.response.JwtResponse;
import com.example.CapstoneProject.response.RoleResponse;
import com.example.CapstoneProject.response.UserResponse;
import com.example.CapstoneProject.response.APIResponse;
import com.example.CapstoneProject.response.AddressResponse;
import com.example.CapstoneProject.security.jwt.JwtUtils;
import com.example.CapstoneProject.service.Interface.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.CapstoneProject.StatusCode.Code;
import com.example.CapstoneProject.model.Address;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService implements IUserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtUtils jwtUtils;
    @Autowired
    private RateRepository rateRepository;
    @Autowired
    private AddressRepository addressRepository;

    @Override
    public JwtResponse getUserInfo(String token) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return null;
        }

        return JwtResponse.builder()
                .id(user.get().getId())
                .fullName(user.get().getFullName())
                .email(user.get().getEmail())
                .phoneNumber(user.get().getPhoneNumber())
                .address(user.get().getAddress())
                .avatar(user.get().getAvatar())
                .methodLogin(user.get().getMethodLogin())
                .role(RoleResponse.builder()
                        .id(user.get().getRole().getId())
                        .name(user.get().getRole().getName())
                        .build())
                .build();
    }

    @Override
    public UserResponse getUserInfoByToken(String token) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return null;
        }
        List<AddressResponse> address = user.get().getAddresses().stream()
                .map((Address addressEntity) -> AddressResponse.builder()
                        .id(addressEntity.getId())
                        .city(addressEntity.getCity())
                        .district(addressEntity.getDistrict())
                        .street(addressEntity.getStreet())
                        .houseNumber(addressEntity.getHouseNumber())
                        .build())
                .collect(Collectors.toList());
        return UserResponse.builder()
                .id(user.get().getId())
                .fullName(user.get().getFullName())
                .email(user.get().getEmail())
                .phoneNumber(user.get().getPhoneNumber())
                .address(user.get().getAddress())
                .avatar(user.get().getAvatar())
                .methodLogin(user.get().getMethodLogin())
                .role(RoleResponse.builder()
                        .id(user.get().getRole().getId())
                        .name(user.get().getRole().getName())
                        .build())
                .addressList(address)
                .build();
    }

    @Override
    public UserResponse getUserInfoById(String id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isEmpty()) {
            return null;
        }
        List<AddressResponse> address = user.get().getAddresses().stream()
                .map((Address addressEntity) -> AddressResponse.builder()
                        .id(addressEntity.getId())
                        .city(addressEntity.getCity())
                        .district(addressEntity.getDistrict())
                        .build())
                .collect(Collectors.toList());
        return UserResponse.builder()
                .id(user.get().getId())
                .fullName(user.get().getFullName())
                .email(user.get().getEmail())
                .phoneNumber(user.get().getPhoneNumber())
                .address(user.get().getAddress())
                .avatar(user.get().getAvatar())
                .methodLogin(user.get().getMethodLogin())
                .role(RoleResponse.builder()
                        .id(user.get().getRole().getId())
                        .name(user.get().getRole().getName())
                        .build())
                .addressList(address)
                .build();
    }

    @Override
    public List<UserResponse> getAllUser() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> UserResponse.builder()
                .id(user.getId())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .phoneNumber(user.getPhoneNumber())
                .address(user.getAddress())
                .avatar(user.getAvatar())
                .methodLogin(user.getMethodLogin())
                .role(RoleResponse.builder()
                        .id(user.getRole().getId())
                        .name(user.getRole().getName())
                        .build())
                .build()).collect(Collectors.toList());
    }

    @Override
public APIResponse updateAddress(AddressRequest addressRequest) {
    String token = addressRequest.getToken();
    String identifier = jwtUtils.getUserFromToken(token);
    Optional<User> user = Optional.empty();
    if (identifier != null) {
        user = userRepository.findByPhoneNumber(identifier);
        if (user.isEmpty()) {
            user = userRepository.findByEmail(identifier);
        }
    }
    if (user.isEmpty()) {
        return APIResponse.builder()
                .statusCode(Code.NOT_FOUND.getCode())
                .message("User not found")
                .build();
    }
    User foundUser = user.get();

    // Check if the user already has an address with the same city, district, and street
    Optional<Address> existingAddress = addressRepository.findByUserAndCityAndDistrictAndStreet(
            foundUser, addressRequest.getCity(), addressRequest.getDistrict(), addressRequest.getStreet());

    Address address;
    if (existingAddress.isPresent()) {
        // Delete the old address
        addressRepository.delete(existingAddress.get());

        // Create a new address with updated details
        address = new Address();
        address.setCity(addressRequest.getCity());
        address.setDistrict(addressRequest.getDistrict());
        address.setStreet(addressRequest.getStreet());
        address.setHouseNumber(addressRequest.getHouseNumber());
        address.setUser(foundUser);
    } else {
        // Create a new address
        address = new Address();
        address.setCity(addressRequest.getCity());
        address.setDistrict(addressRequest.getDistrict());
        address.setStreet(addressRequest.getStreet());
        address.setHouseNumber(addressRequest.getHouseNumber());
        address.setUser(foundUser);
    }

    addressRepository.save(address);
    return APIResponse.builder()
            .statusCode(Code.OK.getCode())
            .message("Address updated successfully")
            .build();
}

    @Override
    public APIResponse deleteAddress(String token, Long addressId) {
        String identifier = jwtUtils.getUserFromToken(token);
        Optional<User> user = Optional.empty();
        if (identifier != null) {
            user = userRepository.findByPhoneNumber(identifier);
            if (user.isEmpty()) {
                user = userRepository.findByEmail(identifier);
            }
        }
        if (user.isEmpty()) {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("User not found")
                    .build();
        }
        Optional<Address> address = addressRepository.findById(addressId);
        if (address.isPresent()) {
            addressRepository.delete(address.get());
            return APIResponse.builder()
                    .statusCode(Code.OK.getCode())
                    .message("Address deleted successfully")
                    .build();
        } else {
            return APIResponse.builder()
                    .statusCode(Code.NOT_FOUND.getCode())
                    .message("Address not found")
                    .build();
        }
    }

}