package jdb.collection;

public class SoftArrayTest {

	public static void main(String[] args) {
		SoftArray<Integer> softArray = new SoftArray<>();
		softArray.add(1);
		softArray.add(2);
		softArray.add(3);
		softArray.stream().forEach(System.out::println);
		System.out.println("softArray.remove(1); //"+softArray.remove(1));	
		softArray.stream().forEach(System.out::println);
		System.out.println("softArray.add(0,4);");	
		softArray.add(0,4);
		softArray.stream().forEach(System.out::println);
	}

}
