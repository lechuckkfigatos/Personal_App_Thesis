package org.lechuck.personal_app.Service;

import org.lechuck.personal_app.Entity.MyUserDetail;
import org.lechuck.personal_app.Entity.UserEntity;
import org.lechuck.personal_app.Repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {

        UserEntity user = userRepository.findByUserName(userName);

        if(user==null){
            System.out.println("User not Found");
            throw new UsernameNotFoundException("User not Found");
        }

        return new MyUserDetail(user);
    }
}
