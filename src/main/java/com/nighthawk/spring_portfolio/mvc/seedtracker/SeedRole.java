package com.nighthawk.spring_portfolio.mvc.seedtracker;

import com.nighthawk.spring_portfolio.mvc.person.PersonRole;

import java.util.HashSet;
import java.util.Set;

public class SeedRole {
    private Set<PersonRole> roles = new HashSet<>();

    public SeedRole() {
        // Adding default roles
        roles.add(new PersonRole("admin"));
        roles.add(new PersonRole("student"));
    }

    public void addRole(String roleName) {
        roles.add(new PersonRole(roleName));
    }

    public boolean updateRole(String oldRoleName, String newRoleName) {
        for (PersonRole role : roles) {
            if (role.getName().equalsIgnoreCase(oldRoleName)) {
                roles.remove(role);
                roles.add(new PersonRole(newRoleName));
                return true;
            }
        }
        return false;
    }

    public Set<PersonRole> getRoles() {
        return roles;
    }

    public static void main(String[] args) {
        SeedRole seedRole = new SeedRole();

        // Example usage
        System.out.println("Default roles: " + seedRole.getRoles());

        // Adding a new role
        seedRole.addRole("teacher");
        System.out.println("Roles after adding 'teacher': " + seedRole.getRoles());

        // Updating a role
        seedRole.updateRole("student", "mentor");
        System.out.println("Roles after updating 'student' to 'mentor': " + seedRole.getRoles());
    }
}