package examples;

import static fields.Field.field;

import java.util.Objects;

import database.Database;
import database.MemoryDatabase;
import records.Record;
import select.Query;
import tables.Table;

public class MultiTableOperations {
	private static Database database;

	public static void main(String[] args) {
		database = new MemoryDatabase();
		createTables();
		insert();
		select();
	}

	private static void select() {
		
		System.out.println("group employees by office");
		Query employeesByOffice = database.select(
				"name", r -> r.get("offices.name"),
				"postCode", r -> r.max("offices.city"),
				"count", Record::count)
			.from("offices","employees")
			.where(r -> Objects.equals(r.get("offices.name"), r.get("employees.office")))
			.groupBy("offices.name");
		
		employeesByOffice.execute().forEachRemaining(System.out::println);
		database.createView("employeesByOffice", employeesByOffice);
		
		System.out.println("group employees by city with using WHERE");	
		database.select(
				"name", r-> r.get("cities.name"),
				"count", r->r.count())
			.from("cities","offices","employees")
			.where(r -> Objects.equals(r.get("cities.postCode"), r.get("offices.city")) &&
					Objects.equals(r.get("offices.name"), r.get("employees.office")))
			.groupBy("cities.name")
			.execute().forEachRemaining(System.out::println);
		
		System.out.println("group employees by city with using JOIN (is equals prevision)");	
		database.select(
				"name", r-> r.get("cities.name"),
				"count", r->r.count())
			.from("cities")
			.join("offices","postCode","city")
			.join("employees","offices.name","office")
			.groupBy("cities.name")
			.execute().forEachRemaining(System.out::println);
		
		System.out.println("group employees by city with VIEW (is equals prevision)");
		database.select(
				"name", r-> r.get("cities.name"),
				"count", r->r.sum("employeesByOffice.count"))
			//.from("cities","employeesByOffice")
			//.where(r -> Objects.equals(r.get("cities.postCode"), r.get("employeesByOffice.postCode")))
			.from("employeesByOffice")
			.join("cities", "employeesByOffice.postCode", "cities.postCode")
			.groupBy("cities.name")
			.execute().forEachRemaining(System.out::println);
	}

	private static void insert() {
		database.insertInto("cities").values("1", "Moscow");
		database.insertInto("cities").values("2", "Saint-Peterburg");
		
		database.insertInto("offices").values("Main", "1");
		database.insertInto("offices").values("Second", "1");
		database.insertInto("offices").values("SPb", "2");
		
		database.insertInto("employees").values("nat01", "Natalia", "Main");
		database.insertInto("employees").values("mar01", "Maria", "Main");
		database.insertInto("employees").values("mar02", "Maria", "Second");
		database.insertInto("employees").values("oleg01", "Oleg", "SPb");
		database.insertInto("employees").values("ivan01", "Ivan", "SPb");
		try{
			database.insertInto("employees").values("ivan02", "Ivan", "Ekb");
		} catch (Exception e) {
			System.out.println("Error: "+ e.getMessage());
		}
	}

	private static void createTables() {
		Table city = database.createTable("cities", 
				field("postCode", String.class).primaryKey(),
				field("name", String.class),
				field("comment", String.class));
		database.createTable("offices", 
				field("name", String.class).primaryKey(),
				//bad variant no control for operations on city.postCode
				field("city", String.class).index().constraint((o) -> city.contains("postCode", o)));
		database.createTable("employees", 
				field("login", String.class).primaryKey(),
				field("name", String.class).notNull(),
				//good variant, provide auto control for operations on city.postCode
				field("office", String.class).index().foreignKey("offices", "name"));
		; 
	}
}
