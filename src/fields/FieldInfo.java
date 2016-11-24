package fields;

import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.function.Supplier;



public class FieldInfo {
	
	private final String name;
	private final Class<?> type;
	private final EnumSet<FieldKey> keys;
	private final Predicate<?> constraint;
	private final Supplier<?> defaultValue;
	private final ForeignKey<?> foreignKey;
	
	public <T> FieldInfo(Field<T> field) {
		this.name = field.getName();
		this.type = field.getType();
		this.keys = field.getKeys().clone();
		this.constraint = field.getConstraint();
		this.defaultValue = field.getDefaultValue();
		this.foreignKey = field.getForeignKey();
	}

	public <T> FieldInfo(String name, Class<T> clazz) {
		this(new Field<T>(name, clazz));
	}
	
	/*public FieldInfo(String name, Class<?> clazz, EnumSet<FieldKey> keys) {
		this(name, clazz, keys, ANY, NULL_SUPPLIER);
	}
	public <T> FieldInfo(String name, Class<T> clazz, Predicate<T> constraint) {
		this(name, clazz, EMPTY_KEYS, constraint, NULL_SUPPLIER);
	}

	public <T> FieldInfo(String name, Class<T> clazz, FieldKey key) {
		this(name, clazz, EnumSet.of(key), ANY, NULL_SUPPLIER);
	}
	public <T> FieldInfo(String name, Class<T> clazz, FieldKey key, Predicate<T> constraint) {
		this(name, clazz, EnumSet.of(key), constraint, NULL_SUPPLIER);
	}

	public <T> FieldInfo(String name, Class<T> clazz, Supplier<T> defaut) {
		this(name, clazz, EMPTY_KEYS, ANY, defaut);
	}

	public <T> FieldInfo(String name, Class<T> clazz, EnumSet<FieldKey> keys, Supplier<?> defaut) {
		this(name, clazz, keys, ANY, defaut);
	}*/

	public Class<?> getType() {
		return type;
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
	public boolean isAutoIncrement() {
		return keys.contains(FieldKey.AUTO_INCREMENT);
	}
	public Object getDefault() {
		return defaultValue.get();
	}
	public Object orGetDefault(Object value) {
		return value!=null? value : getDefault();
	}
	
	public Predicate<Object> getConstraint() {
		@SuppressWarnings("unchecked")
		Predicate<Object> result = (Predicate<Object>) constraint;
		return result;
	}
	public ForeignKey<Object> getForeignKey() {
		@SuppressWarnings("unchecked")
		ForeignKey<Object> result = (ForeignKey<Object>) foreignKey;
		return result;
	}




}
