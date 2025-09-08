package com.SpringSecurity.Security;

import java.util.HashSet;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.SpringSecurity.Entity.Role;
import com.SpringSecurity.Entity.User;
import com.SpringSecurity.Repository.UserRepo;

public class CustomUserDetailService implements UserDetailsService {

	@Autowired
	private UserRepo userRepo;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		// TODO Auto-generated method stub

		User user = userRepo.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("user not found"));
		Role role = user.getRole();

		Set<GrantedAuthority> authorities = new HashSet<>();
		SimpleGrantedAuthority authority = new SimpleGrantedAuthority(role.getRoleName());
		authorities.add(authority);

		return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(),
				authorities);

	}

}
