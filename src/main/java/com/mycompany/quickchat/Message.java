/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.quickchat;
import java.util.Random;
import java.io.FileWriter;
import java.io.FileReader;
import java.io.IOException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
/**
 *
 * @author RENDZO
 */
public class Message {
     private String messageID;
    private int numMessagesSent;
    private String recipient;
    private String message;
    private String messageHash;
    private boolean isSent;
    private boolean isStored;
    private boolean isDisregarded;
    
    // Static arrays for Part 3 - store all messages by category
    private static Message[] sentMessagesArray = new Message[100];
    private static Message[] disregardedMessagesArray = new Message[100];
    private static Message[] storedMessagesArray = new Message[100];
    private static String[] messageHashesArray = new String[100];
    private static String[] messageIDsArray = new String[100];
    
    private static int sentCount = 0;
    private static int disregardedCount = 0;
    private static int storedCount = 0;
    private static int hashCount = 0;
    private static int idCount = 0;
    
    private static int totalMessagesSent = 0;
    private static Random random = new Random();
    
    // JSON file name for storing messages
    private static final String JSON_FILE = "stored_messages.json";
    
    public Message(int messageNumber) {
        this.numMessagesSent = messageNumber;
        this.messageID = generateMessageID();
        this.isSent = false;
        this.isStored = false;
        this.isDisregarded = false;
    }
    
