package com.nighthawk.spring_portfolio.mvc.person;


import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Period;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.nighthawk.spring_portfolio.mvc.userStocks.userStocksTable;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToOne;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Person {


   @Id
   @GeneratedValue(strategy = GenerationType.AUTO)
   private Long id;


   @ManyToMany(fetch = FetchType.EAGER)
   @JoinTable(
       name = "person_person_sections",
       joinColumns = @JoinColumn(name = "person_id"),
       inverseJoinColumns = @JoinColumn(name = "section_id")
   )
   private Collection<PersonRole> roles = new ArrayList<>();


   @Column(length = 255)
   private String pfp;


   private boolean kasmServerNeeded;


   @OneToOne(cascade = CascadeType.ALL, mappedBy = "person")
   @JsonIgnore
   private userStocksTable user_stocks;


   @NotEmpty
   @Size(min = 5)
   @Email
   @Column(unique = true)
   private String email;


   @NotEmpty
   private String password;


   @NonNull
   @Size(min = 2, max = 30)
   private String name;


   @DateTimeFormat(pattern = "yyyy-MM-dd")
   private Date dob;


   @Column
   private double balance;


   @JdbcTypeCode(SqlTypes.JSON)
   @Column(columnDefinition = "jsonb")
   private Map<String, Map<String, Object>> stats = new HashMap<>();


   public Person(String email, String password, String name, Date dob, double balance, PersonRole role, String pfp, boolean kasmServerNeeded) {
       this.email = email;
       this.password = password;
       this.name = name;
       this.dob = dob;
       this.balance = balance;
       this.roles.add(role);
       this.pfp = pfp;
       this.kasmServerNeeded = kasmServerNeeded;
   }


   public int getAge() {
       if (this.dob != null) {
           LocalDate birthDate = this.dob.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
           return Period.between(birthDate, LocalDate.now()).getYears();
       }
       return -1;
   }


   public boolean hasRoleWithName(String roleName) {
       for (PersonRole role : roles) {
           if (role.getName().equals(roleName)) {
               return true;
           }
       }
       return false;
   }


   public static Person createPerson(String name, String email, String password, String dob, double balance) {
       return createPerson(name, email, password, dob, balance, Arrays.asList("ROLE_USER"), null, false);
   }


   public static Person createPerson(String name, String email, String password, String dob, double balance, List<String> roleNames) {
       return createPerson(name, email, password, dob, balance, roleNames, null, false);
   }


   public static Person createPerson(String name, String email, String password, String dob, double balance, List<String> roleNames, String pfp, boolean kasmServerNeeded) {
       Person person = new Person();
       person.setName(name);
       person.setEmail(email);
       person.setPassword(password);
       person.setBalance(balance);
       person.setPfp(pfp);
       person.setKasmServerNeeded(kasmServerNeeded);


       try {
           Date date = new SimpleDateFormat("MM-dd-yyyy").parse(dob);
           person.setDob(date);
       } catch (Exception e) {
           e.printStackTrace();
       }


       for (String roleName : roleNames) {
           PersonRole role = new PersonRole();
           role.setName(roleName);
           person.getRoles().add(role);
       }


       return person;
   }


   public static Person[] init() {
       List<Person> persons = new ArrayList<>();
       persons.add(createPerson("Thomas Edison", "toby@gmail.com", "123toby", "01-01-1840", 1000.0, Arrays.asList("ROLE_ADMIN", "ROLE_USER"), "edison.png", true));
       persons.add(createPerson("Alexander Bell", "bell@gmail.com", "123bell", "01-01-1847", 1500.0, null, "bell.png", false));


       for (Person person : persons) {
           userStocksTable stock = new userStocksTable("AAPL,TSLA", "BTC,ETH", person);
           person.setUser_stocks(stock);
       }


       return persons.toArray(new Person[persons.size()]);
   }


   public static void main(String[] args) {
       Person[] persons = init();
       for (Person person : persons) {
           System.out.println(person);
       }
   }
}



