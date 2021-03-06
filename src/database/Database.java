package database;

import java.util.function.Function;

import fields.Field;
import fields.FieldInfo;
import fields.ForeignKey;
import queries.Insert;
import records.Record;
import select.Query;
import select.Select;
import tables.ReadonlyTable;
import tables.Table;

public interface Database {
	
	public static final Object ALL = null;
	
	Table createTable(String name, FieldInfo... fields);
	Table getTable(String name);
	ReadonlyTable getTableOrView(String name);
	ReadonlyTable createView(String name, Query query);
	default <T extends Record> Select select(){
		return new Select(this);
	}
	@SuppressWarnings("unchecked")
	default <T extends Record> Select select(String name,Function<T, ?> field){
		return new Select(this, new String[]{name}, new Function[]{field});
	}
	@SuppressWarnings("unchecked")
	default <T extends Record> Select select(String name1, Function<T, ?> function1,
			String name2, Function<T, ?> function2) {
		return new Select(this, new String[]{name1,name2}, new Function[]{function1,function2});
	}
	@SuppressWarnings("unchecked")
	default <T extends Record> Select select(String name1, Function<T, ?> function1,
			String name2, Function<T, ?> function2, String name3, Function<T, ?> function3) {
		return new Select(this, new String[]{name1,name2,name3}, new Function[]{function1,function2,function3});
	}
	default <T extends Record> Select select(String field){
		return select(field, r -> r.get(field));
	}
	default Insert insertInto(String table, String... fields){
		return new Insert(getTable(table), fields);
	}
	default <T> ForeignKey<T> foreignKey(String table, String field){
		return new ForeignKey<T>(getTable(table), field);
	}
	default Table createTable(String string, Field<?>... fields){
		FieldInfo[] fieldsInfo = new FieldInfo[fields.length];
		for(int i=0;i<fields.length;i++) {
			ForeignKey<?> foreignKey = fields[i].buildForeignKey(this);
			if(foreignKey!=null){
				Class<?> otherType = foreignKey.getTable().getFieldInfo(foreignKey.getField()).getType();
				if(otherType!=fields[i].getType())
					throw new IllegalArgumentException(
							String.format("Can not create ForeignKey on different types %s, %s for field %s",
									otherType.getSimpleName(), fields[i].getType().getSimpleName(), fields[i].getName()));
			}
			fieldsInfo[i] = new FieldInfo(fields[i]);
		}
		return createTable(string, fieldsInfo);
		
	}

}
