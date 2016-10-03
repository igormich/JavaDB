package records;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

public class RenameRecord implements Record{
	
	private Record sourse;
	private Map<String,Function<Record,?>> renameMap;
	
	public RenameRecord(Record record,String[] names,Function<Record,?>[] values) {
		sourse = record;
		renameMap = new HashMap<>(names.length,1.1f);
		for(int i=0;i<names.length;i++)
			renameMap.put(names[i], values[i]);
	}

	@Override
	public Object get(String name) {
		return renameMap.get(name).apply(sourse);
	}

	@Override
	public Set<String> getFieldsNames() {
		return renameMap.keySet();
	}

	@Override
	public FieldInfo getFieldInfo(String name) {
		return null;
	}

	@Override
	public String getTableName() {
		return "TEMPORALLY TABLE: RENAME";
	}
	public String toString() {
		StringBuilder result = new StringBuilder();
		result.append('[');
		getFieldsNames().stream().forEach(field ->{result.append(field);
												  result.append(':');
												  result.append(get(field));
												  result.append(", ");
												  });
		result.setLength(result.length()-2);
		result.append(']');
		return result.toString();
	}
}
