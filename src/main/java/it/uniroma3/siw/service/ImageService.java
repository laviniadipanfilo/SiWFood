package it.uniroma3.siw.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.repository.ImageRepository;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ImageService {

	@Autowired ImageRepository imageRepository;
	
	public Image findById(Long id) {
        return imageRepository.findById(id).orElse(null);
    }
    
    public Image save(Image r) {
        return imageRepository.save(r);
    }
    
    public String delete(Long id) {
    	this.imageRepository.deleteById(id);
    	return "foto cancellata";
    }

}