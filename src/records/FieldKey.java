package records;

public enum FieldKey {
	PRIMARY_KEY, INDEX, UNIQUE, NOT_NULL;
	boolean isIndex(){
		return this == INDEX || this == PRIMARY_KEY;
	}
	boolean isNotNull(){
		return this == INDEX || this == PRIMARY_KEY || this == NOT_NULL;
	}
	boolean isUnique(){
		return this == UNIQUE || this == PRIMARY_KEY;
	}
}
