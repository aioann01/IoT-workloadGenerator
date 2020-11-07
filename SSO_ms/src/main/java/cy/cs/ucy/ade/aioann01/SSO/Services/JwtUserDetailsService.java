package cy.cs.ucy.ade.aioann01.SSO.Services;


import java.util.ArrayList;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class JwtUserDetailsService implements UserDetailsService {

    @Value("${jwt.username}")
    private String username;

    @Value("${jwt.password}")
    private String password;

    private String bkryptUserPassword(String password){
        return bCryptPasswordEncoder.encode(password);
    }

    private BCryptPasswordEncoder bCryptPasswordEncoder=new BCryptPasswordEncoder();
    @Override
    public UserDetails loadUserByUsername(String tokenUsername) throws UsernameNotFoundException {
        String encryptedPassword=bkryptUserPassword(password);
        if (username.equals(tokenUsername)) {
            return new User(username,encryptedPassword ,
                    new ArrayList<>());
        } else {
            throw new UsernameNotFoundException("User not found with username: " + tokenUsername);
        }
    }

}