package utils;
import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class Pair<A, B> implements Map<A,B>{
    private A first;
    private B second;

    private class Entry implements Map.Entry<A, B>{

		@Override
		public A getKey() {
			return first;
		}

		@Override
		public B getValue() {
			return second;
		}

		@Override
		public B setValue(B value) {
			B oldValue = second;
			second = value;
			return oldValue;
		}
    	
    }
    public Pair(A first, B second) {
    	super();
    	this.first = first;
    	this.second = second;
    }

    public int hashCode() {
    	int hashFirst = first != null ? first.hashCode() : 0;
    	int hashSecond = second != null ? second.hashCode() : 0;

    	return (hashFirst + hashSecond) * hashSecond + hashFirst;
    }

    public boolean equals(Object other) {
    	if (other instanceof Pair) {
    		@SuppressWarnings("rawtypes")
			Pair otherPair = (Pair) other;
    		return 
    		((  this.first == otherPair.first ||
    			( this.first != null && otherPair.first != null &&
    			  this.first.equals(otherPair.first))) &&
    		 (	this.second == otherPair.second ||
    			( this.second != null && otherPair.second != null &&
    			  this.second.equals(otherPair.second))) );
    	}

    	return false;
    }

    public String toString()
    { 
           return "(" + first + ", " + second + ")"; 
    }

    public A getFirst() {
    	return first;
    }

    public void setFirst(A first) {
    	this.first = first;
    }

    public B getSecond() {
    	return second;
    }

    public void setSecond(B second) {
    	this.second = second;
    }

	@Override
	public void clear() {
		
	}

	@Override
	public boolean containsKey(Object key) {
		return Objects.equals(first, key);
	}

	@Override
	public boolean containsValue(Object value) {
		return Objects.equals(second, value);
	}

	@Override
	public Set<java.util.Map.Entry<A, B>> entrySet() {
		return Collections.singleton(new Entry());
	}

	@Override
	public B get(Object key) {
		return second;
	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public Set<A> keySet() {
		return Collections.singleton(first);
	}

	@Override
	public B put(A key, B value) {
		throw new UnsupportedOperationException();
	}

	@Override
	public void putAll(Map<? extends A, ? extends B> m) {
		throw new UnsupportedOperationException();
	}

	@Override
	public B remove(Object key) {
		throw new UnsupportedOperationException();
	}

	@Override
	public int size() {
		return 1;
	}

	@Override
	public Collection<B> values() {
		return Collections.singleton(second);
	}
}