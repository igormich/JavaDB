package records;

import java.util.Map;
import java.util.Set;

import fields.FieldInfo;

public class MapRecord implements Record {

	private Map<String, Object> data;

	public MapRecord(Map<String, Object> data) {
		this.data = data;
	}

	@Override
	public Object get(String name) {
		return data.get(name);
	}

	@Override
	public Set<String> getFieldsNames() {
		return data.keySet();
	}

	@Override
	public FieldInfo getFieldInfo(String name) {
		return new FieldInfo(name, data.get(name).getClass());
	}

	@Override
	public String getTableName() {
		throw new UnsupportedOperationException("");
	}

}
