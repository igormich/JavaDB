package fields;

import java.util.EnumSet;
import java.util.function.Predicate;
import java.util.function.Supplier;

import database.Database;
import utils.Pair;

public class Field<T>{
	
	public static Supplier<Integer> autoincrementI() {
		int[] i = new int[]{0};
		return () -> i[0]++;
	}
	public static Supplier<Long> autoincrementL() {
		long[] l = new long[]{0};
		return () -> l[0]++;
	}
	
	private String name;
	private Class<T> type;
	private EnumSet<FieldKey> keys = EnumSet.noneOf(FieldKey.class);
	private Predicate<T> constraint = (o) -> true;
	private Supplier<T> defaultValue = () -> null;
	private ForeignKey<T> foreignKey = null;
	private Pair<String, String> foreignKeyInfo = null;
	
	public static <T> Field<T> field(String name, Class<T> type){
		return new Field<T>(name, type);
	}
	public Field(String name, Class<T> type){
		this.name = name;
		this.type = type;
	}
	
	public Field<T> primaryKey() {
		keys.add(FieldKey.PRIMARY_KEY);
		return this;
	}
	public Field<T> index() {
		keys.add(FieldKey.INDEX);
		return this;
	}
	public Field<T> unique() {
		keys.add(FieldKey.UNIQUE);
		return this;
	}
	public Field<T> notNull() {
		keys.add(FieldKey.NOT_NULL);
		return this;
	}
	public Field<T> autoIncrement() {
		keys.add(FieldKey.AUTO_INCREMENT);
		return this;
	}
	public Field<T> constraint(Predicate<T> constraint) {
		this.constraint = constraint;
		return this;
	}
	public Field<T> defaultValue(Supplier<T> defaultValue) {
		this.defaultValue = defaultValue;
		return this;
	}
	
	public Class<T> getType() {
		return type;
	}
	public String getName() {
		return name;		
	}
	public EnumSet<FieldKey> getKeys() {
		return keys;
	}
	public Predicate<T> getConstraint() {
		return constraint; 
	}
	@SuppressWarnings("unchecked")
	public Supplier<T>  getDefaultValue() {
		if(isAutoIncrement()){
			if (this.type == Integer.class)
				return (Supplier<T>) autoincrementI();
			else if (this.type == Long.class)
				return (Supplier<T>) autoincrementL();
			else
				throw new IllegalArgumentException("AUTO_INCREMENT can be applied only to Long or Integer types");
		}
		return defaultValue;
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
		public ForeignKey<T> getForeignKey() {
			return foreignKey;
		}
		public Field<T> foreignKey(String table, String field) {
			foreignKeyInfo  = new Pair<String, String>(table, field);
			return this;
		}
		public ForeignKey<?> buildForeignKey(Database database) {
			if(foreignKeyInfo!=null)
				foreignKey = new ForeignKey<T>(database.getTable(foreignKeyInfo.getFirst()), foreignKeyInfo.getSecond());
			return foreignKey;
		}
		public Field<T> keys(EnumSet<FieldKey> keys) {
			this.keys = keys;
			return this;
		}
}
