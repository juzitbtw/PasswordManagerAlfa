package PasswordManager;

import javax.swing.*;
import java.awt.*;

public class GUI extends JFrame {
    private PasswordManager passwordManager;
    private JList<String> entryList;
    private DefaultListModel<String> listModel;
    private JTextField placeField, loginField, passwordField;
    private JPasswordField masterPasswordField;
    private JButton addButton, removeButton, getButton;
    private boolean isLoggedIn = false;

    public GUI(PasswordManager manager) {
        this.passwordManager = manager;
        setTitle("Mine Secure Passwords");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setFont(new Font("Segoe UI", Font.PLAIN, 14));

        // Панель ввода мастер-пароля
        JPanel authPanel = new JPanel(new FlowLayout());
        JLabel masterLabel = new JLabel("Мастер-пароль:");
        masterPasswordField = new JPasswordField(15);
        JButton loadButton = new JButton("Загрузить");

        loadButton.addActionListener(e -> {
            try {
                String masterPass = new String(masterPasswordField.getPassword());
                passwordManager.loadEntries(masterPass);
                refreshEntryList();
                isLoggedIn = true;
                enableButtons();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        authPanel.add(masterLabel);
        authPanel.add(masterPasswordField);
        authPanel.add(loadButton);

        // Панель ввода новых данных
        JPanel inputPanel = new JPanel(new GridLayout(3, 2, 10, 5));
        JLabel placeLabel = new JLabel("Место:");
        JLabel loginLabel = new JLabel("Логин:");
        JLabel passwordLabel = new JLabel("Пароль:");

        placeField = new JTextField(15);
        loginField = new JTextField(15);
        passwordField = new JTextField(15);

        addButton = new JButton("Добавить");
        removeButton = new JButton("Удалить");
        getButton = new JButton("Получить данные");

        inputPanel.add(placeLabel);
        inputPanel.add(placeField);
        inputPanel.add(loginLabel);
        inputPanel.add(loginField);
        inputPanel.add(passwordLabel);
        inputPanel.add(passwordField);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        buttonPanel.add(addButton);
        buttonPanel.add(removeButton);
        buttonPanel.add(getButton);

        disableButtons();

        // Обработчики событий
        addButton.addActionListener(e -> {
            if (!isLoggedIn) return;
            try {
                String masterPass = new String(masterPasswordField.getPassword());
                String place = placeField.getText().trim();
                String login = loginField.getText().trim();
                String password = passwordField.getText().trim();

                // Добавляем проверку на пустые строки
                place = place.isEmpty() ? "[Нет адреса]" : place;
                login = login.isEmpty() ? "[Нет логина]" : login;
                password = password.isEmpty() ? "[Нет пароля]" : password;

                passwordManager.addEntry(place, login, password);
                passwordManager.saveEntries(masterPass);
                refreshEntryList();
                clearFields();
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        removeButton.addActionListener(e -> {
            if (!isLoggedIn) return;
            try {
                int selectedIndex = entryList.getSelectedIndex();
                if (selectedIndex != -1) {
                    String masterPass = new String(masterPasswordField.getPassword());
                    passwordManager.removeEntry(selectedIndex);
                    passwordManager.saveEntries(masterPass);
                    refreshEntryList();
                } else {
                    JOptionPane.showMessageDialog(this, "Выберите запись для удаления.", "Ошибка", JOptionPane.WARNING_MESSAGE);
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
        });

        getButton.addActionListener(e -> {
            if (!isLoggedIn) return;
            int selectedIndex = entryList.getSelectedIndex();
            if (selectedIndex != -1) {
                PasswordEntry entry = passwordManager.getEntry(selectedIndex);
                if (entry != null) {
                    StringBuilder sb = new StringBuilder();
                    sb.append("Место: ").append(entry.getPlace()).append("\n")
                            .append("Логин: ").append(entry.getLogin()).append("\n")
                            .append("Пароль: ").append(entry.getPassword() != null && !entry.getPassword().isEmpty() ?
                                    entry.getPassword() : "[не указан]");
                    JOptionPane.showMessageDialog(this, sb.toString(), "Данные", JOptionPane.INFORMATION_MESSAGE);
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите запись для просмотра.", "Ошибка", JOptionPane.WARNING_MESSAGE);
            }
        });

        // Список записей
        listModel = new DefaultListModel<>();
        entryList = new JList<>(listModel);
        JScrollPane scrollPane = new JScrollPane(entryList);
        scrollPane.setPreferredSize(new Dimension(300, 300));

        // Основная компоновка
        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        mainPanel.add(authPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.add(inputPanel, BorderLayout.NORTH);
        rightPanel.add(buttonPanel, BorderLayout.SOUTH);

        mainPanel.add(rightPanel, BorderLayout.EAST);

        add(mainPanel);
    }

    private void refreshEntryList() {
        listModel.clear();
        for (int i = 0; i < passwordManager.entries.size(); i++) {
            PasswordEntry entry = passwordManager.entries.get(i);
            listModel.addElement(i + ": " + entry.getPlace() + " - " + entry.getLogin());
        }
    }

    private void clearFields() {
        placeField.setText("");
        loginField.setText("");
        passwordField.setText("");
    }

    private void disableButtons() {
        addButton.setEnabled(false);
        removeButton.setEnabled(false);
        getButton.setEnabled(false);
    }

    private void enableButtons() {
        addButton.setEnabled(true);
        removeButton.setEnabled(true);
        getButton.setEnabled(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            PasswordManager manager = new PasswordManager();
            GUI gui = new GUI(manager);
            gui.setVisible(true);
        });
    }
}