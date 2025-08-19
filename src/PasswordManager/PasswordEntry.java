package PasswordManager;

public class PasswordEntry {
    private String place;
    private String login;
    private String password;

    public PasswordEntry(String place, String login, String password) {
        this.place = place;
        this.login = login;
        this.password = password;
    }

    // Геттеры
    public String getPlace() { return place; }
    public String getLogin() { return login; }
    public String getPassword() { return password; }

    @Override
    public String toString() {
        return String.format("%s,%s,%s", place, login, password);
    }
}