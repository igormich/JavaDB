package utils;

import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BiPredicate;
import java.util.function.ToIntFunction;

public class ProxyEquals{
	
	public static <T,K extends T> T build(Class<T> intface, K realObject, ToIntFunction<K> hashCode, BiPredicate<K, Object> equals){
		@SuppressWarnings("unchecked")
		T result = (T) Proxy.newProxyInstance(ClassLoader.getSystemClassLoader(),
                new Class[]{intface},
                (proxy, method, args) -> {
                	if((method.getName().equals("equals"))&&(args.length==1))
                		return equals.test(realObject, args[0]);
                	if((method.getName().equals("hashCode"))&&(args.length==0))
                		return hashCode.applyAsInt(realObject);
                    return method.invoke(realObject, args);
                }
        );
		return result;
	}
	@SuppressWarnings("unchecked")
	public static void main(String[] strings) {
		List<Integer> list1 =new ArrayList<Integer>();
		list1.add(1);
		list1.add(2);
		list1.add(3);
		List<Integer> list2 =new ArrayList<Integer>();
		list2.add(3);
		list2.add(2);
		list2.add(1);
		BiPredicate<List,Object> eqwalizer = (l1,o) ->{if(o instanceof List){
			List cl1=new ArrayList<>(l1);
			List cl2=new ArrayList<>((List)o);
			Collections.sort(cl1);
			Collections.sort(cl2);
			return cl1.equals(cl2);
		}
		return false;};
		List<Integer> strangeList1 = build(List.class, list1,List::size, eqwalizer);
		List<Integer> strangeList2 = build(List.class, list2,List::size, eqwalizer);
		System.out.println(list1.equals(list2));
		System.out.println(strangeList1.equals(strangeList2));
		System.out.println(strangeList1);
		System.out.println(strangeList2);
	}

}
