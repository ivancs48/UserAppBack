package com.springboot.calderon.userapp.repositories;

import org.springframework.data.repository.CrudRepository;

import com.springboot.calderon.userapp.entities.User;

public interface UserRepository extends CrudRepository<User, Long>{

}
