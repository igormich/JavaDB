package examples;

import java.awt.image.BufferedImage;
import java.util.Date;

import database.MemoryDatabase;
import static records.Field.field;
import utils.Pair;


public class SingleTableOperations {

	private static MemoryDatabase database;
	
	private static void createTable() {
		try{
			database.createTable("humans",
					field("name", String.class).primaryKey(),
					//error here, non Comparable can not be used as Index
					field("portrait", BufferedImage.class).index());
		}catch(Exception e){
			System.out.println("ERROR:"+e.getMessage());
		}
		
		database.createTable("humans",
				field("id", Long.class).primaryKey().autoIncrement(),
				field("name", String.class).unique(),
				field("sex", String.class).constraint((o) -> "male".equals(o) || "female".equals(o)),
				field("age", Integer.class).notNull(),
				field("alive", Boolean.class).defaultValue(() -> true),
				field("createDate", String.class).defaultValue(() -> new Date().toString())
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
		database.insertInto("humans").values("Oleg", "male", 25);
		try{
			System.out.println("database.insertInto(\"humans\").values(\"Masha\", 27, \"female\")");
			//error here, 
			database.insertInto("humans").values("Masha", 27, "female");
		}catch(Exception e){
			System.out.println("ERROR:"+e.getMessage());
		}
		database.insertInto("humans","name","age","sex").values("Masha", 27, "female");
		try{
			//error here, dublicated names 
			System.out.println("database.insertInto(\"humans\",\"name\",\"age\",\"sex\")).values(\"Masha\", 27, \"female\")");
			database.insertInto("humans","name","age","sex").values("Masha", 29, "male");
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
		database.insertInto("humans","name","age","sex").values("Nikola", 43, "male");
		database.insertInto("humans","name","age","sex").values("Anfisa", 25, "female");
	}
	
	private static void select() {
		System.out.println("Show names");
		database.select("name")
			.from("humans")
			.execute()
			.forEachRemaining(System.out::println);
		System.out.println("Show all data from table");
		database.select()
			.from("humans")
			.execute()
			.forEachRemaining(System.out::println);
		System.out.println("Show female");
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
		System.out.println("Avg age by sex");
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

	private static void update() {
		System.out.println("Inc age for all male");
		database.update("humans")
			.where(r -> "male".equals(r.get("sex")))
			.set(r -> new Pair<>("age", r.getInt("age")+1));
		database.select()
			.from("humans")
			.execute()
			.forEachRemaining(System.out::println);
		System.out.println("Swap sex");
		database.update("humans")
			.set("sex", sex -> "male".equals(sex) ? "female" : "male");
		database.select()
			.from("humans")
			.execute()
			.forEachRemaining(System.out::println);
		System.out.println("Kill all those whose age is more than 30");
		database.update("humans")
			.where(r ->r.getInt("age")>30)
			.set("alive", false);
		database.select()
			.from("humans")
			.execute()
			.forEachRemaining(System.out::println);
	}

	private static void delete() {
		System.out.println("Delete all dead man");
		System.out.println(database.delete("humans").where(r -> !r.getBoolean("alive")));
		database.select()
			.from("humans")
			.execute()
			.forEachRemaining(System.out::println);
	}
	

	public static void main(String[] args) throws InterruptedException {
		database = new MemoryDatabase();
		createTable();
		insert();
		select();
		update();
		delete();
	}

}
