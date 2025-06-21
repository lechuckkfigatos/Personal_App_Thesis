package org.lechuck.personal_app.Config;

import org.lechuck.personal_app.Entity.MyUserDetail;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    private final MyUserDetail userDetail;
    private final int userId;

    public CustomUserDetails(MyUserDetail userDetail, int userId) {
        this.userDetail = userDetail;
        this.userId = userId;
    }

    public int getUserId() {
        return userId;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return userDetail.getAuthorities();
    }

    @Override
    public String getPassword() {
        return userDetail.getPassword();
    }

    @Override
    public String getUsername() {
        return userDetail.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return userDetail.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return userDetail.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userDetail.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return userDetail.isEnabled();
    }
}