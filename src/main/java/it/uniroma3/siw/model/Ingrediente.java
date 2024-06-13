package it.uniroma3.siw.model;

import java.util.List;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

@Entity
public class Ingrediente {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotNull
    @NotEmpty
	private String nome;
	private String unitaMisura; // grammi, litri...
	@ManyToMany(mappedBy="ingredienti")
	private List<Ricetta> ricette;
	private int quantita;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getNome() {
		return nome;
	}

	public void setNome(String nome) {
		this.nome = nome;
	}

	public List<Ricetta> getRicette() {
		return ricette;
	}

	public void setRicette(List<Ricetta> ricette) {
		this.ricette = ricette;
	}
	
	public String getUnitaMisura() {
		return unitaMisura;
	}

	public void setUnitaMisura(String unitaMisura) {
		this.unitaMisura = unitaMisura;
	}

	public boolean equals(Object o) {
		Ingrediente i = (Ingrediente)o;
		return this.getNome().equals(i.getNome()) && this.getQuantita() == i.getQuantita();
	}
	
	public int hashCode() {
		return this.getNome().hashCode() + this.getQuantita();
	}

	public int getQuantita() {
		return quantita;
	}

	public void setQuantita(int quantita) {
		this.quantita = quantita;
	}

}
