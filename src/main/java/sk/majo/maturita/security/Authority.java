package sk.majo.maturita.security;

import java.util.Arrays;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import sk.majo.maturita.util.Sets;

/**
 * This class stores authority of user and contains utility functions.
 * @author Marian Lorinc
 *
 */
public class Authority {

	private Set<Permission> permissions = new HashSet<>();
	
	public Authority(Collection<Permission> permissions) {
		this.permissions.addAll(permissions);
	}
	
	public Authority(Permission ...permissions) {
		this(Arrays.asList(permissions));
	}

	public Authority() {
		super();
	}
		
	public boolean hasAny(Permission ...permissions) {
		Set<Permission> allValidPermissions = getPermissions();
		for (Permission permission : permissions) {
			if (allValidPermissions.contains(permission)) {
				return true;
			}
		}
		return false;
	}
	
	public boolean has(Permission permission) {
		return hasAny(permission);
	}
	
	public boolean hasAll(Collection<Permission> permissions) {
		return permissions.containsAll(permissions);
	}
	
	public boolean hasAll(Permission ...permissions) {
		return hasAll(Arrays.asList(permissions));
	}

	public Set<Permission> getPermissions() {
		Permission maxAuthority = permissions.stream().max(Comparator.comparing(Permission::ordinal)).orElse(Permission.READ);
		if (maxAuthority.equals(Permission.READ)) {
			return Sets.of(Permission.READ);
		}
		
		Stream<Permission> stream = Arrays.stream(Permission.values());
		return stream.filter(x -> maxAuthority.compareTo(x) >= 0).collect(Collectors.toSet());
	}
	
	public static Set<Permission> listPermissions() {
		return Sets.of(Permission.values());		
	}
	
	public static String listPermissionsToString() {
		return Arrays.stream(Permission.values()).map(Permission::name).collect(Collectors.joining(", "));
	}
	
}
