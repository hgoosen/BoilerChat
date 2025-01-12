import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A clientside program to connect to Server
 *
 * @author Henri Goosen (hgoosen), Benjamin Chen (chen5254), Samit Gadekar (SamitGadekar), Keshav Sreekantham (KeshavSree)
 * @version December 2, 2024
 */

public class Client implements ClientInterface, Runnable {

    // CUSTOMIZATIONS:
    private final boolean AUTO_UPDATE = true;
    private final int chatRefreshSpeed = 5000; // in milliseconds
    private final int msgRefreshSpeed = 1000; // in milliseconds
    private boolean doScalePhotoMessages = true; // in milliseconds

    // NETWORK I/O TOOLS
    private static Scanner scan; // Deprecated
    private static Socket socket;
    private static BufferedReader bfr; // For Text-Based Communication
    private static PrintWriter pw; // For Sending Plain Text Messages
    private static ObjectInputStream ois; // For Object-Based Communication
    private static ObjectOutputStream oos; // For Sending Serialized Objects

    // GUI COMPONENTS
    private JFrame mainWindow;
    private JPanel mainPanel;
    private CardLayout mainLayout;

    // Don't need anymore, added local variables inside method scopes
    // private static javax.swing.Timer chatPollingTimer; // for refreshing chats automatically
    // private static javax.swing.Timer messagePollingTimer; // for refreshing messaging automatically

    private JPanel logIn; // Intro Screen (Henri)
    private JPanel newUser;  // Register Screen | Log In Subset (Henri)
    private JPanel userHome;  // User Main Menu (Ben)
    private JPanel chat;  // Chat List (Samit)
    private JPanel messaging; // Messaging | Chat Subset (Samit)
    private JPanel search;  // Search Screen (Henri)
    private JPanel userProfile; // User Profile Screen | Search Subset (Henri)
    private JPanel settings; // Settings Page (Keshav)
    private JPanel friendsList; // Friends List | Settings Subset (Keshav)
    private JPanel blockedList; // Blocked List | Settings Subset (Keshav)


    private static String clientUsername;
    private String messagingTargetUsername = null;
    private static boolean exit = false;

