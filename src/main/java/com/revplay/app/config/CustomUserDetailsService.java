package com.revplay.app.config;

import com.revplay.app.entity.ArtistAccount;
import com.revplay.app.entity.UserAccount;
import com.revplay.app.repository.IArtistRepository;
import com.revplay.app.repository.IUserAccountRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private IUserAccountRepository userAccountRepository;

    @Autowired
    private IArtistRepository artistRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // Try loading as user first
        UserAccount user = userAccountRepository.findByEmail(email).orElse(null);
        if (user != null) {
            return new User(user.getEmail(), user.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_USER")));
        }

        // Then try as artist
        ArtistAccount artist = artistRepository.findByEmail(email).orElse(null);
        if (artist != null) {
            return new User(artist.getEmail(), artist.getPasswordHash(),
                    Collections.singletonList(new SimpleGrantedAuthority("ROLE_ARTIST")));
        }

        throw new UsernameNotFoundException("User not found with email: " + email);
    }
}
