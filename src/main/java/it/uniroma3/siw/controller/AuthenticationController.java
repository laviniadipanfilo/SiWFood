package it.uniroma3.siw.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import it.uniroma3.siw.model.Credentials;
import it.uniroma3.siw.model.Cuoco;
import it.uniroma3.siw.model.User;
import it.uniroma3.siw.service.CredentialsService;
import it.uniroma3.siw.service.CuocoService;
import it.uniroma3.siw.service.UserService;
import jakarta.validation.Valid;

@Controller
public class AuthenticationController {
	
	@Autowired private CredentialsService credentialsService;
    @Autowired private UserService userService;
    @Autowired private CuocoService cuocoService;
	
	@GetMapping(value = "/register") 
	public String showRegisterForm (Model model) {
		model.addAttribute("user", new User());
		model.addAttribute("credentials", new Credentials());
		return "formRegister";
	}

	@PostMapping(value = { "/register" })
    public String registerUser(@Valid @ModelAttribute("user") User user,
                 BindingResult userBindingResult, @Valid
                 @ModelAttribute("credentials") Credentials credentials,
                 BindingResult credentialsBindingResult,
                 Model model) {
		
		List<User> users = this.userService.getAllUsers();
		for(User u: users) {
			if(u.getName().equals(user.getName()) && u.getSurname().equals(user.getSurname())) {
				model.addAttribute("messaggioErrore", "Queste credenziali ci sono già");
				return "errore.html";
			}
		}

        if(!userBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
            userService.saveUser(user);
            credentials.setUser(user);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("user", user);
            return "formLogin.html";
        }
        return "registerUser";
    }
	
	@GetMapping(value = "/registerCuoco") 
	public String showRegisterCuocoForm (Model model) {
		model.addAttribute("cuoco", new Cuoco());
		model.addAttribute("credentials", new Credentials());
		return "formRegisterCuoco.html";
	}
	
	@PostMapping(value = { "/registerCuoco" }, consumes = "multipart/form-data")
    public String registerCuoco(@Valid @ModelAttribute("cuoco") Cuoco cuoco,
                 BindingResult cuocoBindingResult, @Valid
                 @ModelAttribute("credentials") Credentials credentials,
                 @RequestPart("file") MultipartFile file,
                 BindingResult credentialsBindingResult,
                 Model model) {
		
		Iterable<Cuoco> cuochi = this.cuocoService.findAll();
		for(Cuoco c: cuochi) {
			if(c.getNome().equals(cuoco.getNome()) && c.getCognome().equals(cuoco.getCognome())) {
				model.addAttribute("messaggioErrore", "Queste credenziali ci sono già");
				return "errore.html";
			}
		}
		model.addAttribute("cuoco", cuoco);
        if(!cuocoBindingResult.hasErrors() && !credentialsBindingResult.hasErrors()) {
        	 if(cuoco != null) {
     		    try {
     		    	cuoco.setBytes(file.getBytes());
     		    } catch(Exception e) {
     		    	System.out.println("Problemi nel caricamento foto");
     		    }
     	    }
        	cuocoService.saveCuoco(cuoco);
            credentials.setCuoco(cuoco);
            credentialsService.saveCredentials(credentials);
            model.addAttribute("cuoco", cuoco);
            return "formLogin.html";
        }
        return "registerCuoco";
    }
	
	@GetMapping(value = "/login") 
	public String showLoginForm (Model model) {
		return "formLogin";
	}

	@GetMapping(value = "/") 
	public String index(Model model) {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication instanceof AnonymousAuthenticationToken) {
	        return "home.html";
		}
		else {		
			UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
			Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
			if(credentials.getRole().equals(Credentials.DEFAULT_ROLE))
				return "home.html";
			if(credentials.getRole().equals(Credentials.CUOCO_ROLE))
	    		return "homeCuoco.html";
			if(credentials.getRole().equals(Credentials.ADMIN_ROLE))
				return "homeAdmin.html";
		}
        return "home.html";
	}
		
    @GetMapping(value = "/success")
    public String defaultAfterLogin(Model model) {
        
    	UserDetails userDetails = (UserDetails)SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	Credentials credentials = credentialsService.getCredentials(userDetails.getUsername());
    	if (credentials.getRole().equals(Credentials.ADMIN_ROLE)) {
            return "homeAdmin.html";
        }
    	
    	if(credentials.getRole().equals(Credentials.CUOCO_ROLE))
    		return "homeCuoco.html";
    	
        return "home.html";
    }
    
    @GetMapping(value = "/registerScelta")
    public String registerScelta(Model model) {
    	return "formRegisterScelta.html";
    }
    
}