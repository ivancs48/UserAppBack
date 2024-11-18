package com.springboot.calderon.userapp.repositories;

import java.util.Optional;

import org.springframework.data.repository.CrudRepository;

import com.springboot.calderon.userapp.entities.Roles;

public interface RoleRepository extends CrudRepository<Roles, Long> {
	
	Optional<Roles> findByName(String name);
	
}
