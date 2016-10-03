package tables;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import records.FieldInfo;
import records.Record;

public interface Table extends ReadonlyTable{

	//<T> void addField(String name, Class<T> type, boolean index, boolean unique, Function<Record, T> valueProducer);
	//default <T> void addField(String name, Class<T> type, Function<Record, T> valueProducer){
	//	addField(name, type, false, false, valueProducer);
	//}
	//default <T> void addField(String name, Class<T> type){
	//	addField(name, type, false, false, null);
	//}
	void addField(FieldInfo fieldInfo); 
	void deleteField(String name);
	void insert(String[] names, Object[] values);
	default void insert(Object[] values){
		insert(null, values);
	}
	void createIndex(String name); 
	void dropIndex(String name);
	long delete(Predicate<Record> where);
	default long update(Function<Record, Map<String, Object>> update){
		return update(update,(r) -> true);
	}
	long update(Function<Record, Map<String, Object>> function, Predicate<Record> where);
	
}
