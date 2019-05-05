package sk.majo.maturita.database.models.definitions;


import com.fasterxml.jackson.annotation.JsonIgnoreType;
import lombok.*;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Locale;
import java.util.Map;

/**
 * Represents data returned from Google OAuth2 authentication.
 * @author Marian Lorinc
 */

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode
@JsonIgnoreType
public class GoogleUser {

    private String id;
    private String email;
    // private final boolean emailVerified;
    private String userName;
    private String givenName;
    private String familyName;
    private URI link;
    private URI picture;
    private String gender;
    private Locale locale;
    private String hostDomain;

    public static GoogleUser fromUserInfoMap(Map<String, Object> map) {
        URI link;
        if (map.containsKey("link")) {
            link = getUri(map, "link");
        } else {
            link = getUri(map, "profile");
        }
        URI picture = getUri(map, "picture");
        return new GoogleUser(
                (String) (map.containsKey("id") ? map.get("id") : map.get("sub")),
                (String) map.get("email"),
                (String) map.get("name"),
                (String) map.get("given_name"),
                (String) map.get("family_name"),
                link,
                picture,
                (String) map.get("gender"),
                new Locale((String) map.get("locale")),
                (String) map.get("hd"));
    }

    private static URI getUri(Map<String, Object> map, String key) {
        URI uri = null;
        try {
            if (map.containsKey(key)) {
                uri = new URI((String) map.get(key));
            }
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
        return uri;
    }
}