package sk.majo.maturita.database.models;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.NaturalId;
import org.springframework.hateoas.Identifiable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import sk.majo.maturita.database.models.definitions.GoogleUser;
import sk.majo.maturita.util.Sets;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static javax.persistence.CascadeType.ALL;

/**
 * @author Marian Lorinc
 */

@Data
@EqualsAndHashCode(of = {"email"})
@NoArgsConstructor
@ToString(of = {"email", "firstname", "lastname", "roles"})

@Entity(name = "Users")
@JsonIgnoreProperties(ignoreUnknown = true)
public class SchoolUser implements UserDetails, Identifiable<Long> {

    private static final long serialVersionUID = 1L;

    public static final BCryptPasswordEncoder PASSWORD_ENCODER = new BCryptPasswordEncoder();

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @JsonIgnore
    private Long id;

    @NaturalId
    @Column(length = 50, nullable = false)
    @Email
    @JsonIgnore
    private String email;

    @NotNull
    @Column(length = 20, nullable = false)
    @JsonIgnore
    private String roles;

    @NotBlank
    @Column(length = 15, nullable = false)
    private String firstname;

    @NotBlank
    @Column(length = 15, nullable = false)
    private String lastname;

    @OneToMany(mappedBy = "user", cascade = {ALL}, orphanRemoval = true)
    @JsonIgnore
    private List<GroupMembership> groupMemberships = new ArrayList<>();

    @OneToMany(mappedBy = "author", cascade = {ALL})
    @JsonIgnore
    private List<Article> articles = new ArrayList<>();

    @Column
    private boolean enabled;

    public SchoolUser(GoogleUser user) {
        this(user.getEmail(), "", user.getGivenName(), user.getFamilyName());
    }

    public SchoolUser(@Email String email, @NotNull String roles, @NotBlank String firstname, @NotBlank String lastname) {
        this.email = email;
        this.setRoles(roles);
        this.firstname = firstname;
        this.lastname = lastname;
        this.enabled = false;
    }

    public void addArticle(Article article) {
        articles.add(article);
        article.setAuthor(this);
    }

    @JsonIgnore
    public Set<String> getRoleList() {
        if (this.roles.isEmpty()) {
            return new HashSet<>();
        }
        return Sets.of(this.roles.split(","));
    }

    public void removeArticle(Article article) {
        articles.remove(article);
        article.setAuthor(null);
    }

    @JsonIgnore
    public String getPassword() {
        return null;
    }

    public void setRoles(String roles) {
        this.roles = roles;
    }

    public void setRoles(Set<String> roles) {
        this.roles = roles.stream()
                .map(role -> role.startsWith("ROLE_") ? role.substring("ROLE_".length()) : role)
                .collect(Collectors.joining(","));
    }

    public void addRole(String role) {
        Set<String> roles = getRoleList();
        roles.add(role);
        this.setRoles(roles);
    }

    @JsonIgnore
    public boolean hasRole(String role) {
        if (role.startsWith("ROLE_")) {
            role = role.substring("ROLE_".length());
        }
        return getRoleList().contains(role);
    }

    @JsonIgnore
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return AuthorityUtils.commaSeparatedStringToAuthorityList(this.roles);
    }

    @JsonIgnore
    @Override
    public String getUsername() {
        return getEmail();
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @JsonIgnore
    @Override
    public boolean isEnabled() {
        return enabled;
    }

    public String getFullname() {
        return firstname + " " + lastname;
    }
}
