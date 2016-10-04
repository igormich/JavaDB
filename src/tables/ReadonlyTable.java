package tables;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

import records.FieldInfo;
import records.NullField;
import records.Record;

public interface ReadonlyTable extends DataSourse{
	
	public String getName();
	default NullField getNullField(){
		return new NullField(this);
	}
	Set<String> getFieldsNames();
	Map<String, FieldInfo> getFieldsInfo();
	FieldInfo getFieldInfo(String name);
	boolean isIndex(String index);
	boolean contains(String name, Object value);

	Stream<Record> getDataForIndex(String index, Object value);
}
