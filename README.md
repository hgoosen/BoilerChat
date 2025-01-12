# CS18000 Team Project

## 1. Compile & Run

### Instructions on how to compile and run your project.
1. Run test cases to check if the code functions.
2. Only tests starting with "Run" should be run.

### Detailed I/O Instructions
1. Start Server.java
2. Start Client.java, it should automatically connect to the Server.
4. Afterward you will see a login page. From here you can:
   1. Login - Using existing credentials
   2. Register - Go to the register page to register as a user
5. After logging in you will be met with the options to:
   1.  Chat
      1. Allows a user to view and click on chats 
      2. To start a new chat one must type the user's full username into the text bar and click "Start New Chat"
         1. After clicking on a chat a user can send messages by typing them into the textbox and clicking "Send"
         2. A user can also click select to delete different messages by either clicking on one and clicking "Delete", or clicking "Delete ALL Your Messages"
            1. Trying to delete any message will popup a confirmation window for the user.
   4.  Search
      1.  Allows the user to search for other users by typing any part of a username into the search bar and clicking search a user can then click on any username to go their profile
         1. On another user's profile, one can friend, unfriend, and block them with the respective buttons.
   5.  Settings - Allows the user to access and manage multiple parts of their account including
      1. Friends - Can view and remove friends
      2. Blocked - Can view and unblock users
      3. Choose a Profile Photo - Set a profile photo from an image on their local device
      4. Change Privacy - Toggle between public and private
      5. Back - Returns to the previous page  
   6.  Logout - Self-explanatory        



## 2. Submission

### A list of who submitted which parts of the assignment.
- Submitted Repository To Vocareum Workspace: Henri
- Wrote Design Choices Essay: Keshav
- Wrote Functionality Essay: Isaac

## 3. Description

### A detailed description of each class. This should include the functionality included in the class, the testing done to verify it works properly, and its relationship to other classes in the project.

### User:
- The user class is a representation of a user on the server side. A user has a username, password, as well as a list of chats, a list of Users that are friends, and a list of Users they have blocked.
- Users can friend, message, and block other users and cannot share the same username.
- The test cases check for all fields of user being initialized correctly along with the methods to change those fields.
- `User(String username, String password) throws InvalidUserException`
> Constructor in new user creation.
- `boolean message(Chat chat, Message message)`
> Messages a given chat a given message. Checks exist to ensure a user can only message in chats they are in and are not blocked in.

### Message:
- The message class is a representation of a message on the server side. A message has a sender and the text contents. This class is utilized as a list in Chat to create a chat history.
- `Message(String sender, String text)`
> Constructor in new message creation.
> 
- ### PhotoMessage:
- The PhotoMessage class is a representation of a photo message on the server side. A photo message has a sender, an optional text, and the photo.
- `PhotoMessage(String sender, String filepath)`
> Constructor in new photo message creation.

### Chat:
- The chat class is a representation of a chat on the server side. A chat has two users and a message history. If needed, you can create a new chat without a history (For new chats). All users have a list of chats.
- `Chat(User[] users)`
> Constructor in new chat creation.
> There cannot exist two chats with `[user1, user2]` and `[user2, user1]` because you cannot have two chats with the same user. This is checked in database reconstruction / new chat creation
- The test cases test for all fields being initialized correctly along with the data of chat history and messages being stored correctly.
- 
### Database:
- The database class is designed to manage file input and output operations for users and chats. It provides functionalities for reading from and writing to user and chat data files, managing user accounts, and handling chat sessions.
- `void readUser() throws DatabaseReadException`
> Reads user data from the specified user file and populates the users list.
- `void readChat() throws DatabaseReadException`
> Reads chat data from the specified chat file and populates the chats list.
- `boolean addUser(User user)`
> Adds a new user to the system.
- `boolean addChat(String data)`
> Adds a new chat session to the system.
- `ArrayList<User> searchUser(String search)`
> Searches users based on a search string.
- `boolean writeDatabase()`
> Writes the current state of users and chats to their respective files.
- The test cases check for proper I/O handling, search function, read and write functions.

### DatabaseReadException:
- An exception for handling errors when reading from the database.
### InvalidUserException:
- An exception for handling errors when reading or creating users.
### InvalidPhotoException:
- An exception for handling errors when reading and creating photos.

