package sol.funny.restbackend.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sol.funny.datacore.entity.domain.Client;
import sol.funny.datacore.repository.ClientRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Autowired
    ClientRepository clientRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Client c = clientRepository.getClientByUserNameAndStatus(username, "A");

        if(c != null){
            return new User(c.getUserName(),c.getPassword(), getAuthorities(username) );
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String username) {
        List<GrantedAuthority> authorities = new ArrayList<>();

        List<String> roles = clientRepository.getClientRolesByUserNameAndStatus(username, "A");
        for (String role: roles) {
            authorities.add(new SimpleGrantedAuthority(role));
        }

        return authorities;
    }
}
