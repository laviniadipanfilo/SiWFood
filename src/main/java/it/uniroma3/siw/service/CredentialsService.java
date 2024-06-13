package it.uniroma3.siw.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.repository.CredentialsRepository;

@Service
public class CredentialsService {

    @Autowired protected PasswordEncoder passwordEncoder;

    @Autowired protected CredentialsRepository credentialsRepository;

    @Transactional
    public Credentials getCredentials(Long id) {
        Optional<Credentials> result = this.credentialsRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Credentials getCredentials(String username) {
        Optional<Credentials> result = this.credentialsRepository.findByUsername(username);
        return result.orElse(null);
    }
    
    public Iterable<Credentials> getAllCredentials() {
    	return this.credentialsRepository.findAll();
    }
    
    public Credentials save(Credentials c) {
        return credentialsRepository.save(c);
    }
    
    public String delete(Long id) {
    	this.credentialsRepository.deleteById(id);
    	return "credential cancellata";
    }

    @Transactional
    public Credentials saveCredentials(Credentials credentials) {
    	if(credentials.getRole().equals(Credentials.CUOCO_ROLE))
            credentials.setRole(Credentials.CUOCO_ROLE);
    	else
    		credentials.setRole(Credentials.DEFAULT_ROLE);
        credentials.setPassword(this.passwordEncoder.encode(credentials.getPassword()));
        return this.credentialsRepository.save(credentials);
    }
}
