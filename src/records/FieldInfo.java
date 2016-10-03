package records;

import java.util.EnumSet;
import java.util.function.Predicate;



public class FieldInfo {

	public static final int PRIMARY_KEY = 0;
	
	private static final EnumSet<FieldKey> EMPTY_KEYS = EnumSet.noneOf(FieldKey.class);
	private static final Predicate<?> ANY = (o) -> true;
	
	private final String name;
	private final Class<?> clazz;
	private final EnumSet<FieldKey> keys;
	private final Predicate<?> constraint;

	public <T> FieldInfo(String name, Class<?> clazz, EnumSet<FieldKey> keys, Predicate<T> constraint) {
		this.name = name;
		this.clazz = clazz;
		this.keys = keys;
		this.constraint = constraint;
	}

	public FieldInfo(String name, Class<?> clazz) {
		this(name, clazz, EMPTY_KEYS, ANY);
	}
	
	public FieldInfo(String name, Class<?> clazz, EnumSet<FieldKey> keys) {
		this(name, clazz, keys, ANY);
	}
	public <T> FieldInfo(String name, Class<T> clazz, Predicate<T> constraint) {
		this(name, clazz, EMPTY_KEYS, constraint);
	}

	public Class<?> getType() {
		return clazz;
	}

	public String getName() {
		return name;
	}

	public boolean isIndex() {
		return keys.stream().anyMatch(FieldKey::isIndex);
	}

	public boolean isNotNull() {
		return keys.stream().anyMatch(FieldKey::isNotNull);
	}

	public boolean isUnique() {
		return keys.stream().anyMatch(FieldKey::isUnique);
	}

	public Predicate<Object> getConstraint() {
		@SuppressWarnings("unchecked")
		Predicate<Object> result = (Predicate<Object>) constraint;
		return result;
	}

}
