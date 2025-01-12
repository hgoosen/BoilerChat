import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.runners.Enclosed;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.RunWith;
import org.junit.runner.notification.Failure;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

import static org.junit.Assert.*;

/**
 * A framework to run test cases for Database.java.
 *
 * @author Henri Goosen (hgoosen), Samit Gadekar (SamitGadekar)
 * @version Nov 3, 2024
 */

@RunWith(Enclosed.class)
public class RunDatabaseTest {

    public static void main(String[] args) {
        // Runs all the tests in the TestCase class
        Result result = JUnitCore.runClasses(RunDatabaseTest.TestCase.class);
        if (result.wasSuccessful()) {
            System.out.println("Success - Database Test ran successfully");
        } else {
            for (Failure failure : result.getFailures()) {
                System.out.println("Failure - Database Test - " + failure.toString());
            }
        }
    }

    public static class TestCase {

        private Database database;
        private static final String USER_FILE_PATH = "test_resources/testUserFile.txt";
        private static final String CHAT_FILE_PATH = "test_resources/testChatFile.txt";
        private static User[] testUsers = new User[6];
        private static Chat[] testChats = new Chat[2];

        @Before
        public void setUpResources() {
            // Deleting test files
            deleteFileIfExists(USER_FILE_PATH);
            deleteFileIfExists(CHAT_FILE_PATH);

            // Making the test user and chat objects
            try {
                User alice = new User("Alice", "password");
                User bobby = new User("Bobby", "password");
                User charlie = new User("Charlie", "password");
                User david = new User("David", "password");
                User ethan = new User("Ethan", "password");
                User freddy = new User("Freddy", "password");

                BufferedImage bobbyImage= ImageIO.read(new File("test_resources/img.jpg"));
                ImageIcon bobbyPhoto = new ImageIcon(bobbyImage);
                bobby.setProfilePhoto(bobbyPhoto);

                BufferedImage davidImage = ImageIO.read(new File("test_resources/img.jpg"));
                ImageIcon davidPhoto = new ImageIcon(davidImage);
                david.setProfilePhoto(davidPhoto);

                alice.addFriend("friend1"); // friend
                alice.addBlocked("blocked1"); // blocked

                bobby.addFriend("friend2.1"); // friends
                bobby.addFriend("friend2.2");
                bobby.addBlocked("blocked2.1"); // blocked
                bobby.addBlocked("blocked2.2");
                bobby.addBlocked("blocked2.3");

                testUsers[0] = alice;
                testUsers[1] = bobby;
                testUsers[2] = charlie;
                testUsers[3] = david;
                testUsers[4] = ethan;
                testUsers[5] = freddy;


                testChats[0] = new Chat (new User[] {alice, bobby}, "testChatHistory.txt");
                testChats[1] = new Chat (new User[] {bobby, charlie}, "testChatHistory2.txt");
            } catch (Exception e) {
                System.out.println("Failed to set up user and chat objects for test files: " + e.getMessage());
            }

            // Writing User objects to the user file
            try (ObjectOutputStream userOos = new ObjectOutputStream(new FileOutputStream(USER_FILE_PATH))) {
                userOos.writeObject(testUsers[0]); // alice
                userOos.writeObject(testUsers[1]); // bobby
                userOos.writeObject(testUsers[2]); // charlie
            } catch (IOException e) {
                System.out.println("Failed to write user objects to file: " + e.getMessage());
            }

            // Create chat test history files with Message objects
            createTestChatHistories();

            // Writing Chat objects to the chat file
            try (ObjectOutputStream chatOos = new ObjectOutputStream(new FileOutputStream(CHAT_FILE_PATH))) {
                for (Chat chat : testChats) {
                    chatOos.writeObject(chat);
                }
            } catch (IOException e) {
                System.out.println("Failed to write chat objects to file: " + e.getMessage());
            }

            // Ensuring necessary files exist and are set up
            assertTrue("User input file not created", new File(USER_FILE_PATH).exists());
            assertTrue("Chat input file not created", new File(CHAT_FILE_PATH).exists());

            // initializing database with those files
            database = new Database(USER_FILE_PATH, CHAT_FILE_PATH);
        }
        private void deleteFileIfExists(String fileName) {
            // Deletes a file if it exists
            // Helper method for setUpResources() and tearDown()
            try {
                Files.deleteIfExists(Paths.get(fileName));
            } catch (IOException e) {
                System.out.println("Failed to delete file: " + fileName + " - " + e.getMessage());
            }
        }
        private void createTestChatHistories() {
            try {
                for (int option = 0; option < 2; option++) {
                    if (option == 0) {
                        // Texts for the first chat
                        testChats[0].addMessage(new Message("Alice", "Hello Bobby!"));
                        testChats[0].addMessage(new Message("Bobby", "Hi Alice! How are you?"));
                        testChats[0].addMessage(new Message("Alice", "I'm good, thanks!"));

                    } else if (option == 1) {
                        // Texts for the second chat
                        testChats[1].addMessage(new Message("Bobby", "What's up Charlie?"));
                        testChats[1].addMessage(new Message("Charlie", "Nothing much... how about you?"));
                        testChats[1].addMessage(new Message("Bobby", "Just watching TV. Wanna join?"));
                        testChats[1].addMessage(new Message("Charlie", "Sure, let's go!"));
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed to write test chat histories: " + e.getMessage());
            }
        }

        @After
        public void tearDown() {
            // Clean up any test files created during the tests
            deleteFileIfExists(USER_FILE_PATH);
            deleteFileIfExists(CHAT_FILE_PATH);
            deleteFileIfExists("test_resources/testChatHistory.txt");
            deleteFileIfExists("test_resources/testChatHistory2.txt");
        }

        private void databaseInitialize() {
            try { // Reads in data for tests

                database.readUser();
                database.readChat();
                database.assignChat(testChats[0]);
            } catch (DatabaseReadException e) {
                System.out.println("Error initializing database: " + e.getMessage());
            }
        }

        @Test
        public void testAddUser() {
            databaseInitialize();

            // Should add new user
            assertTrue("Failure: Database failed to add new user", database.addUser(testUsers[3])); // david
            assertTrue("Failure: Database failed to add new user without friends or blocked", database.addUser(testUsers[4])); // ethan
            // Should not add duplicate user
            assertFalse("Failure: Database should not allow adding duplicate users", database.addUser(testUsers[0])); // alice
            assertFalse("Failure: Database should not allow adding duplicate users", database.addUser(testUsers[4])); // ethan
        }

        @Test
        public void testReadUser() throws DatabaseReadException {
            database.readUser();
            // 3 Users added
            assertEquals("Failure: Database failed to read the correct number of users", 3, database.getUsers().size());
            assertEquals("Failure: Database user 1 username incorrect", "Alice", database.getUsers().get(0).getUsername());
            assertEquals("Failure: Database user 2 username incorrect", "Bobby", database.getUsers().get(1).getUsername());
            assertEquals("Failure: Database user 3 username incorrect", "Charlie", database.getUsers().get(2).getUsername());

            // 1 more User added (1 duplicate not added)
            // Writing User objects to the user file
            try (ObjectOutputStream userOos = new ObjectOutputStream(new FileOutputStream(USER_FILE_PATH))) {
                userOos.writeObject(testUsers[0]); // alice, twice in the file
                userOos.writeObject(testUsers[3]); // david, new in the file
            } catch (IOException e) {
                fail("Failed to write additional user objects to file: " + e.getMessage());
            }
            database.readUser();

            assertEquals("Failure: Database failed to read the correct number of users", 4, database.getUsers().size());
            assertEquals("Failure: Database returned incorrect username after reading users", "David", database.getUsers().get(3).getUsername());
        }

        @Test
        public void testEditUser() {
            databaseInitialize();

            User aliceNew = null;
            User freddyNew = null;
            try {
                aliceNew = new User("Alice", "newPassword");
                freddyNew = new User("Freddy", "newPassword");
            } catch (InvalidUserException e) {
                fail("Failed to set up user object(s) for test: " + e.getMessage());
            }
            assertTrue("Failure: Database failed to edit user", database.editUser(testUsers[0], aliceNew)); // alice
            assertFalse("Failure: Database modified a nonexistent user", database.editUser(testUsers[5], freddyNew)); // freddy
        }

        @Test
        public void testAddChat() throws DatabaseReadException{
            database.readUser();

            // Adds and checks chat properties
            assertTrue("Failure: Database did not add chat", database.addChat(testChats[0])); // aliceBobby
            assertEquals("Failure: Database failed to add the correct number of chats", 1, database.getAllChats().size());
            assertEquals("Failure: Database failed to add the correct number of messages in the chat", 3, database.getAllChats().get(0).getHistory().size());

            // Adds and checks chat properties
            assertTrue("Failure: Database did not add chat", database.addChat(testChats[1])); // bobbyCharlie
            assertEquals("Failure: Database failed to add the correct number of chats", 2, database.getAllChats().size());
            assertEquals("Failure: Database failed to add the correct number of messages in the chat", 4, database.getAllChats().get(1).getHistory().size());

            // Should not add chat
            assertFalse("Failure: Database added a duplicate chat", database.addChat(testChats[0])); // aliceBobby, once again
            assertEquals("Failure: Database failed to add the correct number of chats", 2, database.getAllChats().size());
        }

        @Test
        public void testReadChat() throws DatabaseReadException {
            database.readUser();
            database.readChat();
            // Checks number of chats read in
            assertEquals("Failure: Database failed to read the correct number of chats", 2, database.getAllChats().size());
            // Checks users in the chat read in
            assertEquals("Failure: incorrect user in chat", "Alice", database.getAllChats().get(0).getUsers()[0].getUsername());
            assertEquals("Failure: incorrect user in chat", "Bobby", database.getAllChats().get(0).getUsers()[1].getUsername());
            assertEquals("Failure: incorrect user in chat", "Bobby", database.getAllChats().get(1).getUsers()[0].getUsername());
            assertEquals("Failure: incorrect user in chat", "Charlie", database.getAllChats().get(1).getUsers()[1].getUsername());
            // Checks number of messages in each chat
            assertEquals("Failure: incorrect number of messages in chat", 3, database.getAllChats().get(0).getHistory().size());
            assertEquals("Failure: incorrect number of messages in chat", 4, database.getAllChats().get(1).getHistory().size());
        }

        @Test
        public void testAssignChat() throws DatabaseReadException{
            database.readUser();
            database.readChat();

            // Check number of chats per user
            database.assignChat(testChats[0]);
            assertEquals("Failure: Database assigned incorrect number of chats to user", 1, database.getUsers().get(0).getChats().size()); // Alice - one chat
            assertEquals("Failure: Database assigned incorrect number of chats to user", 1, database.getUsers().get(1).getChats().size()); // Bobby - two chats
            assertEquals("Failure: Database assigned incorrect number of chats to user", 0, database.getUsers().get(2).getChats().size()); // Charlie - one chat

            database.assignChat(testChats[1]);
            assertEquals("Failure: Database assigned incorrect number of chats to user when called again", 1, database.getUsers().get(0).getChats().size());
            assertEquals("Failure: Database assigned incorrect number of chats to user when called again", 2, database.getUsers().get(1).getChats().size());
            assertEquals("Failure: Database assigned incorrect number of chats to user when called again", 1, database.getUsers().get(2).getChats().size());
        }

        @Test
        public void testSearchUser() {
            databaseInitialize();
            ArrayList<User> testArray = new ArrayList<>();

            testArray.add(testUsers[1]); // bobby
            assertEquals("Failure: Database returns incorrect search results", testArray, database.searchUser("Bobby"));

            User bobbyTemp = null;
            try {
                bobbyTemp = new User("BobbyTemp", "password");
            } catch (InvalidUserException e) {
                fail("Failed to set up user object(s) for test: " + e.getMessage());
            }
            database.addUser(bobbyTemp);
            testArray.add(bobbyTemp);
            assertEquals("Failure: Database returns incorrect search results", testArray, database.searchUser("Bobby"));
        }

        @Test
        public void testAddFriend() throws InvalidUserException {
            databaseInitialize();

            User alice = new User("aliceUser", "password");
            User freddy = new User("freddyUser", "password");

            database.addUser(alice);
            database.addUser(freddy);

            assertTrue("addFriend method returned false!",
                    database.addFriend(alice.getUsername(), freddy.getUsername()));
            assertTrue("Friends not added correctly",
                    alice.getFriends().contains(freddy.getUsername()) &&
                    freddy.getFriends().contains(alice.getUsername()));
        }

        @Test
        public void testRemoveFriend() throws InvalidUserException {
            databaseInitialize();

            User alice = new User("aliceUser", "password");
            User freddy = new User("freddyUser", "password");

            database.addUser(alice);
            database.addUser(freddy);

            assertTrue("testAddFriend failed",
                    database.addFriend(alice.getUsername(), freddy.getUsername()));
            assertTrue("testAddFriend failed",alice.getFriends().contains(freddy.getUsername()) &&
                    freddy.getFriends().contains(alice.getUsername()));

            assertTrue("removeFriend method returned false!",
                    database.removeFriend(alice.getUsername(), freddy.getUsername()));
            assertFalse("Friends not removed correctly", alice.getFriends().contains(freddy.getUsername()) ||
                    freddy.getFriends().contains(alice.getUsername()));

        }

        @Test
        public void testAddBlocked() throws InvalidUserException {
            databaseInitialize();

            User alice = new User("aliceUser", "password");
            User freddy = new User("freddyUser", "password");

            database.addUser(alice);
            database.addUser(freddy);

            assertTrue("addBlocked method returned false!",
                    database.addBlocked(alice.getUsername(), freddy.getUsername()));
            assertTrue("User not blocked.",
                    alice.getBlocked().contains(freddy.getUsername()));
        }

        @Test
        public void testRemoveBlocked() throws InvalidUserException {
            databaseInitialize();

            User alice = new User("aliceUser", "password");
            User freddy = new User("freddyUser", "password");

            database.addUser(alice);
            database.addUser(freddy);

            assertTrue(database.addBlocked(alice.getUsername(),
                    freddy.getUsername()));
            assertTrue(alice.getBlocked().contains(freddy.getUsername()));

            assertTrue("removeBlocked method returned false!",
                    database.removeBlocked(alice.getUsername(),
                            freddy.getUsername()));
            assertFalse("User not unblocked.",
                    alice.getBlocked().contains(freddy.getUsername()));
        }

        @Test
        public void testBlockFriend() throws InvalidUserException {
            databaseInitialize();

            User alice = new User("aliceUser", "password");
            User freddy = new User("freddyUser", "password");
            database.addUser(alice);
            database.addUser(freddy);

            assertTrue(database.addFriend(alice.getUsername(), freddy.getUsername()));
            assertTrue(alice.getFriends().contains(freddy.getUsername()) &&
                    freddy.getFriends().contains(alice.getUsername()));

            assertTrue(database.addBlocked(alice.getUsername(),
                    freddy.getUsername()));
            assertTrue(alice.getBlocked().contains(freddy.getUsername()));

            assertFalse("Friends not removed correctly",
                    alice.getFriends().contains(freddy.getUsername()) ||
                    freddy.getFriends().contains(alice.getUsername()));
        }

        @Test
        public void testWriteDatabase() throws IOException {
            databaseInitialize();

            // Removes previously initialized testing data to ensure a clean slate for the database
            deleteFileIfExists(USER_FILE_PATH);
            deleteFileIfExists(CHAT_FILE_PATH);

            // Verify that the files were actually deleted
            assertFalse("User file still exists after cleanup", new File(USER_FILE_PATH).exists());
            assertFalse("Chat file still exists after cleanup", new File(CHAT_FILE_PATH).exists());

            // Write data to the database
            assertTrue("Failure: Database failed to write the data", database.writeDatabase());

            // Check the user output file by reading User objects from it
            try (ObjectInputStream userOis = new ObjectInputStream(new FileInputStream(USER_FILE_PATH))) {
                User maybeAlice = (User) userOis.readObject();
                User maybeBobby = (User) userOis.readObject();
                User maybeCharlie = (User) userOis.readObject();

                // Verify User objects
                assertEquals("Incorrect username written...", testUsers[0].getUsername(), maybeAlice.getUsername());
                assertEquals("Incorrect password written...", testUsers[0].getPassword(), maybeAlice.getPassword());
                assertTrue("Expected friend but not found...", maybeAlice.getFriends().contains("friend1"));
                assertTrue("Expected blocked but not found...", maybeAlice.getBlocked().contains("blocked1"));
                assertNull("Expected no profile picture...", maybeAlice.getProfilePhoto());

                assertEquals("Incorrect username written...", testUsers[1].getUsername(), maybeBobby.getUsername());
                assertEquals("Incorrect password written...", testUsers[1].getPassword(), maybeBobby.getPassword());
                assertTrue("Expected friend but not found...", maybeBobby.getFriends().contains("friend2.1"));
                assertTrue("Expected friend but not found...", maybeBobby.getFriends().contains("friend2.2"));
                assertTrue("Expected blocked but not found...", maybeBobby.getBlocked().contains("blocked2.1"));
                assertTrue("Expected blocked but not found...", maybeBobby.getBlocked().contains("blocked2.2"));
                assertTrue("Expected blocked but not found...", maybeBobby.getBlocked().contains("blocked2.3"));
                assertNotNull("Profile picture not found...", maybeBobby.getProfilePhoto());

                assertEquals("Incorrect username written...", testUsers[2].getUsername(), maybeCharlie.getUsername());
                assertEquals("Incorrect password written...", testUsers[2].getPassword(), maybeCharlie.getPassword());
                assertEquals("No friends expected...", 0, maybeCharlie.getFriends().size());
                assertEquals("No blocked expected...", 0, maybeCharlie.getBlocked().size());
                assertNull("Expected no profile picture...", maybeCharlie.getProfilePhoto());
            } catch (ClassNotFoundException e) {
                fail("Failed to read user objects and check: " + e.getMessage());
            }

            // Check the chat output file by reading Chat objects from it
            try (ObjectInputStream chatInputStream = new ObjectInputStream(new FileInputStream(CHAT_FILE_PATH))) {
                Chat chat1 = (Chat) chatInputStream.readObject();
                Chat chat2 = (Chat) chatInputStream.readObject();

                // Verify Chat objects
                assertTrue("Incorrect user in chat...", chat1.getUsers()[0].equals(testUsers[0]) || chat1.getUsers()[1].equals(testUsers[0])); // alice
                assertTrue("Incorrect user in chat...", chat1.getUsers()[0].equals(testUsers[1]) || chat1.getUsers()[1].equals(testUsers[1])); // bobby

                assertTrue("Incorrect user in chat...", chat2.getUsers()[0].equals(testUsers[1]) || chat2.getUsers()[1].equals(testUsers[1])); // bobby
                assertTrue("Incorrect user in chat...", chat2.getUsers()[0].equals(testUsers[2]) || chat2.getUsers()[1].equals(testUsers[2])); // charlie
            } catch (ClassNotFoundException e) {
                fail("Failed to read chat objects and check: " + e.getMessage());
            }
        }

        // Test for handling exceptions when reading nonexistent files
        @Test(expected = DatabaseReadException.class)
        public void testReadUserFileNotFound() throws DatabaseReadException {
            database = new Database("nonexistentFile.txt", CHAT_FILE_PATH);
            database.readUser();
        }
    }

}
