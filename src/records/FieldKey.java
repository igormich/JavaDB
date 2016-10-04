package records;

public enum FieldKey {
	PRIMARY_KEY, INDEX, UNIQUE, NOT_NULL, AUTO_INCREMENT;
	boolean isIndex(){
		return this == INDEX || this == PRIMARY_KEY;
	}
	boolean isNotNull(){
		return this == INDEX || this == PRIMARY_KEY || this == NOT_NULL || this == AUTO_INCREMENT;
	}
	boolean isUnique(){
		return this == UNIQUE || this == PRIMARY_KEY || this == AUTO_INCREMENT;
	}
}
