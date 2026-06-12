package domain;

public class User {
    private int userId;
    private String loginId;
    private String password;
    private String name;


    public User() {
    }

    // DB 조회용
    public User(int userId, String loginId, String password, String name) {
        this.userId = userId;
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }
    
    // 생성용
    public User (String loginId, String password, String name) {
        this.loginId = loginId;
        this.password = password;
        this.name = name;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    // 사용자 조회용
    public String toString() {
        return "User{" +
                "userId=" + userId +
                ", loginId='" + loginId + '\'' +
                ", name='" + name + '\'' +
                '}';
    }

}