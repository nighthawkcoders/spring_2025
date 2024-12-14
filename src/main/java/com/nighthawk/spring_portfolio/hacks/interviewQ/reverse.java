package com.nighthawk.spring_portfolio.hacks.interviewQ;

/**
 * Reverse a string
 * College Board will typically ask you to reverse a string
 */
public class Reverse {
    /**  While loop using string concatenation (Classic Method)
     * CharAt() is allows you to access the character at a specific index
     * length() is allows you to find the length of the string
     *  --- How it works ---
     * Inital call: reverseString1("Hello, World!")
     * Iteration 1: reversed = "" + "!" = "!"
     * Iteration 2: reversed = "!" + "d" = "!d"
     * Iteration 3: reversed = "!d" + "l" = "!dl"
     * ...
     * Last Iteration: reversed = "!dlroW ,olle" + "H" = "!dlroW ,olleH"
     * Return reversed: "!dlroW ,olleH"
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
    
    /** Method 2: For each loop using string concatenation 
     * The foreach loop is used to iterate through the string
     * The .toCharArray() method is used to convert the string to a character array
     * The concatenation operator is used to add characters to the reversed string;
     * --- How it works ---
     * Inital call: reverseString2("Hello, World!")
     * Iteration 1: reversed = "H" + "" = "H"
     * Iteration 2: reversed = "e" + "H" = "eH"
     * Iteration 3: reversed = "l" + "eH" = "leH"
     * ...
     * Last Interation: reversed = "!" + "dlroW ,olleH" = "!dlroW ,olleH"
     * Return reversed: "!dlroW ,olleH"
     */
    public static String reverseString2(String original) {
        String reversed = "";
        for (char c : original.toCharArray()) {
            reversed = c + reversed; // Add character in front of current string, thus reversing the string
        }
        return reversed;
    }

    /** Method3: Recursion Method
     * The base case is when the string is empty, the reversed string is empty
     * The recursive case is when the string is not empty, the first character is added to the end of the reversed string
     * The .isEmpty() method is used to check if the string is empty
     * The substring() method is used to remove the first character from the string
     * --- How it works ---
     * Inital call: reverseString3("Hello, World!")
     * Recursive call: reverseString3("ello, World!") + "H"
     * Recursive call: reverseString3("llo, World!") + "e" + "H"
     * ...
     * Last Pass: reverseString5("") + "d" + "l" + "r" + "o" + "W" + "," + "o" + "l" + "l" + "e" + "H"
     * Recursion Unwinding: "" + "d" + "l" + "r" + "o" + "W" + "," + "o" + "l" + "l" + "e" + "H"
     * Return result: "dlroW,olleH"
     */
    public static String reverseString3(String original) {
        if (original.isEmpty()) {
            return original;
        } else {
            // Trims 0 index on each recursion and creates a stack of concatenations at unwinding
            return reverseString3(original.substring(1)) + original.charAt(0);
        }
    }

/** Method 4: For loop using StringBuilder
     * StringBuilder is a mutable sequence of characters;
     * the .append() method is used to add characters to the mutable sequence
     * the .toString() method is used to convert the sequence back to a string
     * Conventional for loop is used to iterate through the string;
     * .length() allows you to find the length of the string
     * --- How it works ---
     * Initial call: reverseString4("Hello, World!")
     * reversed = ""
     * reversed = "" + "!" = "!"
     * reversed = "!" + "d" = "!d"
     * reversed = "!d" + "l" = "!dl"
     * ...
     * Last Iteration: reversed = "!dlroW,olle" + "H" = "!dlroW,olleH"
     * reversed.toString() = "!dlroW,olleH"
     * Return reversed: "!dlroW,olleH"
     */
    public static String reverseString4(String original) {
        StringBuilder reversed = new StringBuilder();
        for (int i = original.length() - 1; i >= 0; i--) {
            reversed.append(original.charAt(i));
        }
        return reversed.toString();
    }

/** Method 5: Stream Method using StringBuilder
     * The .chars() method is used to convert the string to an IntStream
     * The .forEach() method is used to iterate through the IntStream
     * The .insert() method is used to insert characters at the beginning of the string
     * The .toString() method is used to convert the StringBuilder to a string
     * --- How it works ---
     * Initial call: reverseString5("Hello, World!")
     * reversed = ""
     * reversed = "H" -> "H"
     * reversed = "e" -> "eH"
     * reversed = "l" -> "leH"
     * ...
     * Last Iteration: reversed = "!" -> "!dlroW,olleH"
     * reversed.toString() = "!dlroW,olleH"
     * Return reversed: "!dlroW,olleH"
     */
    public static String reverseString5(String original) {
        StringBuilder reversed = new StringBuilder();
        original.chars().forEach(c -> reversed.insert(0, (char) c));
        return reversed.toString();
    }
    
    // Main method to test the reverseString methods
    public static void main(String[] args) {
        String original = "Hello, World!";
        System.out.println("Original: " + original);

        System.out.println("Reversed using While: " + reverseString1(original));
        System.out.println("Reversed using For each " + reverseString2(original));
        System.out.println("Reversed using Recursion: " + reverseString3(original));
        System.out.println("Reversed using For with StringBuilder: " + reverseString4(original));
        System.out.println("Reversed using Functional style: " + reverseString5(original));
    }

}
