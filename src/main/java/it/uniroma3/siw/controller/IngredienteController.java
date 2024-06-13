package it.uniroma3.siw.controller;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.model.Ingrediente;
import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.repository.CuocoRepository;
import it.uniroma3.siw.repository.IngredienteRepository;
import it.uniroma3.siw.repository.RicettaRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.CuocoService;
import it.uniroma3.siw.service.IngredienteService;
import it.uniroma3.siw.service.RicettaService;

@Controller
public class IngredienteController {

	@Autowired RicettaService ricettaService;
	@Autowired CuocoService cuocoService;
	@Autowired CuocoRepository cuocoRepository;
	@Autowired RicettaRepository ricettaRepository;
	@Autowired IngredienteService ingredienteService;
	@Autowired IngredienteRepository ingredienteRepository;
	@Autowired CredentialsService credentialsService;

	@GetMapping("/ingrediente/{idIngrediente}")
	public String showIngrediente(@PathVariable("idIngrediente") Long idIngrediente, Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		model.addAttribute("ingrediente", this.ingredienteService.findById(idIngrediente));
		for(Ricetta r: this.ingredienteRepository.findById(idIngrediente).get().getRicette()) {
			if(r.getCuoco() != null) {
				Cuoco cuoco = r.getCuoco();
				for(Ingrediente i: r.getIngredienti()) {
					if(credentials.getRole().equals(Credentials.DEFAULT_ROLE))
						return "ingrediente.html";
					if(credentials.getRole().equals(Credentials.CUOCO_ROLE)) {
						if(cuoco != null) {
							if(credentials.getCuoco().getId() == cuoco.getId())
								return "ingredienteCuoco.html";
						}
					}
				}
			}
		}
		if(credentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "ingredienteCuoco.html";
		return "ingrediente.html";
	}
	
	@GetMapping("/ingredienti")
	public String showIngredienti(Model model) {
		model.addAttribute("ingredienti", this.ingredienteService.findAll());
		return "ingredienti.html";
	}
	
	@GetMapping("/ricette/ingrediente/{idIngrediente}")
	public String showRicetteConIngrediente(@PathVariable("idIngrediente") Long idIngrediente, Model model) {
		Ingrediente ingrediente = this.ingredienteRepository.findById(idIngrediente).orElse(null);
	    if (ingrediente == null) {
	    	return "ingrediente.html";
	    }
	    else {
		    List<Ricetta> ricetteConIngredienteBuffer =  new LinkedList<>();
		    Iterable<Ricetta> ricette = this.ricettaRepository.findAll();
		    for(Ricetta i: ricette) {
		    	List<Ingrediente> ingr = i.getIngredienti();
		    	for(Ingrediente j: ingr) {
		    		if(j.getNome().equals(ingrediente.getNome())) {
		    			ricetteConIngredienteBuffer.add(i);
		    		}
		    			
		    	}
		    }
		    
		    Set<Ricetta> ricetteConIngrediente = new HashSet<>();
		    ricetteConIngrediente.addAll(ricetteConIngredienteBuffer);
		    
		    model.addAttribute("ingrediente", ingrediente);
		    model.addAttribute("ricetteConIngrediente", ricetteConIngrediente);
		    
		    return "ricetteConIngrediente.html";
	    }
	}

	@GetMapping("/formNuovoIngrediente/{idRicetta}")
	public String formNuovoIngrediente(@PathVariable("idRicetta") Long idRicetta, Model model) {
	    Ricetta ricetta = this.ricettaRepository.findById(idRicetta).orElse(null);
	    if (ricetta == null) {
	        model.addAttribute("messaggioErrore", "Ricetta non trovata");
	        return "errore.html";
	    }

	    model.addAttribute("ricetta", ricetta);
	    model.addAttribute("ingredienti", this.ingredienteRepository.findAll());
	    model.addAttribute("ingrediente", new Ingrediente());
	    model.addAttribute("idRicetta", idRicetta);
	    return "formNuovoIngrediente.html";
	}

	@PostMapping("/ingrediente/{idRicetta}")
	public String newIngrediente(@ModelAttribute("ingrediente") Ingrediente ingrediente, @PathVariable("idRicetta") Long idRicetta, Model model) {
	    Ricetta ricetta = ricettaRepository.findById(idRicetta).orElse(null);
	    
	    if (ricetta.getIngredienti().contains(ingrediente) || ricetta == null) {
	        model.addAttribute("messaggioErrore", "Questo ingrediente è già presente nella ricetta.");
	        return "errore.html";
	    }
	    
	    for(Ingrediente r: ricetta.getIngredienti()) {
	    	if(r.getNome().equals(ingrediente.getNome())) {
	    		model.addAttribute("messaggioErrore", "Questo ingrediente è già presente nella ricetta: se vuoi cambiarlo, elimina quello già presente e aggiungilo di nuovo!");
	    		return "errore.html";
	    	}
	    }

	    this.ingredienteRepository.save(ingrediente);
	    ricetta.getIngredienti().add(ingrediente);
	    ricettaRepository.save(ricetta);
	    return "redirect:/ricetta/"+idRicetta;
	}

	@PostMapping("/cancellaIngrediente/{idRicetta}/{idIngrediente}")
	public String cancellaIngrediente(@PathVariable("idRicetta") Long idRicetta, 
	                                   @PathVariable("idIngrediente") Long idIngrediente, 
	                                   Model model) {

	    Ricetta ricetta = ricettaRepository.findById(idRicetta).orElse(null);
	    if (ricetta == null) {
	        model.addAttribute("messaggioErrore", "Ricetta non trovata");
	        return "errore.html";
	    }
	    
	    Ingrediente ingrediente = this.ingredienteRepository.findById(idIngrediente).orElse(null);
	    if (ingrediente == null) {
	        model.addAttribute("messaggioErrore", "Ingrediente non trovato");
	        return "errore.html";
	    }

	    ricetta.getIngredienti().remove(ingrediente);
	    ingrediente.getRicette().remove(ricetta);

	    this.ricettaRepository.save(ricetta);

	    model.addAttribute("ricetta", ricetta);
	    model.addAttribute("ricette", ricettaRepository.findAll());
	    model.addAttribute("ingredienti", ingredienteRepository.findAll());

	    return "ricette.html";
	}

}
