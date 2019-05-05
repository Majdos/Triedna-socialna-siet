package sk.majo.maturita.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;
import sk.majo.maturita.database.models.SchoolUser;
import sk.majo.maturita.security.authentication.DomainConfiguration;
import sk.majo.maturita.security.authentication.GooglePrincipalExtractor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Set;

@Controller
@RequestMapping("/google")
public class GoogleController {

    @Autowired
    private DomainConfiguration domainConfiguration;

    @GetMapping("/login/not-allowed")
    public String notAllowed(Model model, HttpServletRequest request,
                             HttpServletResponse response, Authentication authentication,
                             @AuthenticationPrincipal SchoolUser user) {

        // https://github.com/spring-projects/spring-security/blob/master/web/src/main/java/org/springframework/security/web/authentication/logout/SecurityContextLogoutHandler.java
        SecurityContextLogoutHandler logoutHandler = new SecurityContextLogoutHandler();
        logoutHandler.logout(request, response, authentication);

        if (user == null) {
            return "redirect:/";
        }
        model.addAttribute("email", user.getEmail());
        return "domainNotAllowed";
    }

    @ModelAttribute(name = "supportedDomains")
    public Set<String> supportedDomains() {
        return domainConfiguration.getAllowedDomains();
    }

}
