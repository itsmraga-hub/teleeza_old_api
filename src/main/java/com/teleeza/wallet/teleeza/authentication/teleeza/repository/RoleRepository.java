package com.teleeza.wallet.teleeza.authentication.teleeza.repository;

import com.teleeza.wallet.teleeza.authentication.teleeza.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long> {
    Role findByName(String name);
}
