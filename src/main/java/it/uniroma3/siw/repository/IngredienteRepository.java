package it.uniroma3.siw.repository;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.model.Ingrediente;
import it.uniroma3.siw.model.Ricetta;

public interface IngredienteRepository extends CrudRepository<Ingrediente, Long> {
	
	public boolean existsByNomeAndId(String nome, Long id);
	
	public boolean existsByNome(String nome);

}