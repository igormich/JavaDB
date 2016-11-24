package tables;
import java.util.Map;
import java.util.function.Function;
import java.util.function.Predicate;

import database.DataSourse;
import fields.FieldInfo;
import records.Record;

public interface Table extends ReadonlyTable{

	void addField(FieldInfo fieldInfo); 
	void deleteField(String name);
	void insert(Record values);
	default void insert(DataSourse dataSourse){
		dataSourse.getData().forEach(this::insert);
	}
	void createIndex(String name); 
	void dropIndex(String name);
	long delete(Predicate<Record> where);
	default long update(Function<Record, Map<String, Object>> update){
		return update(update,(r) -> true);
	}
	long update(Function<Record, Map<String, Object>> function, Predicate<Record> where);
}
