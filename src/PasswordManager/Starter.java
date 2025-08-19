package PasswordManager;

import java.util.Scanner;

public class Starter {
    public static void main(String[] args) {
        PasswordManager manager = new PasswordManager();

        Scanner scanner = new Scanner(System.in);

        System.out.print("Введите мастер-пароль: ");
        String masterPassword = scanner.nextLine();

        try {
            manager.loadEntries(masterPassword);
        } catch (Exception e) {
            System.out.println("Ошибка загрузки данных. Проверьте пароль.");
            e.printStackTrace();
            return;
        }

        while (true) {
            System.out.println("\nМенеджер паролей:");
            System.out.println("1. Добавить запись");
            System.out.println("2. Удалить запись");
            System.out.println("3. Просмотреть все записи");
            System.out.println("4. Получить конкретную запись");
            System.out.println("5. Выйти");
            System.out.print("Выберите действие: ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // consume newline

            switch (choice) {
                case 1:
                    System.out.print("Место: ");
                    String place = scanner.nextLine();
                    System.out.print("Логин: ");
                    String login = scanner.nextLine();
                    System.out.print("Пароль: ");
                    String password = scanner.nextLine();
                    manager.addEntry(place, login, password);
                    try {
                        manager.saveEntries(masterPassword);
                        System.out.println("Запись добавлена.");
                    } catch (Exception ex) {
                        System.out.println("Ошибка сохранения.");
                        ex.printStackTrace();
                    }
                    break;
                case 2:
                    manager.displayEntries();
                    System.out.print("Введите номер записи для удаления: ");
                    int deleteIndex = scanner.nextInt();
                    manager.removeEntry(deleteIndex);
                    try {
                        manager.saveEntries(masterPassword);
                        System.out.println("Запись удалена.");
                    } catch (Exception ex) {
                        System.out.println("Ошибка сохранения.");
                        ex.printStackTrace();
                    }
                    break;
                case 3:
                    manager.displayEntries();
                    break;
                case 4:
                    manager.displayEntries();
                    System.out.print("Введите номер записи для просмотра: ");
                    int viewIndex = scanner.nextInt();
                    PasswordEntry entry = manager.getEntry(viewIndex);
                    if (entry != null) {
                        System.out.println("Место: " + entry.getPlace());
                        System.out.println("Логин: " + entry.getLogin());
                        System.out.println("Пароль: " +
                                (entry.getPassword() == null || entry.getPassword().isEmpty()
                                        ? "[не указан]" : entry.getPassword()));
                    } else {
                        System.out.println("Неверный номер записи.");
                    }
                    break;
                case 5:
                    System.out.println("Выход из программы.");
                    scanner.close();
                    return;
                default:
                    System.out.println("Неверный выбор.");
            }
        }
    }
}