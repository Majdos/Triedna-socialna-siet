package sk.majo.maturita.security.authentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import sk.majo.maturita.repositories.SchoolUserRepository;
import sk.majo.maturita.repositories.rest.RestSchoolUserRepository;

/**
 * @author Marian Lorinc
 */
@Service
public class Users implements UserDetailsService {
	
	@Autowired
	private SchoolUserRepository repo;
	
	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		return repo.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException(email + " does not exist"));
	}
	
}
