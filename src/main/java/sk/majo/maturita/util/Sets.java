package sk.majo.maturita.util;

import java.util.HashSet;
import java.util.Set;

/**
 * @deprecated This project was written in java 1.8. This class isn't longer needed.
 * @author Majo
 *
 */
public class Sets {

	@SafeVarargs
	public static <T> Set<T> of(T ...items) {
		HashSet<T> set = new HashSet<>();
		for (T t : items) {
			set.add(t);
		}
		return set;
	}
	
	public static <T> Set<T> emptySet() {
		return new HashSet<>();
	}
	
}
