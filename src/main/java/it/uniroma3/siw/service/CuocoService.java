package it.uniroma3.siw.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.repository.CuocoRepository;

@Service
public class CuocoService {

	@Autowired CuocoRepository cuocoRepository;
	
	public Cuoco findById(Long id) {
        return cuocoRepository.findById(id).orElse(null);
    }

    public Iterable<Cuoco> findAll() {
        return cuocoRepository.findAll();
    }
    
    public Iterable<Cuoco> findByNome(String nome) {
		return cuocoRepository.findByNome(nome);
	}
    
    public Cuoco save(Cuoco c) {
        return cuocoRepository.save(c);
    }
    
    public String delete(Long id) {
    	this.cuocoRepository.deleteById(id);
    	return "cuoco cancellato";
    }
    
    @Transactional
    public Cuoco getCuoco(Long id) {
        Optional<Cuoco> result = this.cuocoRepository.findById(id);
        return result.orElse(null);
    }

    @Transactional
    public Cuoco saveCuoco(Cuoco cuoco) {
        return this.cuocoRepository.save(cuoco);
    }

    @Transactional
    public List<Cuoco> getCuochi() {
        List<Cuoco> result = new ArrayList<>();
        Iterable<Cuoco> iterable = this.cuocoRepository.findAll();
        for(Cuoco cuoco : iterable)
            result.add(cuoco);
        return result;
    }

}