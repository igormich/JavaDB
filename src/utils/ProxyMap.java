package utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;
import java.util.stream.Collectors;

public class ProxyMap<K, V> implements Map<K, V> {

	private class Proxy {
		K key;

		@SuppressWarnings("unchecked")
		public Proxy(Object key) {
			this.key = (K) key;
		}

		@Override
		public int hashCode() {
			try{
			return hashCode.applyAsInt(key);
			} catch (ClassCastException e) {
				return 0;
			}
		}

		@Override
		public boolean equals(Object obj) {
			try{
			@SuppressWarnings("unchecked")
			Proxy other=(ProxyMap<K, V>.Proxy) obj;
			return equals.test(key, other.key);
			} catch (ClassCastException e) {
				return false;
			}
			
		}

		public K getKey() {
			return key;
		}
	}
	private class Entry implements Map.Entry<K, V> {

		private java.util.Map.Entry<ProxyMap<K, V>.Proxy, V> proxy;

		public Entry(Map.Entry<Proxy, V> proxy){
			this.proxy = proxy;
			
		}

		@Override
		public K getKey() {
			return proxy.getKey().getKey();
		}

		@Override
		public V getValue() {
			return proxy.getValue();
		}

		@Override
		public V setValue(V arg0) {
			return proxy.setValue(arg0);
		}

		@Override
		public String toString() {
			return "Entry [key=" + getKey() + ", value=" + getValue() + "]";
		}

	}
	private Map<Proxy, V> actualMap;
	private ToIntFunction<K> hashCode;
	private BiPredicate<K, K> equals;

	public ProxyMap(ToIntFunction<K> hashCode, BiPredicate<K, K> equals) {
		super();
		this.actualMap = new HashMap<Proxy, V>();
		this.hashCode = hashCode;
		this.equals = equals;
	}
	
	@Override
	public void clear() {
		actualMap.clear();
	}

	@Override
	public boolean containsKey(Object key) {
		return actualMap.containsKey(new Proxy(key));
	}

	@Override
	public boolean containsValue(Object value) {
		return actualMap.containsValue(value);
	}

	@Override
	public Set<java.util.Map.Entry<K, V>> entrySet() {
		return actualMap.entrySet().stream().map(Entry::new).collect(Collectors.toSet());
	}

	@Override
	public V get(Object key) {
		return actualMap.get(new Proxy(key));
	}

	@Override
	public boolean isEmpty() {
		return actualMap.isEmpty();
	}

	@Override
	public Set<K> keySet() {
		return actualMap.keySet().stream().map(Proxy::getKey).collect(Collectors.toSet());
	}

	@Override
	public V put(K key, V value) {
		return actualMap.put(new Proxy(key), value);
	}

	@Override
	public void putAll(Map<? extends K, ? extends V> map) {
		map.entrySet().stream().forEach(e -> put(e.getKey(), e.getValue()));
	}

	@Override
	public V remove(Object arg0) {
		return actualMap.remove(new Proxy(arg0));
	}

	@Override
	public int size() {
		return actualMap.size();
	}

	@Override
	public Collection<V> values() {
		return actualMap.values();
	}
	
	public static void main(String[] args) {
		Map<Integer, String> map=new ProxyMap<Integer, String>(
				(i) -> Integer.hashCode(Math.abs(i)), 
				(i1, i2) -> Math.abs(i1) == Math.abs(i2));
		map.put(1, "a");
		map.put(2, "b");
		map.put(3, "c");
		map.put(-2, "d");
		map.put(-6, "e");
		map.put(6, "f");
		map.entrySet().forEach(System.out::println);
		System.out.println(map.get("123"));
	}
}