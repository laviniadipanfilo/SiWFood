package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.repository.RicettaRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RicettaService {

	@Autowired RicettaRepository ricettaRepository;
	
	public Ricetta findById(Long id) {
        return ricettaRepository.findById(id).orElse(null);
    }
	
	public Iterable<Ricetta> findByNome(String nome) {
		return ricettaRepository.findByNome(nome);
	}

    public Iterable<Ricetta> findAll() {
        return ricettaRepository.findAll();
    }
    
    public Ricetta save(Ricetta r) {
        return ricettaRepository.save(r);
    }
    
    public String delete(Long id) {
    	this.ricettaRepository.deleteById(id);
    	return "ricetta cancellata";
    }

}