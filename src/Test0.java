import java.awt.Image;
import java.util.EnumSet;

import database.MemoryDatabase;
import records.FieldInfo;
import records.FieldKey;


public class Test0 {

	private static MemoryDatabase database;
	
	private static void createTable() {
		try{
			database.createTable("humans",
					new FieldInfo("name", String.class, EnumSet.of(FieldKey.PRIMARY_KEY)),
					//error here, non Comparable can not be used as Index
					new FieldInfo("portrait", Image.class, EnumSet.of(FieldKey.INDEX)));
		}catch(Exception e){
			System.out.println("ERROR:"+e.getMessage());
		}
		
		database.createTable("humans",
				new FieldInfo("name", String.class, EnumSet.of(FieldKey.PRIMARY_KEY)),
				new FieldInfo("sex", String.class, (o) -> "male".equals(o) || "female".equals(o)),
				new FieldInfo("age", Integer.class, EnumSet.of(FieldKey.NOT_NULL))
		);
	}
	
	private static void insert() {
		try{
			System.out.println("database.insertInto(\"humans\").values(\"Oleg\", null, 33)");
			//error here, table monkeys not created
			database.insertInto("monkeys").values("Bobo", "man", 5);
		}catch(Exception e){
			System.out.println("ERROR:"+e.getMessage());
		}
		try{
			System.out.println("database.insertInto(\"humans\").values(\"Oleg\", null, 33)");
			//error here, field sex can not be "superman" (male or female only)
			database.insertInto("humans").values("Oleg", "superman", 33);
		}catch(Exception e){
			System.out.println("ERROR:"+e.getMessage());
		}
		database.insertInto("humans").values("Oleg", "male", 33);
		try{
			System.out.println("database.insertInto(\"humans\").values(\"Masha\", 27, \"female\")");
			//error here, 
			database.insertInto("humans").values("Masha", 27, "female");
		}catch(Exception e){
			System.out.println("ERROR:"+e.getMessage());
		}
		database.insertInto("humans","name","age","sex").values("Masha", 27, "female");
		try{
			System.out.println("database.insertInto(\"humans\",\"name\",\"age\",\"sex\")).values(\"Masha\", 27, \"female\")");
			database.insertInto("humans","name","age","sex").values("Masha", 27, "female");
		}catch(Exception e){
			System.out.println("ERROR:"+e.getMessage());
		}
		try{
			//error here, age can not be null
			System.out.println("database.insertInto(\"humans\",\"name\",\"age\",\"sex\")).values(\"Natasha\", 27, \"female\")");
			database.insertInto("humans","name","age","sex").values("Natasha", null, "female");
		}catch(Exception e){
			System.out.println("ERROR:"+e.getMessage());
		}
		database.insertInto("humans","name","age","sex").values("Natasha", 16, "female");
	}
	
	private static void select() {
		database.select("name")
		.from("humans")
		.where(r -> "female".equals(r.get("sex")))
		.execute()
		.forEachRemaining(System.out::println);
	try{
		database.select(
				"sex", r -> r.get("sex"),
				"count", r -> r.count(),
				"avg(Age)", r -> r.avg("age"))
			.from("humans")//error here, no groupBy
			.execute()
			.forEachRemaining(System.out::println);
	}catch(Exception e){
		System.out.println("ERROR:"+e.getMessage());
	}
	try{
		database.select(
				"sex", r -> r.get("sex"),//error here, don't use get for groupBy
				"count", r -> r.count(),
				"avg(Age)", r -> r.get("age"))
			.from("humans")
			.groupBy("sex")
			.execute()
			.forEachRemaining(System.out::println);
	}catch(Exception e){
		System.out.println("ERROR:"+e.getMessage());
	}
	database.select(
			"sex", r -> r.get("sex"),
			"count", r -> r.count(),
			"avg(Age)", r -> r.avg("age"))
		.from("humans")
		.groupBy("sex")
		.orderBy("count")
		.execute()
		.forEachRemaining(System.out::println);
		
	}
	
	public static void main(String[] args) {
		database = new MemoryDatabase();
		createTable();
		insert();
		select();
	}


}
