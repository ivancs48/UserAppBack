package com.springboot.calderon.userapp.services;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.lang.NonNull;

import com.springboot.calderon.userapp.entities.User;
import com.springboot.calderon.userapp.models.UserDTO;

public interface UserService {

    List<User> findAll();
    
    Page<User> findAll(Pageable pageable);

    Optional<User> findById(@NonNull Long id);

    User save(User user);

    Optional<User> update(UserDTO user, Long id);
    
    void deleteById(Long id);
}
