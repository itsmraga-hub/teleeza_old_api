package com.teleeza.wallet.teleeza.authentication.teleeza.security;


import com.teleeza.wallet.teleeza.authentication.teleeza.entity.Role;
import com.teleeza.wallet.teleeza.authentication.teleeza.entity.User;
import com.teleeza.wallet.teleeza.authentication.teleeza.repository.AuthRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private AuthRepository userRepository;
    private User user;

    public CustomUserDetailsService(AuthRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String phone) throws UsernameNotFoundException {
        User user = userRepository.findByPhone(phone)
                .orElseThrow(() ->
                        new UsernameNotFoundException("User not found with username or email:" + phone));
        return new org.springframework.security.core.userdetails.User(user.getPhone(),
                user.getPassword(), mapRolesToAuthorities(user.getRoles()));
    }

    private Collection<? extends GrantedAuthority> mapRolesToAuthorities(Set<Role> roles) {
        return roles.stream().map(role -> new SimpleGrantedAuthority(role.getName())).collect(Collectors.toList());
    }

//    @Override
//    public boolean isAccountNonLocked(){
//        return user.isAccountNonLocked();
//    }

//    public boolean isAccountNonLocked(){
//        return user.isAccountNonLocked();
//    }

}
