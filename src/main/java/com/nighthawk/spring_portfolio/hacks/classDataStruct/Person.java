package com.nighthawk.spring_portfolio.hacks.classDataStruct;


/*
Adapted from Person POJO, Plain Old Java Object.
 */
public class Person extends Generics{
    // Class data
    private static String classType = "Person";
    public static KeyTypes key = KeyType.title;  // static initializer
	public static void setOrder(KeyTypes key) {Person.key = key;}
	public enum KeyType implements KeyTypes {title, uid, name, age}

    // Instance data
    private String uid;  // user / person id
    private String password;
    private String name;
    

    // Constructor with zero arguments
    public Person() {
        super.setType(classType);
    }

    // Constructor used when building object from an API
    public Person(String uid, String password, String name) {
        this();  // runs zero argument constructor
        this.uid = uid;
        this.password = password;
        this.name = name;
    }

    /* 'Generics' requires getKey to help enforce KeyTypes usage */
	@Override
	protected KeyTypes getKey() { return Person.key; }

    public String getUserID() {
        return uid;
    }

    /* 'Generics' requires toString override
	 * toString provides data based off of Static Key setting
	 */
	@Override
	public String toString() {		
		String output="";
		if (KeyType.uid.equals(this.getKey())) {
			output += this.uid;
		} else if (KeyType.name.equals(this.getKey())) {
			output += this.name;
		} else {
			output = super.getType() + ": " + this.uid + ", " + this.name;
		}
		return output;
	}

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    // Initialize static test data 
    public static Person[] init() {

        // basics of class construction
        Person p1 = new Person();
        p1.setName("Thomas Edison");
        p1.setUid("toby@gmail.com");
        p1.setPassword("123Toby!");
        // Array definition and data initialization
        Person persons[] = {p1};
        return(persons);
    }

    public static void main(String[] args) {
        // obtain Person from initializer
        Person persons[] = init();
        Person.setOrder(Person.KeyType.title);

        // iterate using "enhanced for loop"
        for( Person person : persons ) {
            System.out.println(person);  // print object
        }
    }

}