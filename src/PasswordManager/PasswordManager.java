package PasswordManager;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class PasswordManager {
    private static final String FILE_NAME = "mineSecurePasswords.txt";
    List<PasswordEntry> entries = new ArrayList<>();
    private byte[] encryptionKey;
    private byte[] salt;

    public void loadEntries(String masterPassword) throws Exception {
        if (!Files.exists(Paths.get(FILE_NAME))) {
            Files.createFile(Paths.get(FILE_NAME));
            salt = KeyDeriver.generateSalt(16); // Генерируем соль при создании файла
            return;
        }

        byte[] fileData = Files.readAllBytes(Paths.get(FILE_NAME));

        // Читаем соль из начала файла
        if (fileData.length >= 16) {
            salt = Arrays.copyOfRange(fileData, 0, 16);
        } else {
            salt = KeyDeriver.generateSalt(16); // Если файл повреждён — генерируем новую соль
        }

        int offset = 16;

        // Генерируем ключ из мастер-пароля и соли
        this.encryptionKey = KeyDeriver.deriveKey(masterPassword, salt, 65536, 256);

        while (offset < fileData.length) {
            // Читаем IV (12 байт)
            byte[] iv = Arrays.copyOfRange(fileData, offset, offset + 12);
            offset += 12;

            // Читаем зашифрованные данные
            byte[] encryptedData = Arrays.copyOfRange(fileData, offset, fileData.length);
            offset = fileData.length;

            // Расшифровываем данные
            String decrypted = AESEncryption.decrypt(this.encryptionKey, Base64.getEncoder().encodeToString(iv) +
                    Base64.getEncoder().encodeToString(encryptedData));

            String[] parts = decrypted.split(",");
            if (parts.length == 3) {
                entries.add(new PasswordEntry(parts[0], parts[1], parts[2]));
            }
        }
    }

    public void saveEntries(String masterPassword) throws Exception {
        boolean isNewFile = !Files.exists(Paths.get(FILE_NAME));

        // Генерируем новую соль, если файл новый или salt == null
        byte[] saltToUse = isNewFile || salt == null ? KeyDeriver.generateSalt(16) : salt;

        this.encryptionKey = KeyDeriver.deriveKey(masterPassword, saltToUse, 65536, 256);

        try (FileOutputStream fos = new FileOutputStream(FILE_NAME)) {
            fos.write(saltToUse); // Сохраняем соль один раз

            for (PasswordEntry entry : entries) {
                try {
                    String encrypted = AESEncryption.encrypt(this.encryptionKey, entry.toString());
                    byte[] encryptedBytes = Base64.getDecoder().decode(encrypted);
                    fos.write(encryptedBytes);
                } catch (Exception e) {
                    System.err.println("Ошибка шифрования записи: " + entry.toString());
                    e.printStackTrace();
                    throw e;
                }
            }
        } catch (IOException e) {
            System.err.println("Ошибка записи в файл: " + FILE_NAME);
            e.printStackTrace();
            throw e;
        }

        this.salt = saltToUse; // Обновляем поле класса
    }

    public void addEntry(String place, String login, String password) {
        entries.add(new PasswordEntry(place, login, password));
    }

    public void removeEntry(int index) {
        if (index >= 0 && index < entries.size()) {
            entries.remove(index);
        }
    }

    public void displayEntries() {
        for (int i = 0; i < entries.size(); i++) {
            System.out.println(i + ": " + entries.get(i).getPlace() + " - " + entries.get(i).getLogin());
        }
    }

    public PasswordEntry getEntry(int index) {
        return (index >= 0 && index < entries.size()) ? entries.get(index) : null;
    }
}