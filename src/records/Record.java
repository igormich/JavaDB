package records;
import java.util.Set;

import fields.FieldInfo;

public interface Record {
	Object get(String name);
	default <T> T getAs(String name){
		@SuppressWarnings("unchecked")
		T result = (T) get(name);
		return result;
	}
	default String getString(String name) {
		return get(name).toString();
	}
	default int getInt(String name){
		return (int) get(name);
	}
	default long getLong(String name) {
		return (long) get(name);
	}
	default double getDouble(String name) {
		return (double) get(name);
	}
	default float getFloat(String name) {
		return (float) get(name);
	}
	default boolean getBoolean(String name) {
		return (boolean) get(name);
	}
	
	Set<String> getFieldsNames();
	FieldInfo getFieldInfo(String name);
	String getTableName();

	default long count(){
		throw new UnsupportedOperationException("Count can be applyed only for GroupBy result");
	}
	default Object max(String name){
		throw new UnsupportedOperationException("Max can be applyed only for GroupBy result");
	}
	default Object min(String name){
		throw new UnsupportedOperationException("Min can be applyed only for GroupBy result");
	}
	default Number avg(String name){
		throw new UnsupportedOperationException("Avg can be applyed only for GroupBy result");
	}
	default Number sum(String name){
		throw new UnsupportedOperationException("Avg can be applyed only for GroupBy result");
	}
}
