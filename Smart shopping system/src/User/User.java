package User;

public class User {
    private int id;
    private String Username;
    private String password;
    public static enum access_levels {
        ADMIN,
        SHOP_CLARK
    }
    private access_levels access_level;



    public User(int id, String username, String password, String access_level) {
        this.id = id;
        Username = username;
        this.password = password;
        switch (access_level) {
            case "ADMIN":
                this.access_level = access_levels.ADMIN;
                break;
            case "SHOP_CLARK":
                this.access_level = access_levels.SHOP_CLARK;
                break;
        }
    }

    public boolean login(String username, String password) {
        System.out.println("Username : " + username + " Password : " + password);
        return this.Username.equals(username) && this.password.equals(password);
    }

    public access_levels[] getAccess_levels() {
        return access_levels.values();
    }
    
    public access_levels getAccessLevel() {
        return access_level;
    }
    
    


}
