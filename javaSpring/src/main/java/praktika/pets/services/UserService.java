package praktika.pets.services;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import praktika.pets.model.AppUser;
import praktika.pets.model.Role;
import praktika.pets.repositories.RoleRepository;
import praktika.pets.repositories.UserRepository;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Service
//@RequiredArgsConstructor
@Transactional
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Autowired
    public UserService(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = userRepository.findByUsername(username);

        if (user == null) {
            throw new UsernameNotFoundException("User not found in database");
        }

        Collection<SimpleGrantedAuthority> authorities = new ArrayList<>();
        user.getRoles().forEach(role -> {
            authorities.add(new SimpleGrantedAuthority(role.getName()));

        });

        return new org.springframework.security.core.userdetails.User(user.getUsername(), user.getPassword(), authorities);
    }


    public List<AppUser> findUsers() {

        return userRepository.findAll();

    }

    public AppUser findUserByName(String name) {

        return userRepository.findByUsername(name);

    }

    public AppUser insertUser(AppUser user) {

        user.setPassword(passwordEncoder.encode(user.getPassword()));
        return userRepository.save(user);
    }

    public Role insertRole(Role role) {

        return roleRepository.save(role);
    }

    public void addRoleToUser(String username, String roleName) {

        AppUser user = userRepository.findByUsername(username);


        Role role = roleRepository.findByName(roleName);
        user.getRoles().add(role);


    }


}