    private String generateMessageID() {
        StringBuilder id = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            id.append(random.nextInt(10));
        }
        return id.toString();
    }
    
    public boolean checkMessageID() {
        return this.messageID != null && this.messageID.length() <= 10;
    }
    
    public String checkRecipientCell(String cellNumber) {
        if (cellNumber == null || cellNumber.isEmpty()) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        if (!cellNumber.startsWith("+")) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        if (cellNumber.length() > 15) {
            return "Cell phone number is incorrectly formatted or does not contain an international code. Please correct the number and try again.";
        }
        this.recipient = cellNumber;
        return "Cell phone number successfully captured.";
    }
    
    public String validateMessage(String msg) {
        if (msg == null) {
            return "Message exceeds 250 characters by 0; please reduce the size.";
        }
        if (msg.length() > 250) {
            int excess = msg.length() - 250;
            return "Message exceeds 250 characters by " + excess + "; please reduce the size.";
        }
        this.message = msg;
        return "Message ready to send.";
    }
    
    public String createMessageHash() {
        if (messageID == null || message == null || message.trim().isEmpty()) {
            return "";
        }
        String firstTwo = messageID.substring(0, 2);
        String[] words = message.trim().split("\\s+");
        
        if (words.length == 0) {
            return "";
        }
        
        String firstWord = words[0].toUpperCase().replaceAll("[^A-Z]", "");
        String lastWord = words[words.length - 1].toUpperCase().replaceAll("[^A-Z]", "");
        
        this.messageHash = firstTwo + ":" + numMessagesSent + ":" + firstWord + lastWord;
        
        // Add to hash array
        if (hashCount < messageHashesArray.length) {
            messageHashesArray[hashCount] = this.messageHash;
            hashCount++;
        }
        
        return this.messageHash;
    }
    
    public String sentMessage(int choice) {
        switch (choice) {
            case 1: // Send
                this.isSent = true;
                totalMessagesSent++;
                if (sentCount < sentMessagesArray.length) {
                    sentMessagesArray[sentCount] = this;
                    sentCount++;
                }
                if (idCount < messageIDsArray.length) {
                    messageIDsArray[idCount] = this.messageID;
                    idCount++;
                }
                return "Message successfully sent.";
            case 2: // Disregard
                this.isDisregarded = true;
                if (disregardedCount < disregardedMessagesArray.length) {
                    disregardedMessagesArray[disregardedCount] = this;
                    disregardedCount++;
                }
                if (idCount < messageIDsArray.length) {
                    messageIDsArray[idCount] = this.messageID;
                    idCount++;
                }
                return "Press 0 to delete the message.";
            case 3: // Store
                this.isStored = true;
                if (storedCount < storedMessagesArray.length) {
                    storedMessagesArray[storedCount] = this;
                    storedCount++;
                }
                if (idCount < messageIDsArray.length) {
                    messageIDsArray[idCount] = this.messageID;
                    idCount++;
                }
                storeMessageToJSON();
                return "Message successfully stored.";
            default:
                return "Invalid choice.";
        }
    }
    
    public String printMessages() {
        if (messageHash == null) {
            createMessageHash();
        }
        return "Message ID: " + messageID + "\n" +
               "Message Hash: " + messageHash + "\n" +
               "Recipient: " + recipient + "\n" +
               "Message: " + message;
    }
    
    public static int returnTotalMessages() {
        return totalMessagesSent;
    }
    
    // ============================================
    // PART 3: JSON FILE STORAGE
    // ============================================
    
    public String storeMessage() {
        JSONObject json = new JSONObject();
        json.put("messageID", messageID);
        json.put("numMessagesSent", numMessagesSent);
        json.put("recipient", recipient);
        json.put("message", message);
        json.put("messageHash", messageHash);
        json.put("isSent", isSent);
        json.put("isStored", isStored);
        json.put("isDisregarded", isDisregarded);
        return json.toJSONString();
    }
    
    private void storeMessageToJSON() {
        try {
            JSONArray messagesArray = readMessagesFromJSON();
            JSONObject messageObj = new JSONObject();
            messageObj.put("messageID", messageID);
            messageObj.put("recipient", recipient);
            messageObj.put("message", message);
            messageObj.put("messageHash", messageHash);
            messageObj.put("flag", "Stored");
            messagesArray.add(messageObj);
            
            FileWriter file = new FileWriter(JSON_FILE);
            file.write(messagesArray.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            System.out.println("Error storing message: " + e.getMessage());
        }
    }
    
    public static JSONArray readMessagesFromJSON() {
        JSONParser parser = new JSONParser();
        try {
            FileReader reader = new FileReader(JSON_FILE);
            Object obj = parser.parse(reader);
            reader.close();
            return (JSONArray) obj;
        } catch (IOException | ParseException e) {
            return new JSONArray();
        }
    }
    
    // ============================================
    // PART 3: ARRAY METHODS
    // ============================================
    
    // a. Display sender and recipient of all stored messages
    public static String displayStoredMessages() {
        StringBuilder result = new StringBuilder();
        JSONArray stored = readMessagesFromJSON();
        
        if (stored.isEmpty()) {
            return "No stored messages found.";
        }
        
        for (Object obj : stored) {
            JSONObject msg = (JSONObject) obj;
            result.append("Sender: ").append(msg.get("messageID"))
                  .append(", Recipient: ").append(msg.get("recipient")).append("\n");
        }
        return result.toString();
    }
    
    // b. Display the longest stored message
    public static String displayLongestStoredMessage() {
        JSONArray stored = readMessagesFromJSON();
        if (stored.isEmpty()) {
            return "No stored messages found.";
        }
        
        String longestMessage = "";
        JSONObject longestMsg = null;
        
        for (Object obj : stored) {
            JSONObject msg = (JSONObject) obj;
            String msgText = (String) msg.get("message");
            if (msgText.length() > longestMessage.length()) {
                longestMessage = msgText;
                longestMsg = msg;
            }
        }
        
        if (longestMsg != null) {
            return "Longest Message: " + longestMessage + 
                   "\nLength: " + longestMessage.length() + " characters";
        }
        return "No stored messages found.";
    }
    
    // c. Search for message ID and display recipient and message
    public static String searchByMessageID(String searchID) {
        JSONArray stored = readMessagesFromJSON();
        
        for (Object obj : stored) {
            JSONObject msg = (JSONObject) obj;
            if (searchID.equals(msg.get("messageID"))) {
                return "Recipient: " + msg.get("recipient") + 
                       "\nMessage: " + msg.get("message");
            }
        }
        return "Message ID not found.";
    }
    
    // d. Search all messages for a particular recipient
    public static String searchByRecipient(String searchRecipient) {
        StringBuilder result = new StringBuilder();
        JSONArray stored = readMessagesFromJSON();
        boolean found = false;
        
        for (Object obj : stored) {
            JSONObject msg = (JSONObject) obj;
            if (searchRecipient.equals(msg.get("recipient"))) {
                result.append("Message: ").append(msg.get("message")).append("\n");
                found = true;
            }
        }
        
        if (!found) {
            return "No messages found for recipient: " + searchRecipient;
        }
        return result.toString();
    }
    
    // e. Delete a message using message hash
    public static String deleteByMessageHash(String searchHash) {
        JSONArray stored = readMessagesFromJSON();
        boolean found = false;
        String deletedMessage = "";
        
        for (int i = 0; i < stored.size(); i++) {
            JSONObject msg = (JSONObject) stored.get(i);
            if (searchHash.equals(msg.get("messageHash"))) {
                deletedMessage = (String) msg.get("message");
                stored.remove(i);
                found = true;
                break;
            }
        }
        
        if (found) {
            try {
                FileWriter file = new FileWriter(JSON_FILE);
                file.write(stored.toJSONString());
                file.flush();
                file.close();
                return "Message: \"" + deletedMessage + "\" successfully deleted.";
            } catch (IOException e) {
                return "Error deleting message: " + e.getMessage();
            }
        }
        return "Message hash not found.";
    }
    
    // f. Display report of all stored messages
    public static String displayReport() {
        StringBuilder report = new StringBuilder();
        report.append("========================================\n");
        report.append("         STORED MESSAGES REPORT\n");
        report.append("========================================\n\n");
        
        JSONArray stored = readMessagesFromJSON();
        if (stored.isEmpty()) {
            return report.toString() + "No stored messages found.";
        }
        
        for (Object obj : stored) {
            JSONObject msg = (JSONObject) obj;
            report.append("Message Hash: ").append(msg.get("messageHash")).append("\n");
            report.append("Recipient: ").append(msg.get("recipient")).append("\n");
            report.append("Message: ").append(msg.get("message")).append("\n");
            report.append("----------------------------------------\n");
        }
        
        report.append("\nTotal stored messages: ").append(stored.size());
        return report.toString();
    }
    
    // Getters for arrays (for testing)
    public static Message[] getSentMessagesArray() { return sentMessagesArray; }
    public static Message[] getDisregardedMessagesArray() { return disregardedMessagesArray; }
    public static Message[] getStoredMessagesArray() { return storedMessagesArray; }
    public static String[] getMessageHashesArray() { return messageHashesArray; }
    public static String[] getMessageIDsArray() { return messageIDsArray; }
    public static int getSentCount() { return sentCount; }
    public static int getDisregardedCount() { return disregardedCount; }
    public static int getStoredCount() { return storedCount; }
    
    // Instance getters
    public String getMessageID() { return messageID; }
    public int getNumMessagesSent() { return numMessagesSent; }
    public String getRecipient() { return recipient; }
    public String getMessage() { return message; }
    public String getMessageHash() { return messageHash; }
    public boolean isSent() { return isSent; }
    public boolean isStored() { return isStored; }
    public boolean isDisregarded() { return isDisregarded; }
    
    // Setters for testing
    public void setMessageID(String messageID) { this.messageID = messageID; }
    public void setMessage(String message) { this.message = message; }
    public void setRecipient(String recipient) { this.recipient = recipient; }
    public void setNumMessagesSent(int num) { this.numMessagesSent = num; }
    public void setMessageHash(String hash) { this.messageHash = hash; }
}

