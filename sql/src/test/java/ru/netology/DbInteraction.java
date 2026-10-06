package ru.netology;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import lombok.SneakyThrows;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.util.Random;

public class DbInteraction {
  @BeforeEach
  @SneakyThrows
  void setUp() {
    var faker = new Faker();
    // Вставляем два пользователя
    try (
        var conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/app", "app", "pass"
        );
        var dataStmt = conn.prepareStatement("INSERT INTO users(login, password) VALUES (?, ?);")
    ) {
      for (int i = 0; i < 2; i++) { 
          dataStmt.setString(1, faker.name().username());
          dataStmt.setString(2, "password");
          dataStmt.executeUpdate();
      }
    }
  }

  @Test
  @SneakyThrows
  void stubTest() {
    // Проверяем количество пользователей
    try (
        var conn = DriverManager.getConnection(
            "jdbc:mysql://localhost:3306/app", "app", "pass"
        )
    ) {
      try (var rs = conn.createStatement()
              .executeQuery("SELECT COUNT(*) FROM users;")
      ) {
        if (rs.next()) {
          int count = rs.getInt(1);
          assertThat(count).isEqualTo(2); // У нас должно быть ровно 2 пользователя
        }
      }

      // Получаем карты первого пользователя и проверяем баланс
      try (var cardsStmt = conn.prepareStatement(
              "SELECT id, number, balance_in_kopecks FROM cards WHERE user_id = ?;"
      )) {
        cardsStmt.setInt(1, 1); // Берём карты у первого пользователя
        
        try (var rs = cardsStmt.executeQuery()) {
          while (rs.next()) {
            int id = rs.getInt("id");
            String number = rs.getString("number");
            int balanceInKopecks = rs.getInt("balance_in_kopecks"); // 🚨 Сломанный тест здесь!
            
            /* ✅ Чтобы проверить работу CI:
             * Оставьте эту строку — она всегда будет падать,
             * так как реальный баланс не равен 999_999 копейкам.
             */
            assertThat(balanceInKopecks)
                .as("Баланс должен быть заведомо неверным!")
                .isEqualTo(999_999); ❌❌❌
          }
        }
      }
    }
}
