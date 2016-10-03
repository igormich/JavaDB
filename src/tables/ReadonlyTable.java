package tables;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import records.FieldInfo;
import records.NullField;
import records.Record;

public interface ReadonlyTable {
	
	public String getName();
	default NullField getNullField(){
		return new NullField(this);
	}
	TableStream<? extends Record> getRecords();
	Set<String> getFieldsNames();
	Map<String, FieldInfo> getFieldsTypes();
	TableStream<? extends Record> getRecordsForIndex(String field);
	TableStream<? extends Record> getRecordsForIndex(String field, Object value);
	FieldInfo getFieldInfo(String name);
	boolean isIndex(String index);
	boolean contains(String name, Object value);
	public Stream<Record> getData();
}
