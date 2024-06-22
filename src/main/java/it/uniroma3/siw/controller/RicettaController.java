package it.uniroma3.siw.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.controller.validator.RicettaValidator;
import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.model.Image;
import it.uniroma3.siw.model.Ingrediente;
import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.repository.CuocoRepository;
import it.uniroma3.siw.repository.ImageRepository;
import it.uniroma3.siw.repository.IngredienteRepository;
import it.uniroma3.siw.repository.RicettaRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.CuocoService;
import it.uniroma3.siw.service.ImageService;
import it.uniroma3.siw.service.IngredienteService;
import it.uniroma3.siw.service.RicettaService;
import lombok.extern.slf4j.Slf4j;

@Controller
@Slf4j
public class RicettaController {

	@Autowired RicettaService ricettaService;
	@Autowired CuocoService cuocoService;
	@Autowired CuocoRepository cuocoRepository;
	@Autowired RicettaRepository ricettaRepository;
	@Autowired IngredienteService ingredienteService;
	@Autowired IngredienteRepository ingredienteRepository;
	@Autowired CredentialsService credentialsService;
	@Autowired RicettaValidator ricettaValidator;
	@Autowired ImageRepository imageRepository;
	@Autowired ImageService imageService;
	
	@GetMapping("/ricetta/{id}")
	public String showRicetta(@PathVariable("id") Long id, Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		model.addAttribute("ricetta", this.ricettaService.findById(id));
		Cuoco cuoco = this.ricettaRepository.findById(id).get().getCuoco();
		
		if(credentials.getRole().equals(Credentials.DEFAULT_ROLE))
			return "ricetta.html";
		if(credentials.getRole().equals(Credentials.CUOCO_ROLE)) {
			if(cuoco != null) {
				if(credentials.getCuoco().getId() == cuoco.getId())
					return "ricettaCuoco.html";
			}
		}
		if(credentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "ricettaCuoco.html";
		return "ricetta.html";
	}

	@GetMapping("/ricette")
	public String showRicette(Model model) {
		model.addAttribute("ricette", this.ricettaService.findAll());
		return "ricette.html";
	}
	
	@GetMapping("/aggiungiCuoco/{idRicetta}")
	public String showCuochi(@PathVariable("idRicetta") Long idRicetta, Model model) {
		model.addAttribute("cuochi", this.cuocoService.findAll());
		model.addAttribute("ricetta", ricettaRepository.findById(idRicetta).get());
	    return "aggiungiCuoco.html";
	}

//	@GetMapping("/aggiungiIngrediente/{idRicetta}")
//	public String showIngredienti(@PathVariable("idRicetta") Long idRicetta, Model model) {
//		model.addAttribute("ingredienti", this.ingredienteService.findAll());
//		model.addAttribute("ricetta", ricettaRepository.findById(idRicetta).get());
//	    return "aggiungiIngrediente.html";
//	}
	
	@GetMapping("/modificaRicetta/{idRicetta}")
	public String formModificaRicetta(@PathVariable("idRicetta") Long idRicetta, Model model) {
		model.addAttribute("ricetta", this.ricettaService.findById(idRicetta));
		return "modificaRicetta.html";
	}
	
	@PostMapping("/modificaRicetta/{idRicetta}")
	public String setCuoco(@PathVariable("idRicetta") Long idRicetta, @ModelAttribute("ricetta") Ricetta ricetta, Model model) {
		System.out.println("-----------------------------------------------------------------");
		if (!cuocoRepository.existsByNomeAndCognome(ricetta.getCuoco().getNome(), ricetta.getCuoco().getCognome())) {
			System.out.println("--------------------------------------------------------------");
			this.cuocoService.save(ricetta.getCuoco());    
			return "redirect:modificaRicetta/"+ricetta.getId();
		} else {
			model.addAttribute("messaggioErrore", "Questo cuoco c'è già");
			return "aggiungiCuoco.html";
		}
	}

//	@PostMapping("/aggiungiIngrediente/{idRicetta}")
//	public String setIngrediente(@PathVariable("idRicetta") Long idRicetta, @ModelAttribute("ricetta") Ricetta ricetta, Model model) {
//		System.out.println("-----------------------------------------------------------------");
//		for(Ingrediente i: ricetta.getIngredienti()) {
//				if (!ingredienteRepository.existsByNome(i.getNome())) {
//				System.out.println("--------------------------------------------------------------");
//				this.cuocoService.save(ricetta.getCuoco());    
//				return "redirect:modificaRicetta/"+ricetta.getId();
//			}
//		}
//		model.addAttribute("messaggioErrore", "Questo ingrediente c'è già");
//		return "errore.html";
//	}

	@GetMapping("/setCuoco/{idCuoco}/{idRicetta}")
	public String formCuoco(@PathVariable("idCuoco") Long idCuoco, @PathVariable("idRicetta") Long idRicetta, Model model) {
		Cuoco cuoco = this.cuocoRepository.findById(idCuoco).get();
		Ricetta ricetta = this.ricettaRepository.findById(idRicetta).get();
		ricetta.setCuoco(cuoco);
		cuoco.getRicette().add(ricetta);
		this.ricettaRepository.save(ricetta);
		model.addAttribute("cuoco", cuoco);
		model.addAttribute("ricetta", ricetta);
		return "modificaRicetta.html";
	}

	@GetMapping("/setIngrediente/{idIngrediente}/{idRicetta}")
	public String formIngrediente(@PathVariable("idIngrediente") Long idIngrediente, @PathVariable("idRicetta") Long idRicetta, Model model) {
		Ingrediente ingrediente = this.ingredienteRepository.findById(idIngrediente).get();
		Ricetta ricetta = this.ricettaRepository.findById(idRicetta).get();
		if(ricetta.getIngredienti().contains(ingrediente)) {
			return "redirect:/ricetta/"+idRicetta;
		}
		else {
			ricetta.getIngredienti().add(ingrediente);
			ingrediente.getRicette().add(ricetta);
			this.ricettaRepository.save(ricetta);
			this.ingredienteRepository.save(ingrediente);
			model.addAttribute("ingrediente", ingrediente);
			model.addAttribute("ricetta", ricetta);
			return "modificaRicetta.html";
		}
	}

	@GetMapping("/formNuovaRicetta")
    public String formNuovaRicetta(Model model) {
        Ricetta ricetta = new Ricetta();
        model.addAttribute("ricetta", ricetta);
        return "formNuovaRicetta";
    }

	@PostMapping(value = "/ricetta", consumes = "multipart/form-data")
	public String newRicetta(@ModelAttribute("ricetta") Ricetta ricetta, @RequestPart("file") MultipartFile file, Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		if (!ricettaRepository.existsByNomeAndId(ricetta.getNome(), ricetta.getId())) {
			Cuoco cuoco = credentials.getCuoco();
			ricetta.setCuoco(cuoco);
			System.out.println("-------------ricetta: "+ricetta.getNome()+"----------------------");
			cuoco.getRicette().add(ricetta);
			this.cuocoService.save(cuoco);
			
		    try {
		    	ricetta.setBytes(file.getBytes());
			    System.out.println("----------------------bytes: "+file.getBytes());
		    } catch(Exception e) {
		    	System.out.println("no");
		    }
		    cuocoRepository.save(ricetta.getCuoco());
		    ricettaRepository.save(ricetta);
			
			model.addAttribute("ricettaCuoco", ricetta);
			return "ricettaCuoco.html";
		} else {
			model.addAttribute("messaggioErrore", "Questa ricetta esiste già");
			return "formNuovaRicetta.html";
		}
	}
	
	@GetMapping("/setImages/{idRicetta}")
	public String formSetImages(@PathVariable("idRicetta") Long idRicetta, Model model) {
	    Ricetta ricetta = this.ricettaRepository.findById(idRicetta).orElse(null);
	    if (ricetta == null) {
	        model.addAttribute("messaggioErrore", "Ricetta non trovata");
	        return "errore.html";
	    }
	    
	    model.addAttribute("ricetta", ricetta);
	    return "formNewImmagini.html";
	}
	
	@PostMapping(value = "/setImages/{idRicetta}", consumes = "multipart/form-data")
	public String newImmagini(@PathVariable("idRicetta") Long idRicetta, @RequestPart("file") MultipartFile file, Model model) {
	    Ricetta ricetta = ricettaRepository.findById(idRicetta).orElse(null);
	    
	    if (ricetta == null) {
	        model.addAttribute("messaggioErrore", "Ricetta non trovata");
	        return "errore.html";
	    }
	    
	    Image image = new Image();
	    try {
	        image.setBytes(file.getBytes());
	        image.setRicetta(ricetta);
	        imageRepository.save(image);
	        ricetta.getImages().add(image);
	    } catch (Exception e) {
	        model.addAttribute("messaggioErrore", "Errore nel caricamento dell'immagine");
	        return "errore.html";
	    }
	    
	    ricettaRepository.save(ricetta);
		return "redirect:/ricetta/"+ricetta.getId();
	}
	
	@PostMapping("/cancellaRicetta/{idRicetta}")
	public String cancellaRicetta(@PathVariable("idRicetta") Long idRicetta, Model model) {
	    Ricetta ricetta = this.ricettaRepository.findById(idRicetta).get();
	    
	    for (Image i : ricetta.getImages()) {
	        i.setRicetta(null);
	        this.imageRepository.save(i);
	    }
	    
	    ricetta.getImages().clear();
	    this.ricettaRepository.save(ricetta);

	    if(this.ricettaService.findById(idRicetta).getCuoco() != null) {
			Cuoco cuoco = this.ricettaService.findById(idRicetta).getCuoco();
	    	cuoco.getRicette().remove(this.ricettaRepository.findById(idRicetta).get());
	    	model.addAttribute("cuoco", this.ricettaService.findById(idRicetta).getCuoco());
	    	Iterable<Credentials> allCredentials = this.credentialsService.getAllCredentials();
			for(Credentials i: allCredentials) {
				if(i.getCuoco() != null) {
					if(i.getCuoco().getId() == cuoco.getId()) {
						for(Ricetta r: cuoco.getRicette()) {
							if(r.getId() == idRicetta) {
								this.ricettaRepository.findById(idRicetta).get().setCuoco(null);
							}
						}
					}
				}
			}
		}

		this.ricettaService.delete(idRicetta);
	    model.addAttribute("ricette", this.ricettaService.findAll());
	    return "ricette.html";
	}
	
	@GetMapping("immagine/{idRicetta}/{idImage}")
	public String getFotoRicetta(@PathVariable("idRicetta") Long idRicetta, @PathVariable("idImage") Long idImage, Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		model.addAttribute("ricetta", this.ricettaService.findById(idRicetta));
		Cuoco cuoco = this.ricettaRepository.findById(idRicetta).get().getCuoco();
		Image image = this.imageRepository.findById(idImage).get();
		model.addAttribute("image", image);
		
		if(credentials.getRole().equals(Credentials.DEFAULT_ROLE))
			return "mostraFoto.html";
		if(credentials.getRole().equals(Credentials.CUOCO_ROLE)) {
			if(cuoco != null) {
				if(credentials.getCuoco().getId() == cuoco.getId())
					return "cancellaFoto.html";
			}
		}
		if(credentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "cancellaFoto.html";
		return "mostraFoto.html";
		
	}

	@PostMapping("/cancellaFoto/{idRicetta}/{idImage}")
	public String cancellaFotoRicetta(@PathVariable("idRicetta") Long idRicetta, @PathVariable("idImage") Long idImage, Model model) {
			
			Ricetta ricetta = ricettaRepository.findById(idRicetta).orElse(null);
		    if (ricetta == null) {
		        model.addAttribute("messaggioErrore", "Ricetta non trovata");
		        return "errore.html";
		    }
		    
		    Image image = imageRepository.findById(idImage).orElse(null);
		    if (image == null) {
		        model.addAttribute("messaggioErrore", "Ricetta non trovata");
		        return "errore.html";
		    }
			
		    image.setRicetta(null);
			ricetta.getImages().remove(image);
			this.imageRepository.delete(image);
			this.ricettaRepository.save(ricetta);
			
    	model.addAttribute("ricetta", ricetta);
		return "redirect:/ricetta/"+ricetta.getId();
	}

	@GetMapping("/cercaRicetta")
	public String formCercaRicetta(Model model) {
		model.addAttribute("ricetta", new Ricetta());
		return "formCercaRicetta.html";
	}
	
	@PostMapping("/cercaRicetta")
	public String cerca(@ModelAttribute("ricetta") Ricetta ricetta, Model model) {
		return "redirect:ricettaNome/"+ricetta.getNome();
	}

	@GetMapping("/ricettaNome/{nome}")
	public String getRicettaByNome(@PathVariable("nome") String nome, Model model) {
		model.addAttribute("ricette", this.ricettaService.findByNome(nome));
		return "ricette.html";
	}
	
}
