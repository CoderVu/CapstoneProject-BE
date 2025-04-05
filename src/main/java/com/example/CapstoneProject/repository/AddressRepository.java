package com.example.CapstoneProject.repository;

import com.example.CapstoneProject.model.Address;
import com.example.CapstoneProject.model.User;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface AddressRepository extends JpaRepository<Address, Long> {
    @Query("SELECT a FROM Address a WHERE a.user = ?1")
    Optional<Address> findByUser(User foundUser);
    @Query("SELECT a FROM Address a WHERE a.user = ?1 AND a.city = ?2 AND a.district = ?3 AND a.street = ?4")
    Optional<Address> findByUserAndCityAndDistrictAndStreet(User foundUser, String city, String district,
            String street);
            @Query("SELECT a FROM Address a WHERE a.user = ?1 AND a.city = ?2 AND a.district = ?3 AND a.street = ?4 AND a.houseNumber = ?5")
            Optional<Address> findByUserAndCityAndDistrictAndStreetAndHouseNumber(User user, String city, String district, String street, String houseNumber);
            Optional<Address> findById(Long addressId);
}
