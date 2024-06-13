package it.uniroma3.siw.model;

import java.time.LocalDate;
import java.util.List;

import javax.persistence.Lob;

import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.NotBlank;

@Entity
public class Cuoco {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@NotBlank
	private String nome;
	@NotBlank
	private String cognome;
	private String email;
	private LocalDate nascita;
    @OneToMany(mappedBy = "cuoco")
	private List<Ricetta> ricette;
    @Lob
    @Column
    private byte[] bytes;

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

	public String getCognome() {
		return cognome;
	}

	public void setCognome(String cognome) {
		this.cognome = cognome;
	}

	public LocalDate getNascita() {
		return nascita;
	}

	public void setNascita(LocalDate nascita) {
		this.nascita = nascita;
	}

	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}

	public List<Ricetta> getRicette() {
		return ricette;
	}

	public void setRicette(List<Ricetta> ricette) {
		this.ricette = ricette;
	}
	
	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}
	
	public boolean equals(Object o) {
		Cuoco i = (Cuoco)o;
		return this.getNome().equals(i.getNome()) && this.getCognome().equals(i.getCognome());
	}
	
	public int hashCode() {
		return this.getNome().hashCode() + this.getCognome().hashCode();
	}
	
	public String generateBase64Image() {
        return Base64.encodeBase64String(this.bytes);
    }

}
