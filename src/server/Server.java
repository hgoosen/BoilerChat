import javax.swing.*;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * A class representing the Server and it's threads. main() delegates threads per client connected,
 * creating a thread for each user
 *
 * @author Keshav Sreekantham, Henri Goosen, Samit Gadekar, Benjamin Chen (chen5254)
 * @version December 2, 2024
 */
public class Server implements Runnable, ServerInterface {
    private final Socket socket;
    private static Database db;

    private BufferedReader bfr; // For Text-Based Communication
    private PrintWriter pw; // For Sending Plain Text Messages
    private ObjectInputStream ois; // For Object-Based Communication
    private ObjectOutputStream oos; // For Sending Serialized Objects

    private String username; // Username of the connected client.

    public Server(Socket socket) {
        this.socket = socket;
    }

    @Override
    public boolean logInUser(String user, String pass) {

        if (db.validateUser(user, pass)) {
            this.username = user;
            return true;
        } else {
            return false;
        }

    }

    public boolean registerUser(String user, String pass) {
        boolean ret;
        try {
            ret = db.addUser(new User(user, pass));
            System.out.println("Added user, current database user list: ");
            for (User printUser : db.getUsers()) {
                System.out.println(printUser.toString());
            }
        } catch (InvalidUserException e) {
            return false;
        }
        return ret;
    }


