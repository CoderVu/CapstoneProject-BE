package com.example.CapstoneProject.security.user;

import com.example.CapstoneProject.model.User;
import com.example.CapstoneProject.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ShopUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    public ShopUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String identifier) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByUsernameOrSubOrFacebookId(identifier, identifier, identifier);
        if (user.isPresent()) {
            return ShopUserDetails.buildUserDetails(user.get());
        } else {
            throw new UsernameNotFoundException("User Not Found with identifier: " + identifier);
        }
    }
}