### Client.java
- The Client class provides the GUI for user interaction. It also manages a network connection with Server.java
- `public static void main(String[] args)`
> Connects to a preset server port. ("localhost", 4242)
- `private static void cleanup()`
> A helper method to handle closing resources.
- `private static void sendToServer()`
> A helper method to handle sending a command to the server.
- The client class contains many helper methods to manage server-client communication.

### Server.java
- The Server class manages. It also manages a network connection with Client.java
- `public static void main(String[] args)`
> Starts a ServerSocket with a preset port. (4242)
- Only one server can exist at a time. Server employs an extensive, infinitely looping switch statement tree to read and process client requests.

### `RunDatabaseInterfaceTest.java`
The `RunDatabaseInterfaceTest` class designed to verify the existence of the `DatabaseInterface` and ensure it’s implemented by at least one of the specified classes.

1. `testExistence()`
-  This test checks whether `DatabaseInterface` exists and is declared as an interface.
-  Using `Class.forName("DatabaseInterface")`, it attempts to locate `DatabaseInterface`.
-  If it finds it, it checks that it is indeed an interface using `interfaceClass.isInterface()`.
-  If `DatabaseInterface` does not exist, a `ClassNotFoundException` is thrown, causing the test to fail with a message indicating its absence.
2. `testImplementation()`
-  This test that `DatabaseInterface` is implemented.
-  It iterates over the `classesToCheck` array and checks that `DatabaseInterface` is assignable from each class.
-  If any class implements `DatabaseInterface`, the loop breaks, and found is set to true.
-  Finally, it asserts that at least one class implements `DatabaseInterface`.
-  If none do, the test fails with the message "DatabaseInterface is not implemented."


### `RunDatabaseTest.java`
This class provides a comprehensive suite of unit tests for the `Database` class, verifying its core functionalities and integration with user and chat data files. The tests ensure that `Database` methods handle data reading, writing, and manipulation accurately.

#### Class Purpose & Structure
The `RunDatabaseTest` class is structured to execute multiple test cases using JUnit, organized within an inner class `TestCase`. These tests simulate real-world scenarios, such as adding users, modifying data, and verifying chat histories. Each test is designed to check for expected outcomes and handle error cases, ensuring robust performance of `Database` methods.

#### Contents
1. `main` Method
-  The `main` method runs all test cases within `TestCase` and outputs the results.
-  It reports each test's success or failure, helping developers quickly assess the state of `Database` functionalities.
2. `TestCase` Class
-  The `TestCase` class contains methods for setting up resources, defining specific test cases, and cleaning up after tests.
-  It focuses on various aspects of `Database` operations, with methods to prepare necessary files, validate database actions, and manage the lifecycle of test files.
3. `setUpResources()` Method
-  This method runs before each test, creating sample user and chat data files required for testing.
-  It writes predefined data into files like `testUserInput.txt` and `testChatInput.txt`, which simulate users, chats, and chat histories.
-  Additionally, it creates sample chat histories with serialized `Message` objects in specific files, ensuring the `Database` has structured data for reading and processing.
4. `deleteFileIfExists(String fileName)` Method
-  A helper method used to delete temporary files if they exist.
-  This supports both `setUpResources` and `tearDown` methods, ensuring a clean environment before and after each test.
5. `createTestChatHistory(String fileName, int option)` Method
-  This method creates chat history files based on given data for different chat scenarios, writing serialized `Message` objects to files.
-  Each file simulates a real chat history that `Database` can read and process during testing.
6. `tearDown()` Method
-  This method cleans up temporary files created during tests, including user and chat files and any test chat history files.
-  It ensures that each test runs in isolation without interference from leftover data.

