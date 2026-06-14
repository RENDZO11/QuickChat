/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/UnitTests/JUnit4TestClass.java to edit this template
 */
package com.mycompany.quickchat;

import org.junit.Test;
import org.junit.Before;
import static org.junit.Assert.*;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

/**
 *
 * @author RENDZO
 */
public class MessageTest {
 
  private Message message1;
    private Message message2;
    private Message message3;
    private Message message4;
    
    @Before
    public void setUp() {
        message1 = new Message(0);
        message2 = new Message(1);
        message3 = new Message(2);
        message4 = new Message(3);
    }
    
    private void setupTestData() {
        // Message 1: Sent
        message1.setMessageID("1111111111");
        message1.setRecipient("+27834557896");
        message1.setMessage("Did you get the cake?");
        message1.createMessageHash();
        message1.sentMessage(1);
        
        // Message 2: Stored
        message2.setMessageID("2222222222");
        message2.setRecipient("+27838884567");
        message2.setMessage("Where are you? You are late! I have asked you to be on time.");
        message2.createMessageHash();
        message2.sentMessage(3);
        
        // Message 3: Disregarded
        message3.setMessageID("3333333333");
        message3.setRecipient("+27834484567");
        message3.setMessage("Yohoooo, I am at your gate.");
        message3.createMessageHash();
        message3.sentMessage(2);
        
        // Message 4: Sent
        message4.setMessageID("4444444444");
        message4.setRecipient("08388884567");
        message4.setMessage("It is dinner time!");
        message4.createMessageHash();
        message4.sentMessage(1);
    }
    
    @Test
    public void testSentMessagesArray() {
        setupTestData();
        Message[] sentArray = Message.getSentMessagesArray();
        int sentCount = Message.getSentCount();
        assertTrue("Sent array should have at least 2 messages", sentCount >= 2);
        assertNotNull("First sent message should not be null", sentArray[0]);
    }
    
    @Test
    public void testDisplayLongestStoredMessage() {
        setupTestData();
        String result = Message.displayLongestStoredMessage();
        assertTrue("Should contain the longest message", 
                   result.contains("Where are you? You are late!"));
    }
    
    @Test
    public void testSearchByMessageID_Found() {
        // Create and save a message to JSON so we can find it
        Message msg = new Message(0);
        msg.setMessageID("5555555555");
        msg.setRecipient("08388884567");
        msg.setMessage("It is dinner time!");
        msg.createMessageHash();
        msg.sentMessage(3);  // Saves to JSON
        
        String result = Message.searchByMessageID("5555555555");
        assertTrue("Should find recipient", result.contains("08388884567"));
        assertTrue("Should find message", result.contains("It is dinner time!"));
    }
    
    @Test
    public void testSearchByMessageID_NotFound() {
        // Use an ID that no test creates
        String result = Message.searchByMessageID("0000000000");
        assertEquals("Message ID not found.", result);
    }
    
    @Test
    public void testSearchByRecipient_Found() {
        setupTestData();
        String result = Message.searchByRecipient("+27838884567");
        assertTrue("Should find message for recipient", 
                   result.contains("Where are you? You are late!"));
    }
    
    @Test
    public void testDeleteByMessageHash() {
        setupTestData();
        String hashToDelete = message2.getMessageHash();
        String result = Message.deleteByMessageHash(hashToDelete);
        assertTrue("Should confirm deletion", result.contains("successfully deleted"));
    }
    
    @Test
    public void testDisplayReport() {
        setupTestData();
        String report = Message.displayReport();
        assertTrue("Should have report header", report.contains("STORED MESSAGES REPORT"));
        assertTrue("Should contain Message Hash", report.contains("Message Hash:"));
    }
    
    @Test
    public void testStoreMessageToJSON() {
        Message msg = new Message(0);
        msg.setMessageID("9999999999");
        msg.setRecipient("+27999999999");
        msg.setMessage("Test JSON storage");
        msg.createMessageHash();
        msg.sentMessage(3);
        
        JSONArray stored = Message.readMessagesFromJSON();
        boolean found = false;
        for (Object obj : stored) {
            JSONObject json = (JSONObject) obj;
            if ("9999999999".equals(json.get("messageID"))) {
                found = true;
                break;
            }
        }
        assertTrue("Message should be stored in JSON", found);
    }
    
    // Part 2 regression tests
    @Test
    public void testMessageLength_Success() {
        String result = message1.validateMessage("Hi Mike, can you join us for dinner tonight?");
        assertEquals("Message ready to send.", result);
    }
    
    @Test
    public void testRecipientCell_Success() {
        String result = message1.checkRecipientCell("+27718693002");
        assertEquals("Cell phone number successfully captured.", result);
    }
    
    @Test
    public void testMessageHash_TestCase1() {
        message1.setMessageID("0012345678");
        message1.setNumMessagesSent(0);
        message1.setMessage("Hi Mike, can you join us for dinner tonight?");
        String hash = message1.createMessageHash();
        assertEquals("00:0:HITONIGHT", hash);
    }
    
    @Test
    public void testSentMessage_Send() {
        String result = message1.sentMessage(1);
        assertEquals("Message successfully sent.", result);
        assertTrue(message1.isSent());
    }
}