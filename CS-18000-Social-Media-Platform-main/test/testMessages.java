
public class testMessages {
    public static void main(String[] args) {
        String[] parts = args[0].split("\\n");
        String[] user1Info = parts[0].split(",");
        String[] user2Info = parts[1].split(",");
        try {
            User user1 = new User(user1Info[0], user1Info[1]);
            User user2 = new User(user2Info[0], user2Info[1]);
            User[] users = {user1, user2};
            Chat chat = new Chat(users);
            Message m = new Message(parts[3]);

            user1.addChat(chat);
            chat.setUser(0, user1);
            user2.addChat(chat);
            chat.setUser(1,user2);
            //Message sender private, receiver public
            user2.setPublic(true);
            //Message sender public, receiver private
            user2.setPublic(false);
            user1.setPublic(true);
            if (user1.message(chat, m)) {
                System.out.println("Able to message non-friend who is in private mode.");
            }
            //Message receiver hasn't added friend
            user1.setPublic(false);
            user1.addFriend("username2");
            if (user1.message(chat, m)) {
                System.out.println("Able to message user who hasn't friended back.");
            }

            //Message while not in chat
            user1.deleteChat("username2");
            chat = new Chat(users);
            chat.setUser(0, user2);
            user2.addFriend("username1");
            user1.addFriend("username2");
            user2.addFriend("username1");
            if (user1.message(chat, m)) {
                System.out.println("Was able to message chat user isn't in.");
            }

            user1.addChat(chat);
            chat.setUser(1,user1);
            user1.addBlocked("username2");
            if (user1.message(chat, m)) {
                System.out.println("Able to message blocked user");
            }

            user1.removeBlocked("username2");
            user2.addBlocked("username1");
            if (user1.message(chat, m)) {
                System.out.println("Able to message user who has them blocked.");
            }
            user2.removeBlocked("username1");

            if (user1.message(chat, m)) {
                System.out.println("Message 1 sent successfully.");
            }
            user1.setPublic(true);
            user2.setPublic(true);
            user1.removeFriend("username2");
            user2.removeFriend("username1");
            if (user1.message(chat, m)) {
                System.out.println("Message 2 sent successfully.");
            }

            user1.setPublic(false);
            user1.addFriend("username2");
            user2.addFriend("username1");
            user1.addFriend("username2");
            if (user1.message(chat,m)) {
                System.out.println("Message 3 sent successfully.");
            }

            user1.setPublic(true);
            user1.removeFriend("username2");
            user2.setPublic(false);
            if (user1.message(chat, m)) {
                System.out.println("Message 4 sent successfully.");
            }


        } catch (InvalidUserException e) {
            e.printStackTrace();
        }
    }
}
