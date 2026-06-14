/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchat;
import java.util.Scanner;
/**
 *
 * @author RENDZO
 */
public class Registration_and_login {
    private String firstName;
    private String lastName;
    private String userName;
    private String password;
    private String cellPhoneNumber;
    
    // Constructor
    public Registration_and_login(String firstName, String lastName, String userName, String password, String cellPhoneNumber) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.userName = userName;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
    }
    
    // Username must contain "_" and be no more than 5 characters
    public boolean checkUserName() {
        return userName.contains("_") && userName.length() <= 5;
    }
    
    // Password: 8+ characters, capital, letter, number, special character
    public boolean checkPasswordComplexity() {
        String regex = "^(?=.*[A-Z])(?=.*\\d)(?=.*[^a-zA-Z0-9]).{8,}$";
        return password.matches(regex);
    }

    // Cell phone must contain international code and number part no more than 10 digits
    public boolean checkCellPhoneNumber() {
        String regex = "^\\+\\d{1,3}\\d{1,10}$";
        return cellPhoneNumber.matches(regex);
    }

    // Registration message
    public String registerUser() {
        if (!checkUserName()) {
            return "Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.";
        }
        if (!checkPasswordComplexity()) {
            return "Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.";
        }
        if (!checkCellPhoneNumber()) {
            return "Cell phone number incorrectly formatted or does not contain international code; please correct the number and try again.";
        }
        return "User has been registered successfully.";
    }
    
    // Login check
    public boolean loginUser(String enteredUserName, String enteredPassword) {
        return this.userName.equals(enteredUserName) && this.password.equals(enteredPassword);
    }

    // Login status message
    public String returnLoginStatus(boolean loginSuccess) {
        if (loginSuccess) {
            return "Welcome " + firstName + " " + lastName + ", it is great to see you again.";
        } else {
            return "Username or password incorrect, please try again.";
        }
    }

    // GETTERS (needed for QuickChat to welcome the user)
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getUserName() { return userName; }

    // ============================================
    // MAIN METHOD — Registration + Login + QuickChat
    // ============================================
    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);

        // ===== REGISTRATION =====
        System.out.println("=== REGISTRATION ===");
        System.out.print("Enter first name: ");
        String firstName = input.nextLine();
        System.out.print("Enter last name: ");
        String lastName = input.nextLine();
        System.out.print("Enter username (must contain _ and max 5 chars): ");
        String userName = input.nextLine();
        System.out.print("Enter password (8+ chars, capital, number, special): ");
        String password = input.nextLine();
        System.out.print("Enter cell phone number (+ and max 10 digits): ");
        String cellPhoneNumber = input.nextLine();
        
        Registration_and_login user = new Registration_and_login(firstName, lastName, userName, password, cellPhoneNumber);

        // Check username
        if (user.checkUserName()) {
            System.out.println("Username successfully captured.");
        } else {
            System.out.println("Username is not correctly formatted; please ensure that your username contains an underscore and is no more than five characters in length.");
        }

        // Check password
        if (user.checkPasswordComplexity()) {
            System.out.println("Password successfully captured.");
        } else {
            System.out.println("Password is not correctly formatted; please ensure that the password contains at least eight characters, a capital letter, a number, and a special character.");
        }

        // Check cell phone
        if (user.checkCellPhoneNumber()) {
            System.out.println("Cell number successfully captured.");
        } else {
            System.out.println("Cell phone number incorrectly formatted or does not contain international code; please correct the number and try again.");
        }

        // Final registration result
        String regResult = user.registerUser();
        System.out.println(regResult);

        // Only continue to login if registration succeeded
        if (!regResult.equals("User has been registered successfully.")) {
            System.out.println("Registration failed. Cannot proceed.");
            input.close();
            return;
        }

        // ===== LOGIN =====
        System.out.println("\n=== LOGIN ===");
        boolean loggedIn = false;
        int attempts = 0;
        
        while (!loggedIn && attempts < 3) {
            System.out.print("Enter username: ");
            String enteredUserName = input.nextLine();
            System.out.print("Enter password: ");
            String enteredPassword = input.nextLine();

            loggedIn = user.loginUser(enteredUserName, enteredPassword);
            System.out.println(user.returnLoginStatus(loggedIn));
            attempts++;
        }

        // ===== QUICKCHAT — PART 2 =====
        if (loggedIn) {
            System.out.println("\n========================================");
            QuickChat.startMessaging(input, user);  // Launch Part 2!
        } else {
            System.out.println("Too many failed attempts. Account locked.");
        }

        input.close();
    }
}

