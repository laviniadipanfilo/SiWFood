package it.uniroma3.siw.model;

import java.util.List;

import javax.persistence.Lob;

import org.apache.tomcat.util.codec.binary.Base64;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Ricetta {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;
	private String nome;
    @ManyToMany
    private List<Ingrediente> ingredienti;
    @Column(length=2000)
    private String descrizione;
    @ManyToOne(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Cuoco cuoco;
    @Lob
    @Column
    private byte[] bytes;
    @OneToMany(mappedBy="ricetta")
    private List<Image> images;

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

	public List<Ingrediente> getIngredienti() {
		return ingredienti;
	}

	public void setIngredienti(List<Ingrediente> ingredienti) {
		this.ingredienti = ingredienti;
	}
	
	public String getDescrizione() {
		return descrizione;
	}

	public void setDescrizione(String descrizione) {
		this.descrizione = descrizione;
	}

	public Cuoco getCuoco() {
		return cuoco;
	}

	public void setCuoco(Cuoco cuoco) {
		this.cuoco = cuoco;
	}
	
	public byte[] getBytes() {
		return bytes;
	}

	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	
	public List<Image> getImages() {
		return images;
	}

	public void setImages(List<Image> images) {
		this.images = images;
	}
	
	public boolean equals(Object o) {
		Ricetta i = (Ricetta)o;
		return this.getNome().equals(i.getNome());
	}
	
	public int hashCode() {
		return this.getNome().hashCode();
	}
	
	public String generateBase64Image() {
        return Base64.encodeBase64String(this.bytes);
    }
	
	public void setBytesToImage(byte[] bytes) {
		Image image = new Image();
		image.setBytes(bytes);
		this.images.add(image);
	}

}
