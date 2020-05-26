package com.onlineshop.diploma.repository;

import com.onlineshop.diploma.domain.security.Role;
import org.springframework.data.repository.CrudRepository;

public interface RoleRepository extends CrudRepository<Role, Long> {
    Role findByname(String name);
}
