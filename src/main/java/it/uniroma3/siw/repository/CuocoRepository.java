package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.model.Ricetta;

public interface CuocoRepository extends CrudRepository<Cuoco, Long> {

	public boolean existsByNomeAndCognome(String nome, String cognome);
	
	public List<Cuoco> findByNome(String nome);

}