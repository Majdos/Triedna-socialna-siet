package sk.majo.maturita.controllers.rest;

import java.util.Set;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import sk.majo.maturita.security.Authority;
import sk.majo.maturita.security.Permission;

/**
 * Returns permissions of logged in user
 * @author Marian Lorinc
 *
 */
@RestController
public class PermissionsRest {
	
	@GetMapping(value = "/api/permissions", produces = "application/json")
	@Cacheable
	public @ResponseBody Set<Permission> getAvailablePermissions() {
		return Authority.listPermissions();
	}
}
