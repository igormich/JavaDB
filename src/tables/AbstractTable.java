package tables;

import fields.FieldInfo;

public abstract class AbstractTable implements Table{
	protected void validate(String name, Object value) {
		FieldInfo fieldInfo = getFieldInfo(name);
		if(fieldInfo.isNotNull() && (value == null)){
			throw new IllegalArgumentException(
					String.format("Field '%s' is NOT NULL but inserted value is null", name));
		}
		if((value != null) && (value.getClass() != fieldInfo.getType())){
			throw new IllegalArgumentException(
					String.format("Field '%s' type and value type are different (%s, %s)", name, 
							value.getClass().getSimpleName(), fieldInfo.getType().getSimpleName()));
		}
		try{
			if(!fieldInfo.getConstraint().test(value)){
				throw new IllegalArgumentException(
						String.format("Constraint in field '%s' not valid for %s", name, value));
			}
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
					String.format("Constraint in field '%s' not valid for %s", name, value));
		}
		if(fieldInfo.getForeignKey()!=null)
		try{
			if(!fieldInfo.getForeignKey().test(value)){
				throw new IllegalArgumentException(
						String.format("ForeignKey in field '%s' not valid for %s", name, value));
			}
		} catch (IllegalArgumentException e) {
			throw e;
		} catch (Exception e) {
			e.printStackTrace();
			throw new IllegalArgumentException(
					String.format("ForeignKey in field '%s' not valid for %s", name, value));
		}
		if(fieldInfo.isUnique() && (contains(name, value))){
			throw new IllegalArgumentException(
					String.format("Dublicated values for unique field '%s'. Value is %s", name, value));
		}
	}
}
