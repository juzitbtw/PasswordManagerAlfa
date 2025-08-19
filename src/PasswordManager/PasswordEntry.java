package PasswordManager;

public class PasswordEntry {
    private String place;
    private String login;
    private String password;

    public PasswordEntry(String place, String login, String password) {
        this.place = place != null ? place : "";
        this.login = login != null ? login : "";
        this.password = password != null ? password : ""; // Разрешаем пустую строку
    }

    public String getPlace() { return place; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return String.format("%s,%s,%s",
                place != null ? place : "",
                login != null ? login : "",
                password != null ? password : "");
    }
}