#### Test Cases
1. `testAddUser()`
-  Tests the `addUser` functionality by adding new users and checking for duplicates.
-  Ensures that new users are added correctly while preventing duplicates.
2. `testReadUser()`
-  Verifies the `readUser` method by reading users from the input file and checking if they’re correctly stored in the `Database`.
-  It also tests reading additional users while preventing duplicate entries.
3. `testEditUser()`
-  Checks the `editUser` method by attempting to modify user data.
-  Confirms that existing users can be updated while non-existent users remain unaffected.
4. `testAddChat()`
-  Tests the `addChat` method by adding new chat sessions between users and ensuring duplicate chats are not added.
-  It also validates the integrity of chat histories, ensuring that messages are correctly stored.
5. `testReadChat()`
-  Tests the `readChat` method by reading chat data from input files, verifying users involved in each chat, and confirming the number of messages in each chat history.
6. `testAssignChat()`
-  Checks the `assignChat` method, ensuring that chats are assigned correctly to each user based on the relationships in the chat data.
-  It verifies that repeated calls do not duplicate chats.
7. `testWriteDatabase()`
-  Tests the `writeDatabase` method to ensure that user and chat data are saved correctly to output files.
-  It verifies the presence and accuracy of data in the written files.
8. `testReadUserFileNotFound()`
-  This test expects a `DatabaseReadException` when attempting to read a nonexistent user file, verifying the Database’s ability to handle missing files.


### `RunMessageInterfaceTest.java`
The `RunMessageInterfaceTest` class is designed to check for the existence of `MessageInterface` and ensure that at least one of the specified classes implements it.

1. `testExistence()`
-  This test verifies the existence of `MessageInterface` and checks that it is declared as an interface.
-  It uses `Class.forName("MessageInterface")` to dynamically load `MessageInterface` by name.
-  If `MessageInterface` exists, it then confirms that it is an interface by using `interfaceClass.isInterface()`.
-  If `MessageInterface` cannot be found, a `ClassNotFoundException` is thrown, causing the test to fail with the message "MessageInterface does not exist."
2. `testImplementation()`
-  This test verifies that `MessageInterface` is implemented.
-  It iterates over the `classesToCheck` array, using `interfaceClass.isAssignableFrom(clazz)` to check if `MessageInterface` is assignable from each class.
-  If any class implements `MessageInterface`, the loop breaks, and found is set to true.
-  The test then asserts that found is true, indicating that at least one class implements `MessageInterface`.
-  If none do, the test fails with the message "MessageInterface is not implemented."


### `RunMessageTest.java`
This class provides a thorough suite of unit tests for the `Message` class, covering essential methods such as setting, retrieving, and formatting message data. The tests confirm that `Message` objects are initialized and modified as expected, ensuring that the class behaves correctly in handling message data.

#### Class Purpose & Structure
The `RunMessageTest` class is organized to validate the `Message` class's core functionalities through a series of JUnit tests. The tests are structured within an inner class `TestCase`, focusing on testing both constructors, getters, setters, and the `toString` method.

#### Contents
1. `main` Method
-  The `main` method runs all test cases within `TestCase` and outputs the results.
-  It provides a summary indicating whether the `Message` tests passed or failed, helping to identify potential issues quickly.
2. `TestCase` Class
-  The `TestCase` class includes setup methods and individual tests, each targeting specific `Message` functionalities.
-  The class initializes a `Message` instance before each test, ensuring a clean slate for accurate and isolated testing.
3. `setUp()` Method
-  This method runs before each test, creating a new `Message` object with predefined sender and text values.
-  It prepares a sample message, which serves as the basis for testing various `Message` functionalities.

#### Test Cases
1. `testConstructorWithSenderAndText()`
-  Tests the constructor that takes sender and text parameters, verifying that both are correctly assigned upon object creation.
2. `testConstructorWithData()`
-  Tests the constructor that takes a single data string (e.g., "sender,text"), ensuring that it parses the data correctly into sender and text fields.
3. `testGetSender()`
-  Validates the `getSender()` method by checking if it returns the correct sender value.
4. `testGetText()`
-  Checks the `getText()` method to confirm that it retrieves the correct message text.
5. `testSetSender()`
-  Tests the `setSender()` method to ensure that the sender can be updated and retrieves the correct new value.
6. `testSetText()`
-  Validates the `setText()` method, ensuring that it updates the text field correctly and retrieves the new value.
7. `testToString()`  
-  Tests the `toString()` method for accurate formatting, verifying the output format of "senderName: messageText".
-  Also re-tests after modifying the sender and text fields to confirm that `toString()` reflects these changes correctly.

## GUI Screens

### Log In
- Self-Explanatory

### Register
- Self-Explanatory

### User Main Menu
- Self-Explanatory

### Chat
- Access existing chats and type username to start new chat. Username reader requires full, exact username.

### Search
- Does not require full username but is case-sensitive.

### Settings
- Self-Explanatory
