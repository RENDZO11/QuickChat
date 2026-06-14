/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchat;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Scanner;
import java.util.ArrayList;
/**
 *
 * @author RENDZO
 */
public class QuickChat {
    // ── Part 3 arrays ──────────────────────────────────────────
    static ArrayList<String>  sentMessagesList      = new ArrayList<>();
    static ArrayList<String>  disregardedMessages   = new ArrayList<>();
    static ArrayList<Message> storedMessages        = new ArrayList<>();
    static ArrayList<String>  messageHashList       = new ArrayList<>();
    static ArrayList<String>  messageIDList         = new ArrayList<>();
    // ───────────────────────────────────────────────────────────
 
    public static void startMessaging(Scanner scanner, Registration_and_login user) {
 
        System.out.println("Welcome to QuickChat.");
        System.out.println("Welcome " + user.getFirstName() + " " + user.getLastName()
                + ", it is great to see you again.");
 
        System.out.print("\nHow many messages do you wish to send? ");
        int totalMessages = Integer.parseInt(scanner.nextLine().trim());
 
        if (totalMessages <= 0) {
            System.out.println("No messages to send.");
            return;
        }
 
        int messagesDone = 0;
        boolean running  = true;
 
        while (running) {
            System.out.println("\n--- QuickChat Menu ---");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Stored Messages");
            System.out.println("4) Quit");
            System.out.print("Choose an option: ");
 
            int choice = Integer.parseInt(scanner.nextLine().trim());
 
            switch (choice) {
 
                // ── Option 1 : Compose & send a message ──────────────
                case 1:
                    if (messagesDone >= totalMessages) {
                        System.out.println("You have already sent all " + totalMessages + " message(s).");
                        break;
                    }
 
                    System.out.println("\n--- Message " + (messagesDone + 1) + " of " + totalMessages + " ---");
 
                    Message msg = new Message(messagesDone);
                    System.out.println("Message ID generated: " + msg.getMessageID());
 
                    // Validate recipient
                    String recipientResult = "";
                    while (!recipientResult.equals("Cell phone number successfully captured.")) {
                        System.out.print("Enter recipient cell number (e.g. +27834557896): ");
                        recipientResult = msg.checkRecipientCell(scanner.nextLine().trim());
                        System.out.println(recipientResult);
                    }
 
                    // Validate message text
                    String messageResult = "";
                    while (!messageResult.equals("Message ready to send.")) {
                        System.out.print("Enter your message (max 250 chars): ");
                        messageResult = msg.validateMessage(scanner.nextLine());
                        System.out.println(messageResult);
                    }
 
                    // Hash & preview
                    String hash = msg.createMessageHash();
                    System.out.println("Message Hash: " + hash);
                    System.out.println("\n--- Message Preview ---");
                    System.out.println(msg.printMessages());
 
                    // Action menu
                    System.out.println("\n1) Send Message");
                    System.out.println("2) Disregard Message");
                    System.out.println("3) Store Message to send later");
                    System.out.print("Choose an option: ");
                    int action = Integer.parseInt(scanner.nextLine().trim());
 
                    String result = msg.sentMessage(action);
                    System.out.println(result);
 
                    // Populate arrays based on action
                    if (action == 1) {
                        sentMessagesList.add(msg.getMessage());
                        messageHashList.add(msg.getMessageHash());
                        messageIDList.add(msg.getMessageID());
                        System.out.println("\n--- Full Message Details ---");
                        System.out.println(msg.printMessages());
 
                    } else if (action == 2) {
                        disregardedMessages.add(msg.getMessage());
                        System.out.print("Press 0 to delete the message: ");
                        if (Integer.parseInt(scanner.nextLine().trim()) == 0) {
                            System.out.println("Message deleted.");
                        }
 
                    } else if (action == 3) {
                        storedMessages.add(msg);
                        messageHashList.add(msg.getMessageHash());
                        messageIDList.add(msg.getMessageID());
                        // Also write to JSON file
                        appendMessageToFile(msg);
                        System.out.println("\n--- Full Message Details ---");
                        System.out.println(msg.printMessages());
                    }
 
                    messagesDone++;
                    break;
 
                // ── Option 2 : Recently sent messages ────────────────
                case 2:
                    if (sentMessagesList.isEmpty()) {
                        System.out.println("No messages sent yet.");
                    } else {
                        System.out.println("\n--- Recently Sent Messages ---");
                        for (int i = 0; i < sentMessagesList.size(); i++) {
                            System.out.println((i + 1) + ". " + sentMessagesList.get(i));
                        }
                    }
                    break;
 
                // ── Option 3 : Stored Messages sub-menu ──────────────
                case 3:
                    storedMessagesMenu(scanner);
                    break;
 
                // ── Option 4 : Quit ───────────────────────────────────
                case 4:
                    running = false;
                    System.out.println("Thank you " + user.getFirstName() + " " + user.getLastName() + ",for using QuickChat. Goodbye!");
                    break;
 
                default:
                    System.out.println("Invalid option. Please try again.");
            }
        }
 
        System.out.println("\n========================================");
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
        System.out.println("========================================");
    }
 //
    // ════════════════════════════════════════════════════════════
    // STORED MESSAGES SUB-MENU  (Part 3 – Requirement 2)
    // ════════════════════════════════════════════════════════════
    private static void storedMessagesMenu(Scanner scanner) {
 
        // Load JSON file into storedMessages list (if not already loaded)
        loadStoredMessagesFromFile();
 
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages Menu ---");
            System.out.println("a) Display sender and recipient of all stored messages");
            System.out.println("b) Display the longest stored message");
            System.out.println("c) Search for a message by ID");
            System.out.println("d) Search messages for a particular recipient");
            System.out.println("e) Delete a message using message hash");
            System.out.println("f) Display full report of all stored messages");
            System.out.println("0) Back to main menu");
            System.out.print("Choose an option: ");
 
            String opt = scanner.nextLine().trim().toLowerCase();
 
            switch (opt) {
 
                // a) Sender + recipient of all stored messages
                case "a":
                    if (storedMessages.isEmpty()) {
                        System.out.println("No stored messages.");
                    } else {
                        System.out.println("\n--- Sender / Recipient of Stored Messages ---");
                        for (Message m : storedMessages) {
                            System.out.println("Recipient: " + m.getRecipient()
                                    + "  |  Message: " + m.getMessage());
                        }
                    }
                    break;
 
                // b) Longest stored message
                case "b":
                    if (storedMessages.isEmpty()) {
                        System.out.println("No stored messages.");
                    } else {
                        Message longest = storedMessages.get(0);
                        for (Message m : storedMessages) {
                            if (m.getMessage().length() > longest.getMessage().length()) {
                                longest = m;
                            }
                        }
                        System.out.println("\nLongest stored message:");
                        System.out.println("\"" + longest.getMessage() + "\"");
                    }
                    break;
 
                // c) Search by message ID
                case "c":
                    System.out.print("Enter Message ID to search: ");
                    String searchID = scanner.nextLine().trim();
                    boolean foundID = false;
                    for (Message m : storedMessages) {
                        if (m.getMessageID().equals(searchID)) {
                            System.out.println("Recipient: " + m.getRecipient());
                            System.out.println("Message  : " + m.getMessage());
                            foundID = true;
                            break;
                        }
                    }
                    if (!foundID) System.out.println("Message ID not found.");
                    break;
 
                // d) Search by recipient
                case "d":
                    System.out.print("Enter recipient number to search: ");
                    String searchRecipient = scanner.nextLine().trim();
                    boolean foundRecipient = false;
                    for (Message m : storedMessages) {
                        if (m.getRecipient().equals(searchRecipient)) {
                            System.out.println("Message: " + m.getMessage());
                            foundRecipient = true;
                        }
                    }
                    if (!foundRecipient) System.out.println("No messages found for that recipient.");
                    break;
 
                // e) Delete by message hash
                case "e":
                    System.out.print("Enter Message Hash to delete: ");
                    String deleteHash = scanner.nextLine().trim();
                    boolean deleted = false;
                    for (int i = 0; i < storedMessages.size(); i++) {
                        if (storedMessages.get(i).getMessageHash().equals(deleteHash)) {
                            String deletedMsg = storedMessages.get(i).getMessage();
                            storedMessages.remove(i);
                            // Also remove from hash and ID lists
                            messageHashList.remove(deleteHash);
                            System.out.println("Message: \"" + deletedMsg + "\" successfully deleted.");
                            saveStoredMessagesToFile(); // update JSON file
                            deleted = true;
                            break;
                        }
                    }
                    if (!deleted) System.out.println("Message Hash not found.");
                    break;
 
                // f) Full report
                case "f":
                    if (storedMessages.isEmpty()) {
                        System.out.println("No stored messages.");
                    } else {
                        System.out.println("\n===== STORED MESSAGES REPORT =====");
                        for (int i = 0; i < storedMessages.size(); i++) {
                            Message m = storedMessages.get(i);
                            System.out.println("--- Message " + (i + 1) + " ---");
                            System.out.println("Message Hash : " + m.getMessageHash());
                            System.out.println("Recipient    : " + m.getRecipient());
                            System.out.println("Message      : " + m.getMessage());
                            System.out.println();
                        }
                        System.out.println("===================================");
                    }
                    break;
 
                case "0":
                    back = true;
                    break;
 
                default:
                    System.out.println("Invalid option.");
            }
        }
    }
    
    // ════════════════════════════════════════════════════════════
    // JSON FILE HELPERS
    // ════════════════════════════════════════════════════════════
 
    private static final String FILE_PATH = "stored_messages.json";
 
    /** Append one message to the JSON file (array format). */
    private static void appendMessageToFile(Message msg) {
        try {
            File file = new File(FILE_PATH);
            ArrayList<String> entries = new ArrayList<>();
 
            // Read existing entries
            if (file.exists()) {
                String content = new String(Files.readAllBytes(file.toPath())).trim();
                if (content.startsWith("[") && content.endsWith("]")) {
                    content = content.substring(1, content.length() - 1).trim();
                    if (!content.isEmpty()) {
                        // Split by },{
                        String[] parts = content.split("(?<=\\}),\\s*(?=\\{)");
                        for (String p : parts) entries.add(p.trim());
                    }
                }
            }
 
            entries.add(msg.storeMessage());
 
            // Write back as JSON array
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < entries.size(); i++) {
                sb.append("  ").append(entries.get(i));
                if (i < entries.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("]");
 
            Files.write(file.toPath(), sb.toString().getBytes());
 
        } catch (IOException e) {
            System.out.println("Warning: Could not write to file. " + e.getMessage());
        }
    }
 
    /** Save the current storedMessages list to file (used after deletion). */
    private static void saveStoredMessagesToFile() {
        try {
            StringBuilder sb = new StringBuilder("[\n");
            for (int i = 0; i < storedMessages.size(); i++) {
                sb.append("  ").append(storedMessages.get(i).storeMessage());
                if (i < storedMessages.size() - 1) sb.append(",");
                sb.append("\n");
            }
            sb.append("]");
            Files.write(Paths.get(FILE_PATH), sb.toString().getBytes());
        } catch (IOException e) {
            System.out.println("Warning: Could not update file. " + e.getMessage());
        }
    }
 
    /** Load stored messages from JSON file into storedMessages list. */
    private static void loadStoredMessagesFromFile() {
        try {
            File file = new File(FILE_PATH);
            if (!file.exists()) return;
 
            String content = new String(Files.readAllBytes(file.toPath())).trim();
            if (!content.startsWith("[") || content.length() < 3) return;
 
            content = content.substring(1, content.length() - 1).trim();
            if (content.isEmpty()) return;
 
            String[] parts = content.split("(?<=\\}),\\s*(?=\\{)");
 
            for (String part : parts) {
                part = part.trim();
 
                String messageID      = extractJSON(part, "messageID");
                String numStr         = extractJSON(part, "numMessagesSent");
                String recipient      = extractJSON(part, "recipient");
                String messageText    = extractJSON(part, "message");
                String messageHash    = extractJSON(part, "messageHash");
 
                int num = numStr.isEmpty() ? 0 : Integer.parseInt(numStr);
 
                // Only add if not already in the list
                boolean alreadyLoaded = false;
                for (Message m : storedMessages) {
                    if (m.getMessageID().equals(messageID)) {
                        alreadyLoaded = true;
                        break;
                    }
                }
                if (alreadyLoaded) continue;
 
                Message m = new Message(num);
                m.setMessageID(messageID);
                m.setRecipient(recipient);
                m.setMessage(messageText);
                m.createMessageHash(); // recalculate or set from file
                // Override hash with stored value if available
                if (!messageHash.isEmpty()) {
                    // use reflection-free setter approach: re-create hash
                    // (hash is derived, so recalculate is fine; stored value is for reference)
                }
 
                storedMessages.add(m);
                if (!messageHashList.contains(m.getMessageHash()))
                    messageHashList.add(m.getMessageHash());
                if (!messageIDList.contains(messageID))
                    messageIDList.add(messageID);
            }
 
        } catch (IOException e) {
            System.out.println("Warning: Could not read stored messages file. " + e.getMessage());
        }
    }
 
    /** Minimal JSON value extractor for string and number fields. */
    private static String extractJSON(String json, String key) {
        String search = "\"" + key + "\"";
        int idx = json.indexOf(search);
        if (idx == -1) return "";
        idx += search.length();
        // skip : and whitespace
        while (idx < json.length() && (json.charAt(idx) == ':' || json.charAt(idx) == ' ')) idx++;
        if (idx >= json.length()) return "";
 
        if (json.charAt(idx) == '"') {
            // string value
            int start = idx + 1;
            int end   = json.indexOf('"', start);
            return end == -1 ? "" : json.substring(start, end);
        } else {
            // number / boolean
            int start = idx;
            int end   = start;
            while (end < json.length() && json.charAt(end) != ',' && json.charAt(end) != '}') end++;
            return json.substring(start, end).trim();
        }
    }
}

