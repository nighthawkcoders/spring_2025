package com.nighthawk.spring_portfolio.hacks.interviewQ;

/**
 * Reverse a string
 * College Board will typically ask you to reverse a string
 */

public class reverse {
    /**  Method 1: While loop using string concatenation
     * CharAt() is allows you to access the character at a specific index
     * length() is allows you to find the length of the string
     */
    public static String reverseString1(String original) {
        String reversed = "";
        int i = original.length();
        while (i > 0) {
            reversed = reversed + original.charAt(i-1);
            i--;
        }
        return reversed;
    }
    
    /** Method 2: For loop using StringBuilder
     * StringBuilder is a mutable sequence of characters;
     * the .append() method is used to add characters to the sequence
     * the .toString() method is used to convert the sequence to a string
     * Conventional for loop is used to iterate through the string;
     * .length() is allows you to find the length of the string 
     */
    public static String reverseString2(String original) {
        StringBuilder reversed = new StringBuilder();
        for (int i = original.length(); i > 0; i--) {
            reversed.append(original.charAt(i-1));
        }
        return reversed.toString();
    }

    /** Method 3: For each loop using string concatenation 
     * The foreach loop is used to iterate through the string
     * The .toCharArray() method is used to convert the string to a character array
     * The concatenation operator is used to add characters to the reversed string;
     */
    public static String reverseString3(String original) {
        String reversed = "";
        for (char c : original.toCharArray()) {
            reversed = c + reversed; // Add character in front of current string, thus reversing the string
        }
        return reversed;
    }
    
    /** Method 4: Stream Method using StringBuilder
     * The .chars() method is used to convert the string to an IntStream
     * The .forEach() method is used to iterate through the IntStream
     * The .insert() method is used to insert characters at the beginning of the string
     * The .toString() method is used to convert the StringBuilder to a string
     */
    public static String reverseString4(String original) {
        StringBuilder reversed = new StringBuilder();
        original.chars().forEach(c -> reversed.insert(0, (char) c));
        return reversed.toString();
    }

    // Main method to test the reverseString methods
    public static void main(String[] args) {
        String original = "Hello, World!";
        System.out.println("Original: " + original);

        System.out.println("Reversed method 1: " + reverseString1(original));
        System.out.println("Reversed method 2: " + reverseString2(original));
        System.out.println("Reversed method 3: " + reverseString3(original));
        System.out.println("Reversed method 4: " + reverseString4(original));
    }

}
