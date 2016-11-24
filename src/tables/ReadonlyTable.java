package tables;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import database.DataSourse;
import fields.FieldInfo;
import records.NullRecord;
import records.Record;

public interface ReadonlyTable extends DataSourse{
	
	public String getName();
	default NullRecord getNullField(){
		return new NullRecord(this);
	}
	Set<String> getFieldsNames();
	Map<String, FieldInfo> getFieldsInfo();
	FieldInfo getFieldInfo(String name);
	boolean isIndex(String index);
	boolean contains(String name, Object value);

	Stream<Record> getDataForIndex(String index, Object value);
	
	default Record getNullRecord(){
		return new NullRecord(this);
	}
}
