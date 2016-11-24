package records;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import fields.FieldInfo;
import tables.ReadonlyTable;

public class DefaultRecord implements Record {

	private static Map<Class<?>,Object> standartTypes = new HashMap<>();
	static{{
		standartTypes.put(Long.class, Long.valueOf(0));
		standartTypes.put(Integer.class, Integer.valueOf(0));
		standartTypes.put(Short.class, Short.valueOf((short) 0));
		standartTypes.put(Byte.class, Byte.valueOf((byte) 0));
		standartTypes.put(Float.class, Float.valueOf(0));
		standartTypes.put(Double.class, Double.valueOf(0));
		standartTypes.put(String.class, "");
		standartTypes.put(Boolean.class, Boolean.FALSE);
	}}
	
	private ReadonlyTable table;

	public DefaultRecord(ReadonlyTable table) {
		this.table = table;
	}

	@Override
	public Object get(String name) {
		FieldInfo fieldInfo = getFieldInfo(name);
		Object result = standartTypes.get(fieldInfo.getType());
		if(result!= null)
			return result;
		try {
			result = fieldInfo.getType().newInstance();
		} catch (Exception sadButTrue) {
			sadButTrue.printStackTrace();
		}
		return result;
	}
	public long count(){
		return 0;
	}
	public Object max(String name){
		return get(name);
	}
	public Object min(String name){
		return get(name);
	}
	public Number avg(String name){
		if(getFieldInfo(name).getType() == Double.class)
			return Double.valueOf(0);
		if(getFieldInfo(name).getType() == Long.class)
			return Double.valueOf(0);
		if(getFieldInfo(name).getType() == Integer.class)
			return Double.valueOf(0);
		if(getFieldInfo(name).getType() == Float.class)
			return Double.valueOf(0);
		throw new IllegalStateException("Avg operation can be  can be applied for only number types");
	}
	public Number sum(String name){
		if(getFieldInfo(name).getType() == Double.class)
			return Double.valueOf(0);
		if(getFieldInfo(name).getType() == Long.class)
			return Double.valueOf(0);
		if(getFieldInfo(name).getType() == Integer.class)
			return Double.valueOf(0);
		if(getFieldInfo(name).getType() == Float.class)
			return Double.valueOf(0);
		throw new IllegalStateException("Avg operation can be  can be applied for only number types");
	}
	@Override
	public Set<String> getFieldsNames() {
		return table.getFieldsNames();
	}

	@Override
	public String getTableName() {
		return table.getName();
	}
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append('[');
		getFieldsNames().stream().forEach(field ->{result.append(field);
												  result.append(':');
												  result.append(get(field));
												  result.append(',');
												  });
		result.setLength(result.length()-1);
		result.append(']');
		return result.toString();
	}

	@Override
	public int hashCode() {
		return 31 * Objects.hashCode(table);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DefaultRecord other = (DefaultRecord) obj;
		return Objects.equals(table, other.table);
	}

	@Override
	public FieldInfo getFieldInfo(String name) {
		return table.getFieldInfo(name);
	}

}
