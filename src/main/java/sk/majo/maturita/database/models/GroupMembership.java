package sk.majo.maturita.database.models;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;

import lombok.*;
import org.springframework.hateoas.Identifiable;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;

import sk.majo.maturita.database.models.markers.Groupable;
import sk.majo.maturita.database.models.pk.GroupMembershipPk;
import sk.majo.maturita.security.Authority;
import sk.majo.maturita.security.Permission;

/**
 * @author Marian Lorinc
 */
@Data
@EqualsAndHashCode(of = { "user", "group" })
@ToString(of = { "id" })
@NoArgsConstructor

@Entity
@JsonIgnoreProperties(ignoreUnknown = true)
public class GroupMembership implements Identifiable<GroupMembershipPk>, Groupable {

	@EmbeddedId
	private GroupMembershipPk id;
	
	@MapsId("userId")
	@ManyToOne(optional = false)
	private SchoolUser user;

	@MapsId("groupId")
	@ManyToOne(optional = false)
	private Group group;

	/**
	 * if the value of variable is true, then this entity represents group invite
	 * otherwise it represents group membership
	 */
	@Column
	@Getter(AccessLevel.NONE)
	private boolean invited;
	
	@ElementCollection(targetClass = Permission.class)
	@CollectionTable(name = "group_membership_permissions")
	@Column(name = "permission", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private Set<Permission> permissions = new HashSet<>();

	public GroupMembership(SchoolUser user, Group group, Set<Permission> permissions) {
		this.user = user;
		this.group = group;
		this.permissions = permissions;
		this.id = new GroupMembershipPk(user.getId(), group.getId());
	}
	
	@Override
	public GroupMembershipPk getId() {
		return new GroupMembershipPk(user.getId(), group.getId());
	}
	
	public Set<Permission> getPermissions() {
		return new Authority(permissions).getPermissions();
	}
	
	public int getAuthorityLevel() {
		return permissions.stream().max(Comparator.comparing(Permission::ordinal)).orElse(Permission.READ).ordinal();
	}

	public boolean getInvited() {
		return invited;
	}
}
