package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.model.Ingrediente;
import it.uniroma3.siw.repository.IngredienteRepository;

@Service
public class IngredienteService {

	@Autowired IngredienteRepository ingredienteRepository;
	
	public Ingrediente findById(Long id) {
        return ingredienteRepository.findById(id).orElse(null);
    }

    public Iterable<Ingrediente> findAll() {
        return ingredienteRepository.findAll();
    }
    
    public Ingrediente save(Ingrediente i) {
        return ingredienteRepository.save(i);
    }
    
    public String delete(Long id) {
    	this.ingredienteRepository.deleteById(id);
    	return "ingrediente cancellato";
    }

}