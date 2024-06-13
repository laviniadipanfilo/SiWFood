package it.uniroma3.siw.model;

import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;

@Entity
public class Image {

	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	@Lob
	@Column
	private byte[] bytes;
	@ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
	private Ricetta ricetta;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public byte[] getBytes() {
		return bytes;
	}
	
	public Ricetta getRicetta() {
		return ricetta;
	}

	public void setRicetta(Ricetta ricetta) {
		this.ricetta = ricetta;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public String generateBase64Image() {
        return Base64.encodeBase64String(this.bytes);
    }

}
