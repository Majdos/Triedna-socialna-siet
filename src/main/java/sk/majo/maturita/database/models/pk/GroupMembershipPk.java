package sk.majo.maturita.database.models.pk;

import java.io.Serializable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * @author Marian Lorinc
 */
@Data
@EqualsAndHashCode(of = { "userId", "groupId" })
@NoArgsConstructor
@AllArgsConstructor
public class GroupMembershipPk implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Long userId;
	private Long groupId;
	
	@Override
	public String toString() {
		return userId + "-" + groupId;
	}
}
