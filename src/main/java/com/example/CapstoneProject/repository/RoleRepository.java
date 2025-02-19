package com.example.CapstoneProject.repository;
import com.example.CapstoneProject.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {
    Role findByName(String name);
}