    @Override
    public void run() {
        try {
            this.bfr = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            this.pw = new PrintWriter(socket.getOutputStream());
            this.oos = new ObjectOutputStream(socket.getOutputStream());
            this.ois = new ObjectInputStream(socket.getInputStream());

            while (socket.isConnected()) {

                String input;
                if ((input = bfr.readLine()) != null) {
                    // In this version, the client assigns a command to each command followed by its args,
                    // then sends to server.
                    // ex: add_friend,JohnSmith would friend JohnSmith on whichever username the thread has.
                    System.out.println(this + " - Input : " + input);
                    Boolean ret = switch (input) {
                        case "log_in" -> logInUser(bfr.readLine(), bfr.readLine());
                        case "register" -> registerUser(bfr.readLine(), bfr.readLine());
                        case "add_friend" -> db.addFriend(this.username, bfr.readLine());
                        case "search_user" -> {
                            // Run searchUser(username) to find results of users
                            ArrayList<User> users = new ArrayList<>();
                            String searchTerm = bfr.readLine().trim();

                            System.out.println("Search Term: " + searchTerm);

                            if (!searchTerm.isBlank()) {
                                if (searchTerm.equals(";")) {
                                    users = db.searchUser("");
                                } else {
                                    users = db.searchUser(searchTerm);
                                }
                            }

                            for (int i = 0; i < users.size(); i++) {
                                if (users.get(i).toString().equals(this.username)) {
                                    users.remove(i); // Removing user that is searching from results
                                }
                            }

                            for (User s : users) {
                                pw.println(s.getUsername()); // Write each username out
                            }
                            pw.println("--STOP--"); // Keyword to mark end of results
                            pw.flush();

                            yield true; // Returning success
                        }
                        case "block_user" -> db.addBlocked(this.username, bfr.readLine());
                        case "set_profile_photo" -> {
                            ImageIcon photo = null;
                            try {
                                photo = (ImageIcon) ois.readObject();
                                yield db.setProfilePhoto(this.username, photo);
                            } catch (Exception e) {
                                yield false;
                            }
                        }
                        case "get_profile_photo" -> {
                            ImageIcon photo = db.getProfilePhoto(bfr.readLine());
                            if (photo != null) {
                                oos.writeObject(photo);
                                oos.flush();
                                yield true;
                            }
                            oos.writeObject(null);
                            yield false;
                        }
                        case "remove_friend" -> db.removeFriend(this.username, bfr.readLine());
                        case "unblock_user" -> db.removeBlocked(this.username, bfr.readLine());
                        case "set_privacy_status" ->
                                db.setPrivacyStatus(this.username, Boolean.parseBoolean(bfr.readLine()));

                        // Note: Below are things that send something back to client.

                        case "send_message" -> {
                            System.out.println("Sending message...");
                            String clientUsername = bfr.readLine();
                            String targetUsername = bfr.readLine();
                            String msg = bfr.readLine();
                            System.out.println("Called database.sendMessage()");
                            boolean sentMsg = db.sendMessage(clientUsername, targetUsername, new Message(clientUsername, msg));
                            System.out.println("send_message status sent to client.");
                            yield sentMsg;
                        }
                        case "send_photo_message" -> {
                            System.out.println("Sending photo message...");
                            String clientUsername = bfr.readLine();
                            String targetUsername = bfr.readLine();
                            ImageIcon photo = null;
                            try {
                                photo = (ImageIcon) ois.readObject();
                            } catch (ClassNotFoundException e) {
                                photo = null;
                            }
                            System.out.println("Called database.sendMessage() for photo");
                            boolean sentMsg = db.sendMessage(clientUsername, targetUsername, new PhotoMessage(clientUsername, photo));
                            System.out.println("send_message status sent to client.");
                            yield sentMsg;
                        }
                        case "delete_message" -> {
                            String clientUsername = bfr.readLine();
                            String targetUsername = bfr.readLine();
                            String validIndexes = bfr.readLine();
                            System.out.println("Inputted indexes: " + validIndexes);
                            String[] tempArray = validIndexes.split(",");
                            int[] intIndexes = new int[tempArray.length];
                            for (int i = 0; i < tempArray.length; i++) {
                                intIndexes[i] = Integer.parseInt(tempArray[i]);
                            }

                            // need to order indexes from large to small
                            Integer[] boxedIndexes = Arrays.stream(intIndexes) // convert to Integer array since Comparator doesn't work with primitives
                                    .boxed()
                                    .toArray(Integer[]::new);
                            Arrays.sort(boxedIndexes, Comparator.reverseOrder()); // sort in descending order
                            intIndexes = Arrays.stream(boxedIndexes) // convert back from Integer to int array
                                    .mapToInt(Integer::intValue)
                                    .toArray();
                            System.out.println("Ordered indexes: " + Arrays.toString(intIndexes));

                            try {
                                for (int i = 0; i < db.getUserFromUsername(clientUsername).getChats().size(); i++) {
                                    Chat chat = db.getUserFromUsername(clientUsername).getChats().get(i);
                                    // In the list of chats of username.
                                    // If the first or second user has the same username as targetUsername.
                                    if (chat.getUsers()[0].getUsername().equals(targetUsername) ||
                                            chat.getUsers()[1].getUsername().equals(targetUsername)) {
                                        chat.removeMessages(intIndexes);
                                    }
                                }
                            } catch (InvalidUserException e) {
                                System.out.println(e.getMessage());
                            }
                            yield true;
                        }
                        case "get_friends_list" -> {
                            oos.writeObject(db.getUserList(this.username, "friends"));
                            oos.flush();
                            yield true;
                        }
                        case "get_blocked_list" -> {
                            oos.writeObject(db.getUserList(this.username, "blocked"));
                            oos.flush();
                            yield true;
                        }
                        case "get_chats_list" -> {
                            String clientUsername = bfr.readLine();
                            ArrayList<Chat> chatsList = db.getChats(clientUsername);

                            try {
                                System.out.println(clientUsername + ": " + chatsList.size());

                                for (Chat eachChat : chatsList) {
                                    oos.writeObject(eachChat);
                                    User[] chatUsers = eachChat.getUsers();
                                    String otherUser = chatUsers[0].getUsername();
                                    if (otherUser.equals(clientUsername)) {
                                        otherUser = chatUsers[1].getUsername();
                                    }
                                    System.out.println("    " + otherUser + " (length: " + eachChat.getHistory().size() + ")");
                                }
                                oos.writeObject(null);
                                oos.flush();
                            } catch (Exception e) {
                                System.out.println("Error: " + e.getMessage());
                                e.printStackTrace();
                                oos.writeObject(null);
                                System.out.println("Chats List Length: null");
                                oos.flush();
                                yield false;
                            }

                            yield true;
                        }
                        case "get_message_history" -> {
                            String clientUsername = bfr.readLine();
                            String targetUsername = bfr.readLine();

                            System.out.println("Client User: " + clientUsername);
                            System.out.println("Target User: " + targetUsername);

                            try {
                                for (Chat chat : db.getUserFromUsername(clientUsername).getChats()) {
                                    if (chat.getUsers()[0].getUsername().equals(targetUsername) ||
                                            chat.getUsers()[1].getUsername().equals(targetUsername)) {
                                        System.out.println("Found chat with both Client and Target:");
                                        for (Message msg : chat.getHistory()) {
                                            System.out.println(msg);
                                            oos.writeObject(msg);
                                        }
                                        oos.writeObject(null);
                                        oos.flush();
                                        yield true;
                                    }
                                }
                            } catch (InvalidUserException e) {
                                System.out.println("Client is invalid in database...");
                                yield false;
                            }

                            // Makes a new chat if the chat doesn't already exist.
                            System.out.println("Could not find existing chat with both Client and Target:");
                            oos.writeObject(null);
                            oos.writeObject(null);
                            oos.flush();

                            // Checks if the user is in the database.
                            boolean exists = false;
                            for (User testUser : db.getUsers()) {
                                if (testUser.getUsername().equals(targetUsername)) {
                                    exists = true;
                                }
                            }
                            if (!exists) {
                                System.out.println("Client tried to send message to invalid Target user.");
                                yield false;
                            }

                            User[] newChatUsernames = new User[2];
                            try {
                                newChatUsernames[0] = db.getUserFromUsername(clientUsername);
                                System.out.println("Found client user.");
                                newChatUsernames[1] = db.getUserFromUsername(targetUsername);
                                System.out.println("Found target user.");
                                Chat chat = new Chat(newChatUsernames);
                                db.addChat(chat);
                                db.assignChat(chat);
                                System.out.println("Made a new chat for client: " + clientUsername + " and target: " + targetUsername);
                                yield true;
                            } catch (InvalidUserException e) {
                                System.out.println(e.getMessage());
                                yield false;
                            }
                        }
                        case "get_privacy_status" -> {
                            // Allows the user specify which privacy status
                            if (bfr.ready()) {
                                yield db.getPrivacyStatus(bfr.readLine());
                            } else {
                                yield db.getPrivacyStatus(this.username);
                            }
                        }

                        case "get_friend_requests" -> {
                            oos.writeObject(db.getUserList(this.username, "requests"));
                            oos.flush();
                            yield true;
                        }

                        case "is_user" -> {
                            String targetUsername = bfr.readLine();
                            for (User testUser : db.getUsers()) {
                                if (testUser.getUsername().equals(targetUsername)) {
                                    yield true; // found user
                                }
                            }
                            yield false; // did not find user
                        }

                        case "is_friend" -> {
                            String otherUser = bfr.readLine();
                            ArrayList<String> friends = db.getUserList(this.username, "friends");
                            for (String friend : friends) {
                                if (friend.equals(otherUser)) {
                                    yield true;
                                }
                            }
                            yield false;
                        }

                        case "is_blocked" -> {
                            String otherUser = bfr.readLine();
                            ArrayList<String> blockedUsers = db.getUserList(this.username, "blocked");
                            if (blockedUsers != null && !blockedUsers.isEmpty()) {
                                for (String blocked : blockedUsers) {
                                    if (blocked.equals(otherUser)) {
                                        yield true;
                                    }
                                }
                            }
                            yield false;
                        }

                        case "am_i_blocked" -> {
                            String clientUser = bfr.readLine();
                            String otherUser = bfr.readLine();
                            ArrayList<String> blockedUsers = db.getUserList(otherUser, "blocked");
                            if (blockedUsers != null && !blockedUsers.isEmpty()) {
                                for (String blocked : blockedUsers) {
                                    if (blocked.equals(clientUser)) {
                                        yield true;
                                    }
                                }
                            }
                            yield false;
                        }

                        default -> {
                            System.out.println("Error: Default Case Reached");
                            yield false;
                        }
                    };
                    System.out.println(this + " - Output : " + input + " : " + ret);
                    pw.println(ret); // Sends "true" or "false" or intended output.
                    pw.flush();
                    db.writeDatabase();
                }
            }
        } catch (IOException ignored) {
        } finally {
            try {
                this.bfr.close();
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
            this.pw.close();
        }
        System.out.println(this + " - Client Disconnected.");
    }

    public static void main(String[] args) {
        // Reading in database
        String userFile = "resources/userFile.txt"; // User Storage Path
        String chatFile = "resources/chatFile.txt"; // Chat Storage Path
        try {
            db = new Database(userFile, chatFile);
            System.out.println("Database Started");
            db.readUser();
            db.readChat();
        } catch (DatabaseReadException e) {
            System.out.println("DB: Read Failure");
            System.out.println(e.getMessage());
            db = new Database(userFile, chatFile);
        }

        printDatabaseUsersAndChats();

        try (ServerSocket serverSocket = new ServerSocket(4242)) { //  IP: "localhost" & Port: 4242
            while (true) {
                System.out.println("Waiting for a client to connect...");
                Socket socket = serverSocket.accept();
                System.out.println("Client connected!");
                Thread client = new Thread(new Server(socket));
                client.start();
                db.writeDatabase(); // writes database after every new client joins
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void printDatabaseUsersAndChats() {
        System.out.println("----------\nUSERS and CHATS in DATABASE:");
        for (User eachUser : db.getUsers()) {
            System.out.println(eachUser.getUsername());
            for (Chat eachChat : eachUser.getChats()) {
                // finding other username of the chats
                User[] chatUsers = eachChat.getUsers();
                String otherUser = chatUsers[0].getUsername();
                if (otherUser.equals(eachUser.getUsername())) {
                    otherUser = chatUsers[1].getUsername();
                }
                System.out.println("    " + eachUser.getUsername() + "–" + otherUser
                        + " (length: " + ((eachChat.getHistory() == null || eachChat.getHistory().isEmpty()) ? 0 : eachChat.getHistory().size()) + ")");
            }
        }
        System.out.println("----------");
    }
}