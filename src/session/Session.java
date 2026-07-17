package src.session;

public class Session {

    public static int userId;

    public static String username;

    public static void setUser(int id, String user) {

        userId = id;
        username = user;

    }

    public static void clear() {

        userId = 0;
        username = null;

    }

}