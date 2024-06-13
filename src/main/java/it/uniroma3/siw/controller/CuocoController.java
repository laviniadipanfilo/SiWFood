package it.uniroma3.siw.controller;

import java.util.List;

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

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.model.Ricetta;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.repository.CuocoRepository;
import it.uniroma3.siw.repository.UserRepository;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.CuocoService;

@Controller
public class CuocoController {
	
	@Autowired CuocoService cuocoService;
	@Autowired CuocoRepository cuocoRepository;
	@Autowired CredentialsService credentialsService;
	@Autowired UserRepository userRepository;
	
	@GetMapping("/profiloCuoco")
	public String showProfiloCuoco(Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	if(credentials.getRole().equals(Credentials.CUOCO_ROLE) || credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
    		Iterable<Cuoco> cuochi = this.cuocoRepository.findAll();
    		for(Cuoco c: cuochi) {
    			if(c.getId() == credentials.getCuoco().getId()) {
    				model.addAttribute("cuoco", c);
    				return "profiloCuoco.html";
    			}
    		}
    	}

    	return "homeCuoco.html";
	}
	
	@GetMapping("/profiloUser")
	public String showProfiloUser(Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	
    	if(credentials.getRole().equals(Credentials.DEFAULT_ROLE)) {
    		Iterable<User> users = this.userRepository.findAll();
    		for(User u: users) {
    			if(u.getId() == credentials.getUser().getId()) {
    				model.addAttribute("user", u);
    				return "profilo.html";
    			}
    		}
    	}

    	return "home.html";
	}
	
	@GetMapping("/cuoco/{id}")
	public String showCuoco(@PathVariable("id") Long id, Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		model.addAttribute("cuoco", this.cuocoService.findById(id));
		if(credentials.getRole().equals(Credentials.ADMIN_ROLE))
			return "cuocoAdmin.html";
		return "cuoco.html";
	}

	@GetMapping("/cuochi")
	public String showCuochi(Model model) {
	model.addAttribute("cuochi", this.cuocoService.findAll());
		return "cuochi.html";
	}
	
	@GetMapping("/cancellaCuochi")
	public String cancellaCuochi(Model model) {
		model.addAttribute("cuochi", this.cuocoService.findAll());
		return "cancellaCuochi.html";
	}

	@PostMapping("/cancellaCuoco/{idCuoco}")
	public String cancellaCuoco(@PathVariable("idCuoco") Long idCuoco, Model model) {
		UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
		Cuoco cuoco = this.cuocoRepository.findById(idCuoco).get();
		
		if(cuoco.getId() == credentials.getCuoco().getId()) {
			model.addAttribute("messaggioErrore", "Non puoi cancellare l'admin!");
			return "errore.html";
		}
		
		List<Ricetta> ricette = cuoco.getRicette();
		for(Ricetta r: ricette) {
			r.setCuoco(null);
		}
		
	    this.cuocoRepository.save(cuoco);

		Iterable<Credentials> allCredentials = this.credentialsService.getAllCredentials();
		for(Credentials i: allCredentials) {
			if(i.getCuoco() != null) {
				if(i.getCuoco().getId() == idCuoco) {
					if(!i.getRole().equals(Credentials.ADMIN_ROLE)) {
		                i.setCuoco(null);
		                this.credentialsService.delete(i.getId());
		            }
				}
			}
		}

		model.addAttribute("credentials", this.credentialsService.getAllCredentials());
		this.cuocoService.delete(idCuoco);

    	model.addAttribute("cuochi", this.cuocoService.findAll());
		return "redirect:/cancellaCuochi";
	}
	
	@GetMapping("/formNuovoCuoco")
	public String formNuovoCuoco(Model model) {
		model.addAttribute("cuoco", new Cuoco());
		return "formNuovoCuoco.html";
	}

	@PostMapping("/cuoco")
	public String newCuoco(@ModelAttribute("cuoco") Cuoco cuoco, Model model) {
			if (!cuocoRepository.existsByNomeAndCognome(cuoco.getNome(), cuoco.getCognome())) {
				this.cuocoService.save(cuoco); 
				return "redirect:cuoco/"+cuoco.getId();
			} else {
				model.addAttribute("messaggioErrore", "Questo cuoco esiste già");
				return "formNuovoCuoco.html";
			}
	}
	
	@GetMapping("/imageCuoco/{idCuoco}")
	public String formSetImage(@PathVariable("idCuoco") Long idCuoco, Model model) {
	    Cuoco cuoco = this.cuocoRepository.findById(idCuoco).orElse(null);
	    if (cuoco == null) {
	        model.addAttribute("messaggioErrore", "Cuoco non trovato");
	        return "errore.html";
	    }

	    model.addAttribute("cuoco", cuoco);
	    return "formNewImageCuoco.html";
	}
	
	@PostMapping(value = "/imageCuoco/{idCuoco}", consumes = "multipart/form-data")
	public String imageCuoco(@PathVariable("idCuoco") Long idCuoco, @RequestPart("file") MultipartFile file, Model model) {
		Cuoco cuoco = cuocoRepository.findById(idCuoco).orElse(null);
	    if(cuoco != null) {
		    try {
		    	cuoco.setBytes(file.getBytes());
		    } catch(Exception e) {
		    	System.out.println("Eccezione in caricamento foto");
		    }
	    }
	    
	    cuocoRepository.save(cuoco);
	    model.addAttribute("cuoco", cuoco);
	    return "cuoco.html";
	}
	
	@GetMapping("/cercaCuoco")
	public String formCercaCuoco(Model model) {
		model.addAttribute("cuoco", new Cuoco());
		return "formCercaCuoco.html";
	}
	
	@PostMapping("/cercaCuoco")
	public String cerca(@ModelAttribute("cuoco") Cuoco cuoco, Model model) {
		return "redirect:cuocoNome/"+cuoco.getNome();
	}

	@GetMapping("/cuocoNome/{nome}")
	public String getCuocoByNome(@PathVariable("nome") String nome, Model model) {
		model.addAttribute("cuochi", this.cuocoService.findByNome(nome));
		return "cuochi.html";
	}
	
}
