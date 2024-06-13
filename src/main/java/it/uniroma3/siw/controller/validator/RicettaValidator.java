package it.uniroma3.siw.controller.validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.repository.RicettaRepository;

@Component
public class RicettaValidator implements Validator {
	
	@Autowired private RicettaRepository ricettaRepository;

	@Override
	public void validate(Object o, Errors errors) {
		Ricetta ricetta = (Ricetta)o;
		if (ricetta.getNome()!=null && ricetta.getId()!=null 
				&& ricettaRepository.existsByNomeAndId(ricetta.getNome(), ricetta.getId())) {
			errors.reject("movie.duplicate");
		}
	}
	@Override
	public boolean supports(Class<?> aClass) {
		return Ricetta.class.equals(aClass);
	}
}