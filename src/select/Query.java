package select;

import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import fields.FieldInfo;
import records.Record;

public interface Query {
	
	public default Iterator<? extends Record> execute() {
		return getData().iterator();
	}
	
	Stream<? extends Record> getData();
	Stream<? extends Record> getFullData();
	//Set<String> getFieldsNames();
	//Map<String, FieldInfo> getFieldsInfo();
}
