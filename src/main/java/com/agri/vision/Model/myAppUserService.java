package com.agri.vision.Model;

import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.agri.vision.Repo.userRepo;

@Service
public class myAppUserService implements UserDetailsService {

    @Autowired
    private userRepo repo;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<user> Myuser = repo.findByUsername(username);
        if (Myuser.isPresent()) {
            user userObj = Myuser.get();
            return org.springframework.security.core.userdetails.User.builder()
                    .username(userObj.getUsername())
                    .password(userObj.getPassword()) // Ensure this is the hashed password
                    .roles("USER") // Assign roles
                    .build();
        } else {
            throw new UsernameNotFoundException("User not found");
        }
    }

}
