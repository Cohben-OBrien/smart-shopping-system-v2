package User;

import java.sql.SQLException;
import java.util.ArrayList;

public class User_authenticator {


    public static boolean User_Authemticator(String username, String password) throws SQLException {
        ArrayList<User> users = Database.Data.Load_users();
        System.out.println(users);
        for (User user : users) {
            if (user.login(username, password)) { return true; }
        }
        return false;
    }


}
