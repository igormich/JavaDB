package records;
import java.util.Objects;
import java.util.Set;

import tables.ReadonlyTable;

public class NullField implements Record {

	private ReadonlyTable table;

	public NullField(ReadonlyTable table) {
		this.table = table;
	}

	@Override
	public Object get(String name) {
		return null;
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
		NullField other = (NullField) obj;
		return Objects.equals(table, other.table);
	}

	@Override
	public FieldInfo getFieldInfo(String name) {
		return table.getFieldInfo(name);
	}
}