    public static void main(String[] args) {
        System.out.println("Welcome To BoilerChat!");
        System.out.println("Connecting To Server...");


        try {
            scan = new Scanner(System.in);
            socket = new Socket(IP, PORT);

            // Dedicated Streams
            bfr = new BufferedReader(new InputStreamReader(socket.getInputStream())); // Text Input
            pw = new PrintWriter(socket.getOutputStream()); // Text Output
            oos = new ObjectOutputStream(socket.getOutputStream()); // Object Output
            ois = new ObjectInputStream(socket.getInputStream()); // Object Input
            System.out.println("Connected To Server!");

            SwingUtilities.invokeLater(new Client()); // Calling GUI Runnable

        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Error Connecting To Server!",
                    "BoilerChat", JOptionPane.ERROR_MESSAGE);
            System.out.println("Error Connecting To Server: " + e.getMessage());
        }
    }

    // GUI Thread
    @Override
    public void run() {
        // Creating Main Frame
        mainWindow = new JFrame("BoilerChat");
        mainWindow.setSize(640, 480);
        mainWindow.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        // Setting Up Card Layout For Panel Switching
        mainLayout = new CardLayout();
        mainPanel = new JPanel(mainLayout);

        // Creating panels using methods.
        logIn = createLogInGUI();
        newUser = createNewUserGUI();
        userHome = createUserHomeGUI();
        chat = createChatGUI();
        messaging = createMessagingGUI();
        search = createSearchGUI();
        settings = createSettingsGUI();
        friendsList = createFriendsListGUI();
        blockedList = createBlockedListGUI();

        // Adding pages to main window.

        mainPanel.add(logIn, "logIn");
        mainPanel.add(newUser, "newUser");
        mainPanel.add(userHome, "userHome");
        mainPanel.add(chat, "chat");
        mainPanel.add(messaging, "messaging");
        mainPanel.add(search, "search");
        mainPanel.add(settings, "settings");
        mainPanel.add(friendsList, "friendsList");
        mainPanel.add(blockedList, "blockedList");

        // making it so that closing any window stops the program safely
        mainWindow.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // O=override default close behavior
        mainWindow.addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                // show confirmation dialog before closing
                int confirm = JOptionPane.showConfirmDialog(
                        mainWindow,
                        "Are you sure you want to exit the application?",
                        "Exit Confirmation",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    System.out.println("Cleaning Up Connections...");
                    cleanup();
                    System.exit(0); // exit the application
                }
            }
        });

        mainWindow.add(mainPanel);
        mainWindow.setLocationRelativeTo(null);
        mainWindow.setVisible(true);
    }

    private JPanel createLogInGUI() {
        JPanel logIn = new JPanel();
        logIn.setLayout(new BoxLayout(logIn, BoxLayout.Y_AXIS)); // Centers components in a column.


        // Create UI Components Here
        JLabel title = new JLabel("Welcome To BoilerChat!");
        JButton newUser = new JButton("Register");
        JButton log = new JButton("Log In");

        JLabel username = new JLabel("Username:");
        JTextField userField = new JTextField(10);
        userField.setMaximumSize(new Dimension(200, 25));
        JLabel password = new JLabel("Password:");
        JPasswordField passField = new JPasswordField(10);
        passField.setMaximumSize(new Dimension(200, 25));

        // Centering Components
        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        newUser.setAlignmentX(Component.CENTER_ALIGNMENT);
        log.setAlignmentX(Component.CENTER_ALIGNMENT);

        username.setAlignmentX(Component.CENTER_ALIGNMENT);
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        password.setAlignmentX(Component.CENTER_ALIGNMENT);
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding components in correct places.
        logIn.add(title, BorderLayout.NORTH);
        logIn.add(Box.createRigidArea(new Dimension(0, 10)));

        logIn.add(username);
        logIn.add(userField);
        logIn.add(Box.createRigidArea(new Dimension(0, 10)));
        logIn.add(password);
        logIn.add(passField);
        logIn.add(Box.createRigidArea(new Dimension(0, 10)));

        logIn.add(log);
        logIn.add(Box.createRigidArea(new Dimension(0, 10))); // Add spacing between components.
        logIn.add(newUser, BorderLayout.SOUTH);
        logIn.add(Box.createVerticalGlue()); // Add vertical space at the bottom.

        userField.addActionListener(e -> {
            passField.requestFocusInWindow(); // allows you to click enter to type in password next
        });

        // common method to login
        Runnable runLogIn = () -> {
            String user = userField.getText();
            String pass = passField.getText();

            if (user.isBlank() || pass.isBlank()) {
                JOptionPane.showMessageDialog(null, "Please enter all information.",
                        "Log In", JOptionPane.INFORMATION_MESSAGE);
            } else {
                sendCommand("log_in", user, pass);

                boolean registered = getSuccess();
                System.out.println("Checking: " + user);
                if (registered) {
                    clientUsername = user; // setting clientUsername

                    // Resetting Text
                    userField.setText("");
                    passField.setText("");

                    mainLayout.show(mainPanel, "userHome");
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to log in.",
                            "Log In", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        log.addActionListener(e -> runLogIn.run());
        passField.addActionListener(e -> runLogIn.run());

        // Adding Action Button For Clicking "Register"
        newUser.addActionListener(e -> {
            userField.setText("");
            passField.setText("");
            mainLayout.show(mainPanel, "newUser");
        });

        return logIn;
    }

    /**
     * <p>GUI JPanel handling new users.</p>
     *
     * @return Completed JPanel with all components
     * @author Henri Goosen (hgoosen)
     */
    private JPanel createNewUserGUI() {
        JPanel register = new JPanel();
        // Centers components in a column.
        register.setLayout(new BoxLayout(register, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Create New User");

        // Creating text input fields.
        JLabel username = new JLabel("Username: (Alphanumeric, 4-16 Characters)");
        JTextField userField = new JTextField(10);
        userField.setMaximumSize(new Dimension(200, 25));
        JLabel password = new JLabel("Password: (Alphanumeric, 8-20 Characters)");
        JPasswordField passField = new JPasswordField(10);
        passField.setMaximumSize(new Dimension(200, 25));
        JLabel passConfirm = new JLabel("Confirm Password:");
        JPasswordField confirmField = new JPasswordField(10);
        confirmField.setMaximumSize(new Dimension(200, 25));

        // Buttons to submit and go back.
        JButton create = new JButton("Create User");
        JButton back = new JButton("Go Back");

        // Aligning components in the center.
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        username.setAlignmentX(Component.CENTER_ALIGNMENT);
        userField.setAlignmentX(Component.CENTER_ALIGNMENT);
        password.setAlignmentX(Component.CENTER_ALIGNMENT);
        passField.setAlignmentX(Component.CENTER_ALIGNMENT);
        passConfirm.setAlignmentX(Component.CENTER_ALIGNMENT);
        confirmField.setAlignmentX(Component.CENTER_ALIGNMENT);

        create.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);

        // Adding components.
        register.add(title, BorderLayout.NORTH);

        register.add(Box.createRigidArea(new Dimension(0, 10)));
        register.add(username);
        register.add(userField);

        register.add(Box.createRigidArea(new Dimension(0, 10)));
        register.add(password);
        register.add(passField);

        register.add(Box.createRigidArea(new Dimension(0, 10)));
        register.add(passConfirm);
        register.add(confirmField);

        register.add(Box.createRigidArea(new Dimension(0, 10)));
        register.add(create);
        register.add(Box.createRigidArea(new Dimension(0, 10)));
        register.add(back, BorderLayout.SOUTH);
        register.add(Box.createVerticalGlue()); // Add vertical space at the bottom.


        // common method to run create new user
        Runnable runCreate = () -> {
            String user = userField.getText();
            String pass = passField.getText();
            String confirm = confirmField.getText();

            if (user.isBlank() || pass.isBlank() || confirm.isBlank()) {
                JOptionPane.showMessageDialog(null, "Please enter all information.",
                        "Register User", JOptionPane.INFORMATION_MESSAGE);
            } else if (!confirm.equals(pass)) {
                JOptionPane.showMessageDialog(null, "Passwords do not match.",
                        "Register User", JOptionPane.INFORMATION_MESSAGE);
            } else {
                sendCommand("register", user, pass);

                boolean registered = getSuccess();

                if (registered) {
                    JOptionPane.showMessageDialog(null, "User successfully created!",
                            "Register User", JOptionPane.INFORMATION_MESSAGE);
                    // Resetting Text
                    userField.setText("");
                    passField.setText("");
                    confirmField.setText("");

                    back.doClick(); // Sends back to home.
                } else {
                    JOptionPane.showMessageDialog(null, "Failed to create new user.",
                            "Register User", JOptionPane.ERROR_MESSAGE);
                }
            }
        };

        // all button actions
        userField.addActionListener(e -> passField.requestFocusInWindow());
        passField.addActionListener(e -> confirmField.requestFocusInWindow());

        confirmField.addActionListener(e -> runCreate.run());
        create.addActionListener(e -> runCreate.run());

        back.addActionListener(e -> {
            // Resetting Text
            userField.setText("");
            passField.setText("");
            confirmField.setText("");
            mainLayout.show(mainPanel, "logIn");
        });

        return register;
    }

    /**
     * <p> Home Page </p>
     *
     * @return User home JPanel
     * @author Benjamin Chen (chen5254)
     */
    private JPanel createUserHomeGUI() {
        JPanel userHome = new JPanel();
        userHome.setLayout(new BoxLayout(userHome, BoxLayout.Y_AXIS));

        JLabel title = new JLabel("Home");
        JButton chat = new JButton("Chat");
        JButton search = new JButton("Search");
        JButton settings = new JButton("Settings");
        JButton logOut = new JButton("Log Out");

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        chat.setAlignmentX(Component.CENTER_ALIGNMENT);
        search.setAlignmentX(Component.CENTER_ALIGNMENT);
        settings.setAlignmentX(Component.CENTER_ALIGNMENT);
        logOut.setAlignmentX(Component.CENTER_ALIGNMENT);

        userHome.add(title, BorderLayout.NORTH);
        userHome.add(Box.createRigidArea(new Dimension(0, 10)));
        userHome.add(chat);
        userHome.add(Box.createRigidArea(new Dimension(0, 10)));
        userHome.add(search);
        userHome.add(Box.createRigidArea(new Dimension(0, 10)));
        userHome.add(settings);
        userHome.add(Box.createRigidArea(new Dimension(0, 10)));
        userHome.add(logOut);
        userHome.add(Box.createVerticalGlue());

        // Adding Action Button
        chat.addActionListener(e -> {
            // removing chat
            mainPanel.remove(this.chat);
            // making and adding again
            this.chat = createChatGUI();
            mainPanel.add(this.chat, "chat");
            // showing
            mainLayout.show(mainPanel, "chat");
        });
        search.addActionListener(e -> {
            mainLayout.show(mainPanel, "search");
        });
        settings.addActionListener(e -> {
            // removing settings
            mainPanel.remove(this.settings);
            // making and adding again
            this.settings = createSettingsGUI();
            mainPanel.add(this.settings, "settings");
            // showing
            mainLayout.show(mainPanel, "settings");
        });
        logOut.addActionListener(e -> {
            mainLayout.show(mainPanel, "logIn");
        });

        return userHome;
    }

    /**
     * <p> Chat List </p>
     *
     * @return Chat JPanel
     * @author Samit Gadekar (sgadekar)
     */
    private JPanel createChatGUI() {
        JPanel chat = new JPanel();
        chat.setLayout(new BorderLayout());

        // title
        JLabel title = new JLabel("Your Chats", SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));
        chat.add(title, BorderLayout.NORTH);

        // scrollable chat list
        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        scroll.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JScrollPane scrollPane = new JScrollPane(scroll);
        chat.add(scrollPane, BorderLayout.CENTER);

        // bottom panel
        JButton back = new JButton("Back");

        JButton startNewChat = new JButton("Start New Chat");
        JTextField targetUsernameField = new JTextField(10);

        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        bottomPanel.add(back);
        bottomPanel.add(startNewChat);
        bottomPanel.add(targetUsernameField);

        chat.add(bottomPanel, BorderLayout.SOUTH);
        Dimension minimumSize = new Dimension(100, 50);
        chat.setMinimumSize(minimumSize);

        // common method to message a new chat
        Runnable runStartNewChat = () -> {
            String readTargetUsername = targetUsernameField.getText().trim();

            if (!readTargetUsername.isEmpty()) {
                // checking if valid user
                sendCommand("is_user", readTargetUsername);
                boolean found = getSuccess();

                if (!found) {
                    JOptionPane.showMessageDialog(
                            chat,
                            "User not found!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else if (readTargetUsername.equals(clientUsername)) {
                    JOptionPane.showMessageDialog(
                            chat,
                            "You cannot chat with yourself!",
                            "Error",
                            JOptionPane.ERROR_MESSAGE
                    );
                } else {
                    System.out.println("Messaging Target Username Changed: " + messagingTargetUsername + " -> " + readTargetUsername);
                    messagingTargetUsername = readTargetUsername;
                    targetUsernameField.setText(""); // clear the field after creating the chat

                    // remove the old messaging panel
                    mainPanel.remove(messaging);

                    // adding the new messaging panel
                    messaging = createMessagingGUI();
                    mainPanel.add(messaging, "messaging");

                    mainLayout.show(mainPanel, "messaging");
                }
            } else {
                JOptionPane.showMessageDialog(
                        chat,
                        "Please enter a username!",
                        "Error",
                        JOptionPane.ERROR_MESSAGE
                );
            }
        };

        startNewChat.addActionListener(e -> runStartNewChat.run());
        targetUsernameField.addActionListener(e -> runStartNewChat.run());

        // back button
        back.addActionListener(e -> {
            mainLayout.show(mainPanel, "userHome");
        });

        // update the chat list dynamically
        javax.swing.Timer chatPollingTimer = new javax.swing.Timer(chatRefreshSpeed, e -> updateChatList(scroll));
        chatPollingTimer.setRepeats(true);

        chat.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                updateChatList(scroll); // Immediately update the chat list
                if (AUTO_UPDATE) {
                    chatPollingTimer.start(); // Start polling when the panel becomes visible
                    System.out.println("Chat panel is visible. Polling timer started.");
                }
            }

            @Override
            public void componentHidden(java.awt.event.ComponentEvent e) {
                chatPollingTimer.stop(); // Stop polling when the panel becomes hidden
                System.out.println("Chat panel is hidden. Polling timer stopped.");
            }
        });

        return chat;
    }

    /**
     * <p> Helper method to update chat list on page open </p>
     *
     * @author Samit Gadekar (sgadekar)
     */
    private void updateChatList(JPanel scroll) {
        scroll.removeAll(); // Clear any existing content.

        sendCommand("get_privacy_status");
        boolean clientIsPublic = getSuccess();
        JLabel privacyStatus = new JLabel("Status: " + (clientIsPublic ? "Public" : "Private"));
        privacyStatus.setFont(new Font("Arial", Font.ITALIC, 14));
        scroll.add(privacyStatus);

        sendCommand("get_chats_list", clientUsername);
        ArrayList<Chat> recievedChatsList = new ArrayList<>();

        // getting chats list from server
        try {
            Chat eachRecievedChat = (Chat) ois.readObject();
            while (eachRecievedChat != null) {
                recievedChatsList.add(eachRecievedChat);
                eachRecievedChat = (Chat) ois.readObject();
            }

            // need to remove debugging
            System.out.println("Received Chats ArrayList (" + recievedChatsList.size() + ") for: " + clientUsername);
            for (Chat chat : recievedChatsList) {
                System.out.println("    " + chat.getUsers()[0].getUsername() + "-" + chat.getUsers()[1].getUsername());
            }

            bfr.readLine(); // clear buffer

        } catch (IOException e) {
            System.out.println("Error: Could Not Receive Chat List " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: ClassNotFoundException " + e);
        }

        // filter the chats
        ArrayList<Chat> friendsChatsList = new ArrayList<>();
        ArrayList<Chat> otherChatsList = new ArrayList<>();
        ArrayList<Chat> blockedChatsList = new ArrayList<>();
        ArrayList<Chat> iAmBeingBlockedChatsList = new ArrayList<>();

        for (Chat eachChat : recievedChatsList) {
            User[] chatUsers = eachChat.getUsers();
            String otherUser = chatUsers[0].getUsername();
            if (otherUser.equals(clientUsername)) {
                otherUser = chatUsers[1].getUsername();
            }

            sendCommand("is_blocked", otherUser);
            boolean targetIsBlocked = getSuccess();
            sendCommand("am_i_blocked", clientUsername, messagingTargetUsername);
            boolean iAmBlocked = getSuccess();
            sendCommand("is_friend", otherUser);
            boolean targetIsFriend = getSuccess();
            if (targetIsBlocked) {
                blockedChatsList.add(eachChat);
            } else if (iAmBlocked) {
                iAmBeingBlockedChatsList.add(eachChat);
            } else if (targetIsFriend) {
                friendsChatsList.add(eachChat);
            } else {
                otherChatsList.add(eachChat);
            }
        }

        // Showing all chats and headers
        scroll.add(new JLabel(" "));
        scroll.add(new JLabel("Friends"));
        showChatsFromList(friendsChatsList, scroll);

        if (clientIsPublic) {
            scroll.add(new JLabel(" "));
            scroll.add(new JLabel("Other"));
            showChatsFromList(otherChatsList, scroll);

            /*
            scroll.add(new JLabel(" "));
            scroll.add(new JLabel("Blocked"));
            showChatsFromList(blockedChatsList, scroll);

            scroll.add(new JLabel(" "));
            scroll.add(new JLabel("Blocked Me"));
            showChatsFromList(iAmBeingBlockedChatsList, scroll);
             */
        }

        scroll.revalidate();
        scroll.repaint();
    }

    /**
     * <p> Helper method to add chat list on each page </p>
     *
     * @author Samit Gadekar (sgadekar)
     */
    private void showChatsFromList(ArrayList<Chat> inputChatsList, JPanel scroll) {
        if (inputChatsList == null || inputChatsList.isEmpty()) {
            JLabel label = new JLabel("You have no chats!");
            label.setFont(new Font("Arial", Font.ITALIC, 14));
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            scroll.add(label);
        } else {

            for (Chat eachChat : inputChatsList) {

                User[] chatUsers = eachChat.getUsers();
                String otherUser = chatUsers[0].getUsername();
                if (otherUser.equals(clientUsername)) {
                    otherUser = chatUsers[1].getUsername();
                }

                String finalOtherUser = otherUser; // final copy of username for use inside the lambda

                JButton chatButton = new JButton(otherUser); // use a button for clickable chat
                chatButton.setAlignmentX(Component.CENTER_ALIGNMENT);

                chatButton.addActionListener(e -> {
                    messagingTargetUsername = finalOtherUser;

                    // remove the old messaging panel
                    mainPanel.remove(messaging);

                    // adding the new messaging panel
                    messaging = createMessagingGUI();
                    mainPanel.add(messaging, "messaging");

                    mainLayout.show(mainPanel, "messaging");
                }); // open chat on click

                scroll.add(chatButton);
            }
        }
    }

    /**
     * <p> Messaging </p>
     *
     * @return Messaging JPanel
     * @author Samit Gadekar (sgadekar)
     */
    private JPanel createMessagingGUI() {
        JPanel messaging = new JPanel();
        messaging.setLayout(new BorderLayout());

        // title
        JPanel titlePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));

        ImageIcon myProfilePhoto = getProfilePhoto(messagingTargetUsername, 30);
        JLabel profilePhotoLabel;
        if (myProfilePhoto != null) {
            profilePhotoLabel = new JLabel(myProfilePhoto);
        } else {
            profilePhotoLabel = new JLabel("  ");
        }
        profilePhotoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        System.out.println("Messaging Target Username: " + messagingTargetUsername);
        JLabel title = new JLabel("Messaging: " + messagingTargetUsername, SwingConstants.CENTER);
        title.setFont(new Font("Arial", Font.BOLD, 16));


        titlePanel.add(profilePhotoLabel);
        titlePanel.add(Box.createRigidArea(new Dimension(5, 0)));
        titlePanel.add(title);
        messaging.add(titlePanel, BorderLayout.NORTH);

        // chat panel for messages
        JPanel chatPanel = new JPanel();
        chatPanel.setLayout(new BoxLayout(chatPanel, BoxLayout.Y_AXIS));

        // scroll
        JScrollPane scrollPane = new JScrollPane(chatPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding for aesthetics

        messaging.add(scrollPane, BorderLayout.CENTER);

        // input area
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.X_AXIS));

        JButton backButton = new JButton("Back");
        JTextField messageField = new JTextField();
        messageField.setMaximumSize(new Dimension(Short.MAX_VALUE, 30)); // expandable width with fixed height
        JButton sendButton = new JButton("Send");
        JButton uploadButton = new JButton("Upload");
        JButton selectMessagesButton = new JButton("Set");

        // spacings
        inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        inputPanel.add(backButton);
        inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        inputPanel.add(messageField);
        inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        inputPanel.add(sendButton);
        inputPanel.add(Box.createRigidArea(new Dimension(5, 0)));
        inputPanel.add(uploadButton);
        inputPanel.add(Box.createRigidArea(new Dimension(15, 0)));
        inputPanel.add(selectMessagesButton);
        inputPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding around the input area

        messaging.add(inputPanel, BorderLayout.SOUTH);

        // needs to be array so that can pass into display history helper method and update here as well
        int[] msgHistSize = new int[]{0};

        // update the message list dynamically
        javax.swing.Timer messagePollingTimer;

        if (messagingTargetUsername != null) {
            // initial loading message history
            updateMessageHistory(chatPanel, scrollPane, msgHistSize);
            // scroll to the bottom
            SwingUtilities.invokeLater(() ->
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum())
            );

            // polling timer for updating history every -variable- milliseconds
            messagePollingTimer = new javax.swing.Timer(msgRefreshSpeed, e -> updateMessageHistory(chatPanel, scrollPane, msgHistSize));
            messagePollingTimer.setRepeats(true);

            // attach a listener to handle visibility changes
            messaging.addHierarchyListener(e -> {
                if ((e.getChangeFlags() & HierarchyEvent.SHOWING_CHANGED) != 0) {
                    if (AUTO_UPDATE && messaging.isShowing()) {
                        messagePollingTimer.start(); // start timer when panel is visible
                        System.out.println("Messaging panel shown. Polling timer started.");
                    } else {
                        messagePollingTimer.stop(); // stop timer when panel is hidden
                        System.out.println("Messaging panel hidden. Polling timer stopped.");
                    }
                }
            });
        }

        // common method to send a message
        Runnable sendMessage = () -> {
            String messageText = messageField.getText().trim();

            sendCommand("am_i_blocked", clientUsername, messagingTargetUsername);
            boolean iAmBlocked = getSuccess();

            if (iAmBlocked) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            null, messagingTargetUsername + " blocked you.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            } else if (!messageText.isEmpty()) {
                // sending message to database
                sendCommand("send_message", clientUsername, messagingTargetUsername, messageText);

                boolean status = getSuccess();
                // error in sending the message
                if (!status) {
                    System.out.println("Error: Message Send Failure");
                    // show error pop-up
                    SwingUtilities.invokeLater(() -> {
                        JOptionPane.showMessageDialog(
                                null, "Failed to send the message. Please try again.",
                                "Error", JOptionPane.ERROR_MESSAGE);
                    });
                }
                updateMessageHistory(chatPanel, scrollPane, msgHistSize);

                // scroll to the bottom
                SwingUtilities.invokeLater(() ->
                        scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum())
                );

                // clear input field
                messageField.setText("");

            }
        };

        // back button
        backButton.addActionListener(e -> mainLayout.show(mainPanel, "chat"));

        // send message with send button
        sendButton.addActionListener(e -> sendMessage.run());

        // send message with enter key
        messageField.addActionListener(e -> sendMessage.run());

        // select messages to be deleted
        selectMessagesButton.addActionListener(e -> {
            selectMessagesGUI();
            updateMessageHistory(chatPanel, scrollPane, new int[]{0});
        });

        // uploading a PhotoMessage
        uploadButton.addActionListener(e -> {
            String messageText = messageField.getText().trim();

            sendCommand("am_i_blocked", clientUsername, messagingTargetUsername);
            boolean iAmBlocked = getSuccess();

            if (iAmBlocked) {
                SwingUtilities.invokeLater(() -> {
                    JOptionPane.showMessageDialog(
                            null, messagingTargetUsername + " blocked you.",
                            "Error", JOptionPane.ERROR_MESSAGE);
                });
            } else {
                JFileChooser fileChooser = new JFileChooser();
                int result = fileChooser.showOpenDialog(mainWindow); // Show the file chooser

                if (result == JFileChooser.APPROVE_OPTION) {
                    File selectedFile = fileChooser.getSelectedFile();
                    System.out.println("Selected File: " + selectedFile.getAbsolutePath());
                    try {
                        BufferedImage image = ImageIO.read(selectedFile);

                        if (image == null) {
                            throw new IOException("Image Read Error");
                        }

                        System.out.println("Image Loaded Successfully.");

                        // sending message to database
                        sendCommand("send_photo_message", clientUsername, messagingTargetUsername);
                        // send photo object
                        oos.writeObject(new ImageIcon(image));
                        oos.flush();

                        boolean status = getSuccess();
                        // error in sending the message
                        if (!status) {
                            System.out.println("Error: Message Send Failure");
                            // show error pop-up
                            SwingUtilities.invokeLater(() -> {
                                JOptionPane.showMessageDialog(
                                        null, "Failed to send the message. Please try again.",
                                        "Error", JOptionPane.ERROR_MESSAGE);
                            });
                        }
                        updateMessageHistory(chatPanel, scrollPane, msgHistSize);

                        // scroll to the bottom
                        SwingUtilities.invokeLater(() ->
                                scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum())
                        );
                    } catch (Exception er) {
                        JOptionPane.showMessageDialog(null, "Error reading photo: "
                                + er.getMessage(), "Error Reading Photo", JOptionPane.ERROR_MESSAGE);
                    }

                } else if (result == JFileChooser.CANCEL_OPTION) {
                    System.out.println("File selection canceled.");
                }
            }
        });

        return messaging;
    }

    /**
     * <p> Helper method to update the message history </p>
     *
     * @author Samit Gadekar (sgadekar)
     */
    private void updateMessageHistory(JPanel chatPanel, JScrollPane scrollPane, int[] msgHistSize) {
        // get message history
        ArrayList<Message> messageHistory = getMessageHistory(messagingTargetUsername);
        getSuccess(); // clear buffer

        int oldMsgHistSize = msgHistSize[0];
        int newMsgHistSize = messageHistory.size();
        int photoSize = 200;

        if (messageHistory != null && !messageHistory.isEmpty() && oldMsgHistSize != newMsgHistSize) {
            chatPanel.removeAll(); // clear previous messages

            for (Message msg : messageHistory) {
                // add the message to the chat panel
                JPanel messageWrapper = new JPanel(new FlowLayout(
                        msg.getSender().equals(clientUsername) ? FlowLayout.RIGHT : FlowLayout.LEFT)); // align message based on sender
                messageWrapper.setOpaque(false); // transparent background

                JLabel messageLabel = null;
                if (msg instanceof PhotoMessage) {
                    ImageIcon sentPhoto = ((PhotoMessage) msg).getPhoto();
                    JLabel sentPhotoLabel;
                    if (sentPhoto != null) {
                        if (doScalePhotoMessages) {
                            sentPhoto = new ImageIcon(
                                    ((PhotoMessage) msg).getPhoto().getImage().getScaledInstance(photoSize, photoSize, Image.SCALE_SMOOTH)
                            );
                        }
                        sentPhotoLabel = new JLabel(sentPhoto);
                    } else {
                        sentPhotoLabel = new JLabel("(Attached photo not found)");
                    }
                    messageLabel = sentPhotoLabel;
                } else {
                    // formatting of message
                    String messageText = (msg.getSender().equals(clientUsername) ? "" : "(" + msg.getTimeStamp() + ") ")
                            + msg.getText()
                            + (msg.getSender().equals(clientUsername) ? " (" + msg.getTimeStamp() + ")" : "");
                    messageLabel = new JLabel(messageText);
                }

                messageLabel.setOpaque(true);
                messageLabel.setBackground(msg.getSender().equals(clientUsername) ? Color.CYAN : Color.LIGHT_GRAY); // color based on sender
                if (msg instanceof PhotoMessage) {
                    messageLabel.setOpaque(false);
                }
                messageLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // padding inside the bubble

                messageWrapper.add(messageLabel);
                chatPanel.add(messageWrapper);
            }
        } else if (messageHistory == null || messageHistory.isEmpty()) {
            chatPanel.removeAll(); // clear previous messages

            // label for no messages
            JLabel emptyLabel = new JLabel("No messages in this chat!");
            emptyLabel.setFont(new Font("Arial", Font.ITALIC, 14));
            emptyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
            chatPanel.add(emptyLabel);
        }

        // Refresh the chat panel
        chatPanel.revalidate();
        chatPanel.repaint();

        // scrolling down if a new message appears
        if (oldMsgHistSize != newMsgHistSize) {
            msgHistSize[0] = newMsgHistSize;
            // scroll to the bottom
            SwingUtilities.invokeLater(() ->
                    scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum())
            );
        }
    }

    /**
     * <p> Helper method to select and manipulate the messages </p>
     *
     * @author Samit Gadekar (sgadekar)
     */
    private void selectMessagesGUI() {
        // window for selecting messages
        JDialog deleteDialog = new JDialog((JFrame) null, "Delete Messages", true);
        deleteDialog.setLayout(new BorderLayout());
        deleteDialog.setSize(600, 300);
        deleteDialog.setLocationRelativeTo(null); // Center the dialog

        // get message history
        ArrayList<Message> messageHistory = getMessageHistory(messagingTargetUsername);
        getSuccess(); // Clear buffer

        if (messageHistory == null || messageHistory.isEmpty()) {
            JOptionPane.showMessageDialog(deleteDialog,
                    "No messages available for selection.",
                    "Info",
                    JOptionPane.INFORMATION_MESSAGE);
            return;
        }

        // panel to show checkboxes
        JPanel messagePanel = new JPanel();
        messagePanel.setLayout(new BoxLayout(messagePanel, BoxLayout.Y_AXIS));

        // array to hold checkboxes for each message
        ArrayList<JCheckBox> messageCheckBoxes = new ArrayList<>();

        // Populate the panel with checkboxes for each message
        for (Message msg : messageHistory) {
            String displayText = "(" + msg.getTimeStamp() + ") " + msg.getText();
            if (msg instanceof PhotoMessage) {
                displayText += "(attached photo)";
            }
            JCheckBox checkBox = new JCheckBox(displayText);
            messageCheckBoxes.add(checkBox);
            if (msg.getSender().equals(clientUsername)) {
                messagePanel.add(checkBox);
            }
        }

        // add the panel to a scroll pane in case of many messages
        JScrollPane scrollPane = new JScrollPane(messagePanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        deleteDialog.add(scrollPane, BorderLayout.CENTER);

        // add buttons to the dialog
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));

        JButton cancelButton = new JButton("Cancel");
        JButton photoScalingButton = new JButton(doScalePhotoMessages ? "Photos are scaled" : "Photos are not scaled");
        JButton deleteAllButton = new JButton("Delete ALL Your Messages");
        JButton deleteButton = new JButton("Delete");

        buttonPanel.add(cancelButton);
        buttonPanel.add(photoScalingButton);
        buttonPanel.add(Box.createRigidArea(new Dimension(10, 0)));
        buttonPanel.add(deleteAllButton);
        buttonPanel.add(deleteButton);
        deleteDialog.add(buttonPanel, BorderLayout.SOUTH);

        // cancel button functionality
        cancelButton.addActionListener(e -> deleteDialog.dispose());

        // photo scaling button functionality
        photoScalingButton.addActionListener(e -> {
            doScalePhotoMessages = !doScalePhotoMessages;
            photoScalingButton.setText(doScalePhotoMessages ? "Scaled Photos" : "Original Photos");
        });

        // delete ALL button functionality
        deleteAllButton.addActionListener(e -> {
            System.out.println("Delete All button clicked");

            // get all message
            ArrayList<Message> deleteAllMessageHistory = getMessageHistory(messagingTargetUsername);
            getSuccess(); // Clear the buffer

            if (deleteAllMessageHistory != null && !deleteAllMessageHistory.isEmpty()) {
                // show confirmation
                int confirm = JOptionPane.showConfirmDialog(
                        deleteDialog,
                        "Are you sure you want to delete all your messages?",
                        "Confirm Delete All",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    // generate a comma-separated string of all indexes
                    String allIndexes = "";
                    for (int i = 0; i < deleteAllMessageHistory.size(); i++) {
                        if (deleteAllMessageHistory.get(i).getSender().equals(clientUsername)) {
                            allIndexes += i + ",";
                        }
                    }
                    if (!allIndexes.isEmpty()) {
                        allIndexes = allIndexes.substring(0, allIndexes.length() - 1); // removes trailing comma
                    } else {
                        allIndexes = "-1";
                    }

                    // send the command to delete all messages
                    sendCommand("delete_message", clientUsername, messagingTargetUsername, allIndexes);

                    boolean status = getSuccess();
                    if (!status) {
                        JOptionPane.showMessageDialog(
                                null,
                                "Failed to delete messages. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE
                        );
                    } else {
                        if (allIndexes.equals("-1")) {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "You do not have any messages to delete.",
                                    "Error",
                                    JOptionPane.ERROR_MESSAGE
                            );
                        } else {
                            JOptionPane.showMessageDialog(
                                    null,
                                    "All messages have been successfully deleted.",
                                    "Success",
                                    JOptionPane.INFORMATION_MESSAGE
                            );
                            updateMessageHistory(new JPanel(), new JScrollPane(), new int[]{0}); // update messages panel
                        }

                        // close the dialog after successful deletion
                        deleteDialog.dispose();
                    }
                } else {
                    JOptionPane.showMessageDialog(
                            null,
                            "No messages to delete.",
                            "Information",
                            JOptionPane.INFORMATION_MESSAGE
                    );
                }
            } else {
                System.out.println("Delete All operation canceled by user.");
            }
        });

        // delete button functionality
        deleteButton.addActionListener(e -> {
            System.out.println("Delete button clicked");

            // Gather selected indexes
            ArrayList<Integer> selectedIndexes = new ArrayList<>();
            for (int i = 0; i < messageCheckBoxes.size(); i++) {
                if (messageCheckBoxes.get(i).isSelected()) {
                    selectedIndexes.add(i);
                }
            }

            // Send delete command if there are selected messages
            if (!selectedIndexes.isEmpty()) {
                // show confirmation
                int confirm = JOptionPane.showConfirmDialog(
                        deleteDialog,
                        "Are you sure you want to delete the selected messages?",
                        "Confirm Delete",
                        JOptionPane.YES_NO_OPTION,
                        JOptionPane.WARNING_MESSAGE);

                if (confirm == JOptionPane.YES_OPTION) {
                    String indexesToDelete = selectedIndexes.stream()
                            .map(String::valueOf)
                            .collect(Collectors.joining(","));
                    sendCommand("delete_message", clientUsername, messagingTargetUsername, indexesToDelete);

                    boolean status = getSuccess();
                    if (status) {
                        JOptionPane.showMessageDialog(deleteDialog,
                                "Messages deleted successfully.",
                                "Success",
                                JOptionPane.INFORMATION_MESSAGE);
                        deleteDialog.dispose(); // Close the dialog
                        updateMessageHistory(new JPanel(), new JScrollPane(), new int[]{0}); // update messages panel
                    } else {
                        JOptionPane.showMessageDialog(deleteDialog,
                                "Failed to delete messages. Please try again.",
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } else {
                    System.out.println("Delete operation canceled by user.");
                }
            } else {
                JOptionPane.showMessageDialog(deleteDialog,
                        "No messages selected for deletion.",
                        "Info",
                        JOptionPane.INFORMATION_MESSAGE);
            }
        });

        deleteDialog.setVisible(true);
    }

    /**
     * <p> Helper method to get messages history </p>
     *
     * @author Samit Gadekar (sgadekar)
     */
    private static ArrayList<Message> getMessageHistory(String targetUsername) {
        ArrayList<Message> msgHistory = new ArrayList<>();
        try { // Sending the usernames to get the history.

            sendCommand("get_message_history", clientUsername, targetUsername);

            Object obj = ois.readObject();
            while (obj != null) {
                Message msg = (Message) obj;
                msgHistory.add(msg);
                obj = ois.readObject();
            }
            return msgHistory;
        } catch (IOException | ClassNotFoundException e) {
            System.out.println(e.getMessage());
        }
        return new ArrayList<>();
    }


    /**
     * <p> Settings Page </p>
     *
     * @return Settings JPanel
     * @author Keshav Sreekantham (KeshavSree)
     */
    private JPanel createSettingsGUI() {
        JPanel settings = new JPanel();

        JLabel title = new JLabel("Settings");

        // Centers components in a column.
        settings.setLayout(new BoxLayout(settings, BoxLayout.Y_AXIS));

        ImageIcon myProfilePhoto = getProfilePhoto(clientUsername, 150);
        JLabel profilePhotoLabel;
        if (myProfilePhoto != null) {
            profilePhotoLabel = new JLabel(myProfilePhoto);
        } else {
            profilePhotoLabel = new JLabel("No Profile Image.");
        }
        profilePhotoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);

        JButton friendsList = new JButton("Friends");
        JButton blockedList = new JButton("Blocked");

        JButton selectProfilePhoto = new JButton("Choose Profile Photo");


        final boolean[] isPrivate = {false};
        JLabel privacyLabel = new JLabel("Your Privacy Setting:");
        JButton privacy = new JButton("ERROR");

        JButton back = new JButton("Back");

        Component padding = Box.createRigidArea(new Dimension(0, 10));
        Component topPadding = Box.createRigidArea(new Dimension(0, 100));

        title.setAlignmentX(Component.CENTER_ALIGNMENT);
        friendsList.setAlignmentX(Component.CENTER_ALIGNMENT);
        blockedList.setAlignmentX(Component.CENTER_ALIGNMENT);
        selectProfilePhoto.setAlignmentX(Component.CENTER_ALIGNMENT);
        privacyLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        privacy.setAlignmentX(Component.CENTER_ALIGNMENT);
        back.setAlignmentX(Component.CENTER_ALIGNMENT);

        settings.add(title);
        settings.add(profilePhotoLabel);
        settings.add(friendsList);
        settings.add(Box.createRigidArea(new Dimension(0, 10)));
        settings.add(blockedList);
        settings.add(Box.createRigidArea(new Dimension(0, 10)));
        settings.add(selectProfilePhoto);
        settings.add(Box.createRigidArea(new Dimension(0, 40)));
        settings.add(privacyLabel);
        settings.add(Box.createRigidArea(new Dimension(0, 10)));
        settings.add(privacy);
        settings.add(Box.createRigidArea(new Dimension(0, 10)));
        settings.add(back);


        friendsList.addActionListener(e -> {
            mainLayout.show(mainPanel, "friendsList");
        });
        blockedList.addActionListener(e -> {
            mainLayout.show(mainPanel, "blockedList");
        });
        selectProfilePhoto.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            int result = fileChooser.showOpenDialog(mainWindow); // Show the file chooser

            if (result == JFileChooser.APPROVE_OPTION) {
                File selectedFile = fileChooser.getSelectedFile();
                System.out.println("Selected File: " + selectedFile.getAbsolutePath());
                setProfilePhoto(selectedFile);
            } else if (result == JFileChooser.CANCEL_OPTION) {
                System.out.println("File selection canceled.");
            }
        });

        privacy.addActionListener(e -> {
            boolean newPrivacyStatus = !isPrivate[0];
            sendCommand("set_privacy_status", String.valueOf(newPrivacyStatus));
            if (getSuccess()) {
                isPrivate[0] = newPrivacyStatus;
                privacy.setText(isPrivate[0] ? "Everyone" : "Friends Only");
            } else {
                System.out.println("Failed to update privacy status on the server.");
            }
        });

        settings.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                sendCommand("get_privacy_status");
                try {
                    isPrivate[0] = Boolean.parseBoolean(bfr.readLine());
                    privacy.setText(isPrivate[0] ? "Everyone" : "Friends Only");
                } catch (IOException ioe) {
                    System.out.println("Failed to get privacy status.");
                }
            }
        });

        back.addActionListener(e -> {
            mainLayout.show(mainPanel, "userHome");
        });


        return settings;
    }

    /**
     * <p> Friends Page </p>
     *
     * @return Friends JPanel
     * @author Keshav Sreekantham (KeshavSree)
     */
    private JPanel createFriendsListGUI() {
        JPanel friends = new JPanel();
        friends.setLayout(new BorderLayout());

        JLabel title = new JLabel("Friends");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton back = new JButton("Back");

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(scroll);

        friends.add(title, BorderLayout.NORTH);
        friends.add(scrollPane, BorderLayout.CENTER);
        friends.add(back, BorderLayout.SOUTH);

        back.addActionListener(e -> {
            mainLayout.show(mainPanel, "settings");
        });

        friends.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                updateFriendsList(scroll); // Update the friends list dynamically.
            }
        });

        return friends;
    }

    /**
     * <p> Helper method to update friends list on page open </p>
     *
     * @author Keshav Sreekantham (KeshavSree)
     */
    private void updateFriendsList(JPanel scroll) {
        scroll.removeAll(); // Clear any existing content.
        sendCommand("get_friends_list");
        ArrayList<String> friendsList = null;

        try {
            Object receivedObject = ois.readObject();

            if (receivedObject instanceof ArrayList<?> receivedList) {
                boolean allUsers = receivedList.stream().allMatch(item -> item instanceof String);
                if (allUsers) {
                    friendsList = (ArrayList<String>) receivedList;
                } else {
                    System.out.println("ArrayList Does Not Contain Only Strings.");
                }
            } else {
                System.out.println("Not An ArrayList.");
            }

            bfr.readLine(); // Clear Buffer
        } catch (IOException e) {
            System.out.println("Error: Could Not Receive Friends List " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: ClassNotFoundException " + e);
        }

        if (friendsList == null || friendsList.isEmpty()) {
            JLabel label = new JLabel("You have no friends!");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            scroll.add(label);
        } else {
            for (String str : friendsList) {
                JLabel label = new JLabel(str);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                scroll.add(label);
            }
        }

        scroll.revalidate(); // Refreshes the scroll panel.
        scroll.repaint();
    } //helper for createFriendsListGUI()

    /**
     * <p> Blocked Page </p>
     *
     * @return Blocked JPanel
     * @author Keshav Sreekantham (KeshavSree)
     */
    private JPanel createBlockedListGUI() {
        JPanel blocked = new JPanel();
        blocked.setLayout(new BorderLayout());

        JLabel title = new JLabel("Blocked");
        title.setHorizontalAlignment(SwingConstants.CENTER);

        JButton back = new JButton("Back");

        JPanel scroll = new JPanel();
        scroll.setLayout(new BoxLayout(scroll, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(scroll);

        blocked.add(title, BorderLayout.NORTH);
        blocked.add(scrollPane, BorderLayout.CENTER);
        blocked.add(back, BorderLayout.SOUTH);

        back.addActionListener(e -> {
            mainLayout.show(mainPanel, "settings");
        });

        blocked.addComponentListener(new java.awt.event.ComponentAdapter() {
            @Override
            public void componentShown(java.awt.event.ComponentEvent e) {
                updateBlockedList(scroll); // Update the blocked list dynamically.
            }
        });

        return blocked;
    }

    /**
     * <p> Helper method to update blocked list on page open </p>
     *
     * @author Keshav Sreekantham (KeshavSree)
     */
    private void updateBlockedList(JPanel scroll) {
        scroll.removeAll(); // Clear any existing content.
        sendCommand("get_blocked_list");
        ArrayList<String> blockedList = null;

        try {
            Object receivedObject = ois.readObject();

            if (receivedObject instanceof ArrayList<?> receivedList) {
                boolean allUsers = receivedList.stream().allMatch(item -> item instanceof String);
                if (allUsers) {
                    blockedList = (ArrayList<String>) receivedList;
                } else {
                    System.out.println("ArrayList Does Not Contain Only Strings.");
                }
            } else {
                System.out.println("Not An ArrayList.");
            }

            bfr.readLine(); // Clear buffer
        } catch (IOException e) {
            System.out.println("Error: Could Not Receive Blocked List " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: ClassNotFoundException " + e);
        }

        if (blockedList == null || blockedList.isEmpty()) {
            JLabel label = new JLabel("You have not blocked anyone!");
            label.setAlignmentX(Component.CENTER_ALIGNMENT);
            scroll.add(label);
        } else {
            for (String str : blockedList) {
                JLabel label = new JLabel(str);
                label.setAlignmentX(Component.CENTER_ALIGNMENT);
                scroll.add(label);
            }
        }

        scroll.revalidate(); // Refresh the scroll panel.
        scroll.repaint();
    } //helper for createBlockedListGUI()

    /**
     * <p> GUI JPanel handling the search feature </p>
     *
     * @return Completed JPanel with all components
     * @author Henri Goosen (hgoosen)
     */
    private JPanel createSearchGUI() {
        JPanel searchPage = new JPanel();
        // Centering Elements
        searchPage.setLayout(new BoxLayout(searchPage, BoxLayout.Y_AXIS));

        JPanel searchBar = new JPanel();
        JButton back = new JButton("Back");
        JTextField searchField = new JTextField(10);
        JButton searchButton = new JButton("Search");
        searchBar.add(back, BorderLayout.WEST);
        searchBar.add(searchField, BorderLayout.WEST);
        searchBar.add(searchButton, BorderLayout.EAST);
        searchBar.setMaximumSize(new Dimension(600, 40));

        JPanel resultPanel = new JPanel();
        resultPanel.setLayout(new BoxLayout(resultPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultPanel);
        scrollPane.setMaximumSize(new Dimension(250, 350));

        searchBar.setAlignmentX(Component.CENTER_ALIGNMENT);
        scrollPane.setAlignmentX(Component.CENTER_ALIGNMENT);

        searchPage.add(searchBar);
        searchPage.add(Box.createRigidArea(new Dimension(0, 10)));
        searchPage.add(scrollPane);

        // Back Button Function
        back.addActionListener(e -> {
            searchField.setText("");
            resultPanel.removeAll(); // Clears Results
            mainLayout.show(mainPanel, "userHome");
        });

        // common method to run search
        Runnable runSearch = () -> {
            resultPanel.removeAll(); // Removing Buttons & Search Results

            String term = searchField.getText();
            ArrayList<String> searchResults = new ArrayList<>();

            sendCommand("search_user", term);

            try { // Receiving Search Results
                String line = bfr.readLine();
                while (!line.equals("--STOP--")) {
                    searchResults.add(line);
                    line = bfr.readLine();
                }
            } catch (IOException er) {
                throw new RuntimeException(er);
            }

            getSuccess(); // Clear buffer.

            if (searchResults.isEmpty()) {
                resultPanel.removeAll();
                JLabel noResults = new JLabel("No results found!");
                noResults.setFont(new Font("Arial", Font.ITALIC, 14));
                noResults.setAlignmentX(Component.CENTER_ALIGNMENT);
                resultPanel.add(noResults);
                resultPanel.revalidate();
                resultPanel.repaint();
            } else {
                for (String s : searchResults) {
                    JButton userButton = new JButton(s);
                    // Keeping buttons uniform size.
                    userButton.setMaximumSize(
                            new Dimension(200, 20));
                    userButton.setAlignmentX(Component.CENTER_ALIGNMENT);
                    resultPanel.add(userButton);
                    userButton.addActionListener(event -> {
                        userProfile = createUserProfileGUI(s);
                        mainPanel.add(userProfile, "userProfile");
                        mainLayout.show(mainPanel, "userProfile");
                        System.out.printf("%s has been clicked!\n", s);
                    });
                }
                // Refreshes UI To Show Buttons
                resultPanel.revalidate(); // Refresh the scroll panel.
                resultPanel.repaint();
            }

        };

        // Search Button Function
        searchButton.addActionListener(e -> runSearch.run());
        searchField.addActionListener(e -> runSearch.run());

        return searchPage;
    }

    /**
     * <p> Creates User Profile Panel from username </p>
     *
     * @return JPanel userProfile
     * @author Henri Goosen (hgoosen), Benjamin Chen (chen5254), Samit Gadekar (sgadekar)
     */
    private JPanel createUserProfileGUI(String user) {
        JPanel userProfile = new JPanel();
        userProfile.setLayout(new BoxLayout(userProfile, BoxLayout.Y_AXIS));

        JPanel topBar = new JPanel(new FlowLayout()); // For back button
        topBar.setMaximumSize(new Dimension(600, 40));
        JPanel userInfo = new JPanel(); // For user info
        JPanel userActions = new JPanel(); // For actions relating to the user

        // Creating back button.
        JButton back = new JButton("Back");
        topBar.add(back, BorderLayout.WEST);

        //  Adding user info
        userInfo.setLayout(new BoxLayout(userInfo, BoxLayout.Y_AXIS));

        JLabel username = new JLabel(String.format("<html><h2>%s</h2></html>", user));
        userInfo.add(username);

        ImageIcon profilePhoto = getProfilePhoto(user, 50);
        JLabel profilePhotoLabel;
        if (profilePhoto != null) {
            profilePhotoLabel = new JLabel(profilePhoto);
        } else {
            profilePhotoLabel = new JLabel("No Profile Image.");
        }
        profilePhotoLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        userInfo.add(profilePhotoLabel);


        sendCommand("get_privacy_status", user);
        boolean isPublic = getSuccess();
        userInfo.add(Box.createRigidArea(new Dimension(0, 10)));
        userInfo.add(new JLabel("Status: " + (isPublic ? "Public" : "Private")));

        userInfo.add(Box.createVerticalGlue());

        // Adding user actions

        JToggleButton friend = new JToggleButton("Friend");
        JToggleButton block = new JToggleButton("Block");

        sendCommand("is_friend", user);
        boolean friends = getSuccess();

        sendCommand("is_blocked", user);
        boolean blocked = getSuccess();

        // User action buttons.
        friend.setSelected(friends);
        friend.setText(friends ? "Unfriend" : "Friend");
        friend.addActionListener(e -> {  // This is the basic toggle button.
            AbstractButton eventButton = (AbstractButton) e.getSource();
            String action = (eventButton.isSelected()) ? "add_friend" : "remove_friend";
            sendCommand(action, user);
            getSuccess(); // Clearing buffer.
            String value = (eventButton.isSelected()) ? "Unfriend" : "Friend";
            eventButton.setText(value);
        });

        block.setSelected(blocked);
        if (blocked) {
            friend.setEnabled(false);
        }
        block.setText(blocked ? "Unblock" : "Block");
        block.addActionListener(e -> {
            AbstractButton eventButton = (AbstractButton) e.getSource();
            if (eventButton.isSelected()) {
                int confirm = JOptionPane.showConfirmDialog(null,
                        String.format("Are you sure you'd like to block %s?", user),
                        "Confirm Block", JOptionPane.YES_NO_OPTION,
                        JOptionPane.QUESTION_MESSAGE);
                if (confirm == JOptionPane.NO_OPTION) {
                    eventButton.setSelected(false);
                    return; // Cancel
                }
            }
            String action = (eventButton.isSelected()) ? "block_user" : "unblock_user";
            sendCommand(action, user);
            getSuccess();
            String value = (eventButton.isSelected()) ? "Unblock" : "Block";
            eventButton.setText(value);

            // Update UI to reflect removing friend.
            if (friend.isSelected()) {
                friend.setSelected(false);
                friend.setText("Friend");
            }
            friend.setEnabled(!eventButton.isSelected()); // When blocked, disables friend button.

        });
        JButton message = new JButton("Message"); // Open messaging screen.

        userActions.add(friend);
        userActions.add(block);
/*
        userActions.add(message);

        // Adding functionality to buttons.
        message.addActionListener(e -> {
            mainPanel.remove(userProfile);
            messagingTargetUsername = user;

            // remove the old messaging panel
            mainPanel.remove(messaging);

            // adding the new messaging panel
            messaging = createMessagingGUI();
            mainPanel.add(messaging, "messaging");

            mainLayout.show(mainPanel, "messaging");
        });
*/
        back.addActionListener(e -> {
            mainPanel.remove(userProfile);
            mainLayout.show(mainPanel, "search");
        });

        userProfile.add(topBar);
        userProfile.add(userInfo);
        userProfile.add(userActions);

        return userProfile;
    }

    /**
     * <p> Helper method to set profile photo of client user </p>
     *
     * @author Keshav Sreekantham (KeshavSree)
     */
    private static void setProfilePhoto(File file) {
        try {
            BufferedImage image = ImageIO.read(file);

            if (image == null) {
                throw new IOException("Image Read Error");
            }

            System.out.println("Image Loaded Successfully.");

            sendCommand("set_profile_photo");
            oos.writeObject(new ImageIcon(image));
            oos.flush();

            if (getSuccess()) {
                System.out.println("Profile Photo Set");
            } else {
                System.out.println("Invalid Photo.");
            }
        } catch (Exception e) {
            JOptionPane.showMessageDialog(null, "Error reading photo: "
                    + e.getMessage(), "Error Reading Photo", JOptionPane.ERROR_MESSAGE);
        }
    }

    /**
     * <p> Helper method to receive profile photo from server using username </p>
     *
     * @return ImageIcon profilePhoto
     * @author Keshav Sreekantham (KeshavSree)
     */
    private static ImageIcon getProfilePhoto(String user, int size) {
        // READING Profile Photo
        sendCommand("get_profile_photo", user); // Activates Server OOS
        ImageIcon photo = null;
        try {
            photo = (ImageIcon) ois.readObject();
            getSuccess(); // clear Buffer
            if (photo == null) {
                return null;
            }
            // auto crop to 50 x 50
            photo = new ImageIcon(
                    photo.getImage().getScaledInstance(
                            size, size, Image.SCALE_SMOOTH));
        } catch (Exception e) {
            System.out.println("Error Loading Image.");
        }
        return photo;
    }

    /**
     * <p> Helper method to clean up all resources </p>
     *
     * @author Samit Gadekar (sgadekar), Benjamin Chen (chen5254)
     */
    private static void cleanup() {
        System.out.println("Shutting down resources...");
        try {
            if (socket != null && !socket.isClosed()) socket.close();
            if (bfr != null) bfr.close();
            if (pw != null) pw.close();
            if (ois != null) ois.close();
            if (oos != null) oos.close();
        } catch (IOException e) {
            System.out.println("Error During Cleanup: " + e.getMessage());
        }
    }

    /**
     * <p> Helper Method, Sends Command To Server, Returns Results </p>
     *
     * @author Samit Gadekar (sgadekar), Henri Goosen (hgoosen)
     */
    private static void sendCommand(String command, String... args) {
        //  System.out.println(command);
        pw.println(command);
        for (String arg : args) {
            System.out.println(arg);
            pw.println(arg);
        }
        pw.flush();
    }

    /**
     * <p> Helper Method used after sendCommand to clear buffer or get success result from server </p>
     *
     * @author Henri Goosen (hgoosen), Benjamin Chen (chen5254), Samit Gadekar (sgadekar)
     */
    private static boolean getSuccess() {
        // Returns Method Result
        boolean success;
        try {
            success = Boolean.parseBoolean(
                    bfr.readLine()); // Reads The Returned Boolean
        } catch (IOException e) {
            success = false;
        }
        return success;
    }




    /*

     */
/**
 * <p> Handles user log in to server.</p>
 * @throws IOException From PrintWriter
 *//*

    @Deprecated(since = "Log In GUI has been implemented.")
    public static boolean logInSequence() throws IOException {
        while (true) {
            System.out.println("1. Log In\n" +
                               "2. Register User\n" +
                               "3. Quit");
            int select = 0;

            try {
                select = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a number.");
            }

            switch (select) {
                case 1 -> { // Log In
                    if (handleLogin()) {
                        return true;
                    }
                }
                case 2 -> { // Register Prompt
                    if (handleRegistration()) {
                        return false; // Stays In The Login Loop
                    }
                }
                case 3 -> { // Confirm & Exit
                    do {
                        System.out.println("Are you sure you'd like to exit?");
                        String ans = scan.nextLine();
                        if (ans.equalsIgnoreCase("Y") || ans.equalsIgnoreCase("Yes")) {
                            exit = true;
                            return false;
                        } else if (ans.equalsIgnoreCase("N") || ans.equalsIgnoreCase("No")) {
                            return false;
                        } else {
                            System.out.println("Enter Y/N.");
                        }
                    } while (true);
                }
                default -> System.out.println("Invalid Command: Please Try Again.");
            }

        }
    }

    @Deprecated(since = "Log In has been implemented.")
    private static boolean handleLogin() {
        System.out.println("Enter Username:");
        String username = scan.nextLine();
        System.out.println("Enter Password:");
        String password = scan.nextLine();


        // Send Data Format: Command \n Arg 1 \n Arg 2

        sendCommand("log_in", username, password);

        boolean loggedIn = getSuccess();
        if (loggedIn) {
            System.out.println("Login Successful!");
            clientUsername = username;
            return true;
        } else {
            System.out.println("Invalid username or password.");
            return false;
        }
    }

    @Deprecated(since = "New User GUI has been implemented.")
    private static boolean handleRegistration() {
        System.out.println("Create Username:");
        String username = scan.nextLine();

        String password;
        String temp;
        do {
            System.out.println("Create Password:");
            password = scan.nextLine();
            System.out.println("Confirm Password:");
            temp = scan.nextLine();
            if (!temp.equals(password)) {
                System.out.println("Password Does Not Match.");
            }
        } while (!temp.equals(password));

        sendCommand("register", username, password);

        boolean registered = getSuccess();

        if (registered) {
            System.out.println("Registration Successful!");
            return true;
        } else {
            System.out.println("Registration Failed.");
            return false;
        }
    }

    private static void settings() {
        int settings = 0;
        boolean invalid = true;
        while (invalid) {
            System.out.println("Select Setting:");
            System.out.println("0. Exit\n" +
                               "1. Privacy\n" +
                               "2. Friends\n" +
                               "3. Blocked\n" +
                               "4. Profile Picture\n" +
                               "5. Friend Requests");
            try {
                settings = Integer.parseInt(scan.nextLine());
                invalid = false;
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input: Please Enter A Number.");
            }
        }

        switch (settings) {
            case 1 -> handlePrivacy();
            case 2 -> handleFriendsList();
            case 3 -> handleBlockedList();
            // case 5 -> handleFriendRequests(); Slightly broken.
        }
    }

    private static void handlePrivacy() {
        int privacy = 0;
        boolean invalid = true;
        while (invalid) {
            System.out.println("Choose Privacy Setting:");
            System.out.println("1. Private\n" +
                               "2. Public");
            try {
                privacy = Integer.parseInt(scan.nextLine());
                invalid = false;
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input. Please Enter A Number.");
            }
        }

        switch (privacy) {
            case 1 -> {
                System.out.println("Set Privacy To Private");
                sendCommand("set_privacy_status", "false");
            }
            case 2 -> {
                System.out.println("Set Privacy To Public");
                sendCommand("set_privacy_status", "true");

            }
        }
        getSuccess();

    }
    private static void handleFriendsList() {
        sendCommand("get_friends_list");

        try {
            Object receivedObject = ois.readObject();

            // Check is an ArrayList.
            if (receivedObject instanceof ArrayList<?> receivedList) {

                // Check all elements in the ArrayList are type User.
                boolean allUsers = receivedList.stream().allMatch(item -> item instanceof String);

                if (allUsers) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> friendsList = (ArrayList<String>) receivedList;

                    String input;
                    while (true) {
                        System.out.println("Friends List: ( Type -exit To Exit)");
                        for (String user : friendsList) {
                            System.out.println(user);
                        }

                        input = scan.nextLine();
                        if (input.equals("-exit")) {
                            break;
                        } else {
                            System.out.println("Invalid Input");
                        }
                    }
                } else {
                    System.out.println("ArrayList Does Not Contain Only Strings.");
                }
            } else {
                System.out.println("Not An ArrayList.");
            }
            bfr.readLine(); // Clear Buffer
        } catch (IOException e) {
            System.out.println("Error: Could Not Receive Friends List " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: ClassNotFoundException " + e);
        }
    }
    private static void handleBlockedList() {
        sendCommand("get_blocked_list");

        try {
            Object receivedObject = ois.readObject();

            // Check is an ArrayList.
            if (receivedObject instanceof ArrayList<?> receivedList) {

                // Check all elements in the ArrayList are type User.
                boolean allUsers = receivedList.stream().allMatch(item -> item instanceof String);

                if (allUsers) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> blockedList = (ArrayList<String>) receivedList;

                    String input;
                    while (true) {
                        System.out.println("Blocked List: (Type -exit To Exit)");
                        for (String user : blockedList) {
                            System.out.println(user);
                        }

                        input = scan.nextLine();
                        if (input.equals("-exit")) {
                            break;
                        } else {
                            System.out.println("Invalid Input");
                        }
                    }
                } else {
                    System.out.println("ArrayList Does Not Contain Only Strings.");
                }
            } else {
                System.out.println("Not An ArrayList.");
            }
            bfr.readLine(); // clear buffer
        } catch (IOException e) {
            System.out.println("Error: Could Not Receive Blocked List " + e);
        } catch (ClassNotFoundException e) {
            System.out.println("Error: ClassNotFoundException " + e);
        }
    }

    private static void handleFriendRequests() {
        sendCommand("get_friend_requests");
        try {
            Object receivedObject = ois.readObject();

            // Check is an ArrayList.
            if (receivedObject instanceof ArrayList<?> receivedList) {

                // Check all elements in the ArrayList are type Users.
                boolean allUsers = receivedList.stream().allMatch(item -> item instanceof String);

                if (allUsers) {
                    @SuppressWarnings("unchecked")
                    ArrayList<String> friendRequestList = (ArrayList<String>) receivedList;

                    String input;
                    while (true) {
                        System.out.println("Pending Friend Requests");
                        System.out.println("Type Name To Accept: (Type -exit To Exit)");
                        for (String user : friendRequestList) {
                            System.out.println(user);
                        }

                        input = scan.nextLine();
                        if (input.equals("-exit")) {
                            break;
                        } else {
                            for (String user : friendRequestList) {
                                if (user.equals(input)) {
                                    sendCommand("add_friend", user);
                                    getSuccess();
                                    System.out.println("Accepted " + user + "'s Friend Request!");
                                    return;
                                }
                            }
                            System.out.println("Invalid Input");
                        }
                    }
                } else {
                    System.out.println("ArrayList Does Not Contain Only Strings.");
                }
            } else {
                System.out.println("Not An ArrayList.");
            }
            bfr.readLine(); // Clear Buffer.
        } catch (Exception e) {
            System.out.println("Error Reading From Server");
        }
    }

    public static void userMenu() {
        while (true) {
            System.out.println("1. Chat\n" +
                               "2. Search\n" +
                               "3. Settings\n" +
                               "4. Log Out");
            int select = 0;

            try {
                select = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Invalid Input. Please Enter A Number.");
            }

            switch (select) {
                case 1 -> chat();
                case 2 -> search();
                case 3 -> settings();
                case 4 -> { // Confirm & Log Out
                    do {
                        System.out.println("Are you sure you'd like to log out?");
                        String ans = scan.nextLine();
                        if (ans.equalsIgnoreCase("Y") || ans.equalsIgnoreCase("Yes")) {
                            return;
                        } else if (ans.equalsIgnoreCase("N") || ans.equalsIgnoreCase("No")) {
                            break;
                        } else {
                            System.out.println("Enter Y/N.");
                        }
                    } while (true);
                }
                default -> System.out.println("Invalid Command: Please Try Again.");
            }
        }
    }

    @Deprecated(since = "Search GUI has been implemented.")
    public static void search() {
        boolean run = true;
        while (run) {
            System.out.println("Enter Username To Search For (Type -exit To Exit): ");
            String term = scan.nextLine();

            if (term.equalsIgnoreCase("-exit")) {
                return;
            }

            sendCommand("search_user", term);

            int index = 1;
            try { // Receiving Search Results
                String line = bfr.readLine();
                while (!line.equals("--STOP--")) {
                    System.out.printf("%d : %s\n", index, line);
                    line = bfr.readLine();
                    index++;
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            if (index == 1) { // No Results Found
                System.out.println("No results found.");
                sendCommand(String.valueOf(-1)); // Inform server no selection.
                getSuccess(); // Clearing Buffer
                continue;
            }

            System.out.println("-1 : Back");

            int select;
            try {
                select = scan.nextInt();
            } catch (InputMismatchException ignored) {
                select = -2;
            }

            scan.nextLine(); // Clear Buffer

            if (select == -1) {
                sendCommand(String.valueOf(-1)); // Back Command
                run = false;
            } else if (0 < select && select <= (index - 1)) {
                sendCommand(String.valueOf(select - 1)); // Send Selected Index

                // Reading back the selected user's profile.
                try {
                    profileOptions(bfr.readLine());
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }

            } else {
                sendCommand(String.valueOf(-1)); // Invalid Command
                System.out.println("Command Not Recognized.");
            }

            // Read boolean response to determine success/failure.
            getSuccess();
        }
    }

    @Deprecated(since = "userProfile GUI has been implemented.")
    private static void profileOptions(String otherUser) {
        // Reading back the selected user's profile.
        try {
            String line = bfr.readLine();
            while (!Objects.equals(line, "--STOP--") && line != null) {
                System.out.println(line);
                line = bfr.readLine();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (true) {
            readProfilePhoto(otherUser);
            System.out.println("[PROFILE IMAGE]\n");

            System.out.println("1. Message\n" +
                               "2. Manage Friend\n" +
                               "3. Manage Block\n" +
                               "4. Go Back");
            int select = 0;

            try {
                select = Integer.parseInt(scan.nextLine());
            } catch (NumberFormatException e) {
               // Covered in switch.
            }

            switch (select) {
                case 1 -> {
                    messageUser(otherUser);
                }
                case 2 -> {
                    int option = 0;
                    boolean invalid = true;
                    while (invalid) {
                        System.out.println("Choose Friend Option:");
                        System.out.println("1: Add Friend\n" +
                                           "2: Remove Friend");
                        try {
                            option = Integer.parseInt(scan.nextLine());
                            invalid = false;
                        } catch (NumberFormatException e) {
                            // Covered by switch.
                        }

                        switch (option) {
                            case 1 -> {
                                sendCommand("add_friend", otherUser);

                                if (getSuccess()) {
                                    System.out.println(otherUser + " added as a friend.");
                                } else {
                                    System.out.printf("Cannot add %s as a friend.\n", otherUser);
                                }
                            }
                            case 2 -> {
                                sendCommand("remove_friend", otherUser);

                                if (getSuccess()) {
                                    System.out.println(otherUser + " has been removed as a friend.");
                                } else {
                                    System.out.printf("Cannot remove %s as a friend.\n", otherUser);
                                }
                            }
                            default -> System.out.println("Command Not Recognized.");
                        }
                    }
                }
                case 3 -> {
                    int option = 0;
                    boolean invalid = true;
                    while (invalid) {
                        System.out.println("Manage Block Status:");
                        System.out.println("1: Block\n" +
                                           "2: Unblock");
                        try {
                            option = Integer.parseInt(scan.nextLine());
                            invalid = false;
                        } catch (NumberFormatException e) {
                            // Covered by switch.
                        }

                        switch (option) {
                            case 1 -> {
                                System.out.println(otherUser + " has been blocked.");
                                sendCommand("block_user", otherUser);
                                getSuccess();
                            }
                            case 2 -> {
                                System.out.println(otherUser + " has been unblocked.");
                                sendCommand("unblock_user", otherUser);
                                getSuccess();
                            }
                            default -> System.out.println("Command Not Recognized.");
                        }
                    }
                }
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid Command: Please Try Again.");
            }
        }

    }

    private static void chat() {
        do {
            System.out.print("Target Username ('Type -exit' To Exit): ");
            String targetUsername = scan.nextLine();

            if (targetUsername.equals("-exit")) {
                return;
            }
            if (targetUsername.equals(clientUsername)) {
                System.out.println("You cannot chat with yourself!");
                return;
            }

            // System.out.println("Loading Messages...");
            ArrayList<Message> msgHistory = getMessageHistory(targetUsername);
            try {
                // Boolean sent from server when getMessageHistory() called.
                String validTarget = bfr.readLine();
                // String since somewhere on server, the pw is not flushed, so boolean would always be false.
                if (!validTarget.contains("true")) {
                    System.out.println("Invalid target user.");
                    continue;
                }
            } catch (IOException e) {
                System.out.println("Could not validate user.");
                System.out.println(e.getMessage());
                continue;
            }

            printMessageHistory(targetUsername, 0);

            while (messageUser(targetUsername));

        } while (true);
    }
    private static void printMessageHistory(String targetUsername, int option) {
        // System.out.println("Loading Messages...");
        ArrayList<Message> msgHistory = getMessageHistory(targetUsername);
        getSuccess(); // Clear Buffer

        System.out.println("Message history with " + targetUsername + ":");
        if (!msgHistory.isEmpty()) {
            switch (option) {
                case 1 -> { // Printing for selecting message.
                    int index = msgHistory.size() - 1;
                    int digits = 0;
                    for (int i = index; i > 0; i /= 10) {
                        digits++;
                    }
                    for (Message msg : msgHistory) {
                        System.out.printf("%" + (digits + 3) + "s ", String.format("[%d]", index--)); // Aligning
                        System.out.println(msg);
                    }
                }
                default -> { // Simple Printing
                    for (Message msg : msgHistory) {
                        System.out.println("  " + msg);
                    }
                }
            }
        } else {
            System.out.println("  (--:--) No Messages Found");
        }
    }
    private static boolean messageUser(String targetUsername) {
        final String exitCase = "-exit";
        final String refreshCase = "-ref";
        final String deleteCase = "-del";

        System.out.printf("%n<'%s' To Exit, '%s' To Refresh, '%s' To Delete>%n", exitCase, refreshCase, deleteCase);
        System.out.printf("%s --> %s : ", clientUsername, targetUsername);
        String targetMessage = scan.nextLine();

        switch (targetMessage) {
            case exitCase -> {
                return false;
            }
            case refreshCase -> {
                printMessageHistory(targetUsername, 0);
            }
            case deleteCase -> {
                printMessageHistory(targetUsername, 1);
                System.out.println("Please enter the indexes of the messages you want to delete, separated by commas: ");
                String[] stringIndexInput = scan.nextLine().replaceAll(" ", "").split(",");

                int[] intIndexInput = new int[stringIndexInput.length];
                int maxSize = getMessageHistory(targetUsername).size();
                getSuccess(); // Clearing Buffer
              
                for (int i = 0; i < stringIndexInput.length; i++) {
                    try {
                        int index = Integer.parseInt(stringIndexInput[i]);
                        if (stringIndexInput[i].isEmpty()) {
                            intIndexInput[i] = -1;
                        } else if (index < 0 || index >= maxSize) {
                            System.out.println("Invalid Input: IndexOutOfBounds " + stringIndexInput[i]);
                            intIndexInput[i] = -1;
                        } else {
                            intIndexInput[i] = index;
                        }
                    } catch (NumberFormatException e) {
                        System.out.println("Invalid Input: NumberFormat " + stringIndexInput[i]);
                        intIndexInput[i] = -1;
                    }
                }

                // Removing Duplicate Values
                ArrayList<Integer> removedDuplicates = new ArrayList<>();
                for (int i : intIndexInput) {
                    if (!removedDuplicates.contains(i)) {
                        removedDuplicates.add(i);
                    }
                }
                // Convert ArrayList<Integer> to int[]
                int[] result = new int[removedDuplicates.size()];
                for (int i = 0; i < removedDuplicates.size(); i++) {
                    result[i] = removedDuplicates.get(i);
                }
                intIndexInput = result;

                // Converting back into string to submit to server.
                String stringIndexes = "";
                for (int validIndex : intIndexInput) {
                    if (validIndex != -1) {
                        stringIndexes += validIndex + ",";
                    }
                }

                // Submitting To Server
                if (!stringIndexes.isEmpty()) {
                    System.out.println("Deleting Messages: " + stringIndexes.substring(0, stringIndexes.length() - 1));
                    sendCommand("delete_message", clientUsername, targetUsername, stringIndexes.substring(0, stringIndexes.length() - 1));
                    getSuccess(); // Clearing Buffer
                } else {
                    System.out.println("Deleting Messages: None Provided");
                }

                printMessageHistory(targetUsername, 0);
            }
            default -> {
                sendCommand("send_message", clientUsername, targetUsername, targetMessage);

                boolean status = getSuccess();
                if (status) {
                    printMessageHistory(targetUsername, 0);
                    return true;
                } else {
                    System.out.println("Error: Message Send Failure");
                    printMessageHistory(targetUsername, 0);
                    return false;
                }
            }
        }
        return true; // Unreachable Statement -  I Think...
    }
*/

}
