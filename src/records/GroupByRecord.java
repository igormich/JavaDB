package records;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

public class GroupByRecord implements Record {

	private List<Record> list;
	private Object value;
	private String field;

	public GroupByRecord(String field, Object value, List<Record> list) {
		this.field = field;
		this.value = value;
		this.list = list;
	}
	@Override
	public Object get(String name) {
		if(name.equals(field))
			return value;
		if(list.get(0).getFieldsNames().contains(name))
				throw new IllegalStateException("Get can be applyed only for fields used in GroupBy");
		throw new RuntimeException(String.format("Can not found data for '%s'", name));	
	}
	@Override
	public Object max(String name) {
		@SuppressWarnings("unchecked")
		Object result = list.stream().map(r -> r.get(name)).max((Comparator<Object>) Comparator.naturalOrder()).get();
		return result;
	}
	@Override
	public Object min(String name) {
		@SuppressWarnings("unchecked")
		Object result = list.stream().map(r -> r.get(name)).min((Comparator<Object>) Comparator.naturalOrder()).get();
		return result;
	}
	
	public long count() {
		return list.size();
	}
	@Override
	public Number avg(String name) {
		if(getFieldInfo(name).getType() == Double.class)
			return list.stream().mapToDouble(r -> r.getDouble(name)).average().getAsDouble();
		if(getFieldInfo(name).getType() == Long.class)
			return list.stream().mapToDouble(r -> r.getLong(name)).average().getAsDouble();
		if(getFieldInfo(name).getType() == Integer.class)
			return (float)list.stream().mapToDouble(r -> r.getInt(name)).average().getAsDouble();
		if(getFieldInfo(name).getType() == Float.class)
			return (float)list.stream().mapToDouble(r -> r.getFloat(name)).average().getAsDouble();
		throw new IllegalStateException("Avg operation can be  can be applied for only number types");
	}
	@Override
	public Number sum(String name) {
		if(getFieldInfo(name).getType() == Double.class)
			return list.stream().mapToDouble(r -> r.getDouble(name)).sum();
		if(getFieldInfo(name).getType() == Long.class)
			return list.stream().mapToLong(r -> r.getLong(name)).sum();;
		if(getFieldInfo(name).getType() == Integer.class)
			return list.stream().mapToInt(r -> r.getInt(name)).sum();
		if(getFieldInfo(name).getType() == Float.class)
			return (float)list.stream().mapToDouble(r -> r.getFloat(name)).sum();
		throw new IllegalStateException("Avg operation can be  can be applied for only number types");
	}
	@Override
	public Set<String> getFieldsNames() {
		return list.get(0).getFieldsNames();
	}

	@Override
	public String getTableName() {
		return "TEMPORALLY TABLE: GROUP BY ";
	}
	@Override
	public FieldInfo getFieldInfo(String name) {
		return list.get(0).getFieldInfo(name);
	}

}
