package com.techpulseIt.workflowengine.security.user;



import com.techpulseIt.workflowengine.entity.User;
import com.techpulseIt.workflowengine.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AppUserDetailsService implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException{
        User user =  userRepository.findByEmail(email)
                .orElseThrow(()-> new UsernameNotFoundException("User not found"));
        return AppUserDetails.buildUserDetails(user);
    }
}
