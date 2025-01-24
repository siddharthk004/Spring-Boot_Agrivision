package com.agri.vision.Service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agri.vision.Model.user;
import com.agri.vision.Repo.userRepo;

@Service
public class myAppUserService implements UserDetailsService {

    @Autowired
    private userRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        user userObj = repo.findByUsername(username);

        return org.springframework.security.core.userdetails.User.builder()
                .username(userObj.getUsername())
                .password(userObj.getPassword()) // Ensure this is hashed
                .roles("USER") // You can dynamically fetch roles if needed
                .build();
    }

}
