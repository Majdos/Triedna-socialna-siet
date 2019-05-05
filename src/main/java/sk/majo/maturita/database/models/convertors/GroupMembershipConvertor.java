package sk.majo.maturita.database.models.convertors;

import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.data.rest.webmvc.spi.BackendIdConverter;
import org.springframework.stereotype.Component;

import sk.majo.maturita.controllers.exceptions.NotFound;
import sk.majo.maturita.database.models.GroupMembership;
import sk.majo.maturita.database.models.pk.GroupMembershipPk;

/**
 * Parses primary key from url and vice versa
 * @author Marian Lorinc
 *
 */
@Component
public class GroupMembershipConvertor implements BackendIdConverter {

	/**
	 * Regex for &lt;userId&gt;-&lt;groupId&gt
	 */
	private static final Pattern parserPattern = Pattern.compile("^(?<userId>\\d+)-(?<groupId>\\d+)$");
	
	@Override
	public boolean supports(Class<?> delimiter) {
		return delimiter.equals(GroupMembership.class);
	}

	@Override
	public Serializable fromRequestId(String id, Class<?> entityType) {
		Matcher matcher = parserPattern.matcher(id);
		if (matcher.find()) {
			return new GroupMembershipPk(Long.parseLong(matcher.group("userId")), Long.parseLong(matcher.group("groupId")));
		}
		throw new NotFound("Clenstvo neexistuje");
	}

	@Override
	public String toRequestId(Serializable id, Class<?> entityType) {
		return id.toString();
	}

}
