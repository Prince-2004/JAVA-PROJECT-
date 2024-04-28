import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTables {
    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/mydatabase";
    private static final String USERNAME = "username";
    private static final String PASSWORD = "password";
    private static final String CREATE_QUESTION_TABLE = "CREATE TABLE IF NOT EXISTS questions (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "question_text VARCHAR(255) NOT NULL" +
            ")";

    private static final String CREATE_OPTION_TABLE = "CREATE TABLE IF NOT EXISTS options (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "question_id INT NOT NULL," +
            "option_text VARCHAR(255) NOT NULL," +
            "FOREIGN KEY (question_id) REFERENCES questions(id)" +
            ")";

    private static final String CREATE_ANSWER_TABLE = "CREATE TABLE IF NOT EXISTS answers (" +
            "question_id INT PRIMARY KEY," +
            "correct_option_id INT NOT NULL," +
            "FOREIGN KEY (question_id) REFERENCES questions(id)," +
            "FOREIGN KEY (correct_option_id) REFERENCES options(id)" +
            ")";

    private static final String CREATE_STUDENT_TABLE = "CREATE TABLE IF NOT EXISTS students (" +
            "id INT AUTO_INCREMENT PRIMARY KEY," +
            "name VARCHAR(100) NOT NULL," +
            "email VARCHAR(100) NOT NULL" +
            ")";

    private static final String CREATE_MARKS_TABLE = "CREATE TABLE IF NOT EXISTS marks (" +
            "student_id INT NOT NULL," +
            "question_id INT NOT NULL," +
            "marks_obtained INT NOT NULL," +
            "FOREIGN KEY (student_id) REFERENCES students(id)," +
            "FOREIGN KEY (question_id) REFERENCES questions(id)" +
            ")";

    public static void main(String[] args) {
        try (Connection conn = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
             Statement stmt = conn.createStatement()) {
            stmt.executeUpdate(CREATE_QUESTION_TABLE);
            stmt.executeUpdate(CREATE_OPTION_TABLE);
            stmt.executeUpdate(CREATE_ANSWER_TABLE);
            stmt.executeUpdate(CREATE_STUDENT_TABLE);
            stmt.executeUpdate(CREATE_MARKS_TABLE);
            System.out.println("Tables created successfully.");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
