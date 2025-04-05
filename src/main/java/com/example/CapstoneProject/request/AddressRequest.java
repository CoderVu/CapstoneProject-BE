package com.example.CapstoneProject.request;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddressRequest {
    private String token;
    private String city;
    private String district;
    private String street;
    private String houseNumber;
    
}
