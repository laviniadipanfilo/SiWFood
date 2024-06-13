package it.uniroma3.siw.repository;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

import it.uniroma3.siw.model.Ricetta;

public interface RicettaRepository extends CrudRepository<Ricetta, Long> {

	public boolean existsByNomeAndId(String nome, Long id);

	public boolean existsByNome(String nome);
	
	public List<Ricetta> findByNome(String nome);
}