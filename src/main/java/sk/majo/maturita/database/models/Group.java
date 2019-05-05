package sk.majo.maturita.database.models;

import static javax.persistence.CascadeType.ALL;

import java.io.Serializable;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.annotations.NaturalId;
import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import sk.majo.maturita.database.models.enums.GroupType;
import sk.majo.maturita.security.Permission;

/**
 * @author Marian Lorinc
 */
@Data
@EqualsAndHashCode(of = { "name" })
@NoArgsConstructor

@ToString(of = { "name", "type", "imagePath" })

@Entity(name = "Groups")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Group implements Identifiable<Long>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Global group, where everyone has access
	 */
	public static final String GLOBAL_GROUP = "SPSJM news";

	/**
	 * Path to images
	 */
	public static final Path IMAGES_ROOT = Paths.get("/images");
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	@JsonIgnore
	private Long id;
	
	@NaturalId
	@Column(length = 25, nullable = false)
	@Pattern(regexp = "^(?:[1-9]\\.[A-Z]|[a-zA-Z][A-Za-z0-9]+(?:[ _-][A-Za-z0-9]+)*)$")
	private String name;

	/**
	 * Type of group
	 */
	@Column(nullable = false)
	@Enumerated(EnumType.ORDINAL)
	private GroupType type;
	
	@Column(length = 50)
	private String description;
	
	@ElementCollection
	@CollectionTable(name="keywords", joinColumns=@JoinColumn(name="group_id"))
	@Column(name= "keyword", length= 20)
	@Size(max = 10, message = "Prekroceny limit klucovych slov")
	private Set<String> keywords = new HashSet<>();

	/**
	 * Location of group's images
	 */
	@Column(length = 20)
	private String imagePath;

	@OneToMany(mappedBy = "group", cascade = { ALL }, orphanRemoval = true)
	@JsonIgnore
	private List<GroupMembership> members = new ArrayList<>();

	@OneToMany(mappedBy = "group", cascade = { ALL }, orphanRemoval = true)
	@JsonIgnore
	private List<Article> articles = new ArrayList<>();
	
	
	public Group(String name) {
		this.name = name;
	}

    public Group(String name, SchoolUser owner) {
    	this.name = name;
		addSuperUser(owner);
	}
	   
	public void addAdmin(SchoolUser schoolUser) {
		addMember(schoolUser, Permission.ADMIN);
	}

	public void addArticle(Article article) {
		articles.add(article);
		article.setGroup(this);
	}
	
	public void addMember(SchoolUser schoolUser, Set<Permission> permissions) {
		GroupMembership membership = new GroupMembership(schoolUser, this, permissions);
		members.add(membership);
		schoolUser.getGroupMemberships().add(membership);
	}
	
	public void addMember(SchoolUser schoolUser, Permission ... permissions) {
		addMember(schoolUser, new HashSet<>(Arrays.asList(permissions)));
	}
	
	public void addMember(SchoolUser schoolUser) {
		addMember(schoolUser, Permission.WRITE);
	}

	public void addSuperUser(SchoolUser schoolUser) {
		addMember(schoolUser, Permission.ROOT);
	}
	
	public void addKeywords(String ...keywords) {
		this.keywords.addAll(Arrays.asList(keywords));
	}
	
	public void removeKeywords(String ...keywords) {
		this.keywords.removeAll(Arrays.asList(keywords));
	}
			
	public void removeArticle(Article article) {
		articles.remove(article);
		article.setGroup(null);
	}

	/**
	 * Resolves directory, whe are stored images
	 * @return images directory path
	 */
	@JsonIgnore
	public Path getImagesRootPath() {
		return IMAGES_ROOT.resolve(name);
	}
	
	public String getImagesRoot() {
		return getImagesRootPath().toString().replace('\\', '/');
	}
	
	/**
	 * Returns jumbotron image of group.
	 * @return path to jumbotron image of group
	 */
	public String getImagePath() {
		return imagePath != null ? getImagesRootPath().resolve(imagePath).toString().replace('\\', '/') : null;
	}

    public int getMembersCount() {
        return members.size();
    }		
}
