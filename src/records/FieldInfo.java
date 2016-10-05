package records;

import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Stream;



public class FieldInfo {

	public static final int PRIMARY_KEY = 0;
	
	public static Supplier<Object> autoincrimentI() {
		int[] i = new int[]{0};
		return () -> i[0]++;
	}
	public static Supplier<Object> autoincrimentL() {
		long[] l = new long[]{0};
		return () -> l[0]++;
	}
	private static final EnumSet<FieldKey> EMPTY_KEYS = EnumSet.noneOf(FieldKey.class);
	private static final Predicate<?> ANY = (o) -> true;
	private static final Supplier<?> NULL_SUPPLIER= () -> null;
	
	private final String name;
	private final Class<?> clazz;
	private final EnumSet<FieldKey> keys;
	private final Predicate<?> constraint;
	private final Supplier<?> defaultValue;
	
	public <T> FieldInfo(String name, Class<?> clazz, EnumSet<FieldKey> keys, Predicate<T> constraint,Supplier<?> defaultValue) {
		this.name = name;
		this.clazz = clazz;
		this.keys = keys;
		this.constraint = constraint;
		if(isAutoIncrement()){
			if (this.clazz == Integer.class)
				this.defaultValue =  autoincrimentI();
			else if (this.clazz == Long.class)
				this.defaultValue =  autoincrimentL();
			else
				throw new IllegalArgumentException("AUTO_INCREMENT can be applied only to Long or Integer types");
			
		} else {
			this.defaultValue = defaultValue;
		}
	}

	public FieldInfo(String name, Class<?> clazz) {
		this(name, clazz, EMPTY_KEYS, ANY, NULL_SUPPLIER);
	}
	
	public FieldInfo(String name, Class<?> clazz, EnumSet<FieldKey> keys) {
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

	public void validate(Object value) {
		if(isNotNull() && (value == null)){
			throw new IllegalArgumentException(
					String.format("Field '%s' is NOT NULL but inserted value is null", name));
		}
		if((value != null) && (value.getClass() != getType())){
			throw new IllegalArgumentException(
					String.format("Field '%s' type and value type are different (%s, %s)", name, 
							value.getClass().getSimpleName(), getType().getSimpleName()));
		}
		try{
			if(!getConstraint().test(value)){
				throw new IllegalArgumentException(
						String.format("Constraint in field '%s' not valid for %s", name, value));
			}
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
					String.format("Constraint in field '%s' not valid for %s", name, value));
		}
	}



}
