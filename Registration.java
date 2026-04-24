import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public class Registration {

    private static final Logger LOGGER = Logger.getLogger(Registration.class.getName());

    private static final int PBKDF2_ITERATIONS = 310_000;
    private static final int SALT_LENGTH_BYTES  = 16;
    private static final int KEY_LENGTH_BITS    = 256;

    // Credentials are read from environment variables; set DB_URL, DB_USER, DB_PASSWORD before running.
    private static final String DB_URL  = System.getenv().getOrDefault("DB_URL",  "jdbc:mysql://localhost:3306/registration_db");
    private static final String DB_USER = System.getenv().getOrDefault("DB_USER", "root");
    private static final String DB_PASS = System.getenv("DB_PASSWORD");

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    /**
     * Hashes the given password using PBKDF2WithHmacSHA256 with a random salt.
     * Returns a single string in the format {@code base64(salt):base64(hash)}.
     */
    static String hashPassword(char[] password) {
        try {
            SecureRandom random = new SecureRandom();
            byte[] salt = new byte[SALT_LENGTH_BYTES];
            random.nextBytes(salt);

            PBEKeySpec spec = new PBEKeySpec(password, salt, PBKDF2_ITERATIONS, KEY_LENGTH_BITS);
            try {
                SecretKeyFactory factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256");
                byte[] hash = factory.generateSecret(spec).getEncoded();
                return Base64.getEncoder().encodeToString(salt)
                        + ":" + Base64.getEncoder().encodeToString(hash);
            } finally {
                spec.clearPassword();
            }
        } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
            throw new IllegalStateException("Password hashing failed", e);
        }
    }

    static boolean isValidEmail(String email) {
        if (email == null) return false;
        String pattern = "^[\\w._%+\\-]+@[\\w\\-]+(\\.[\\w\\-]+)*\\.[a-zA-Z]{2,}$";
        return email.matches(pattern);
    }

    static boolean isStrongPassword(char[] password) {
        return password != null && password.length >= 8;
    }

    public static void registerUser(String name, String email, String phone, char[] password) {
        if (name == null || name.isBlank()) {
            System.out.println("Registration failed: Name is required.");
            return;
        }
        if (!isValidEmail(email)) {
            System.out.println("Registration failed: Invalid email address.");
            return;
        }
        if (!isStrongPassword(password)) {
            System.out.println("Registration failed: Password must be at least 8 characters.");
            return;
        }

        String hashedPassword = hashPassword(password);
        java.util.Arrays.fill(password, '\0');   // clear sensitive data

        String sql = "INSERT INTO users (name, email, phone, password) VALUES (?, ?, ?, ?)";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, name);
            pstmt.setString(2, email);
            pstmt.setString(3, phone);
            pstmt.setString(4, hashedPassword);
            int rows = pstmt.executeUpdate();
            if (rows > 0) {
                System.out.println("Registration successful for: " + name);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error during registration", e);
            System.out.println("Registration failed. Please try again later.");
        }
    }

    public static void displayUsers() {
        String sql = "SELECT id, name, email, phone FROM users";
        try (Connection conn = getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            System.out.println("\n--- Registered Users ---");
            System.out.printf("%-5s %-20s %-30s %-15s%n", "ID", "Name", "Email", "Phone");
            System.out.println("-".repeat(72));
            while (rs.next()) {
                System.out.printf("%-5d %-20s %-30s %-15s%n",
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("email"),
                        rs.getString("phone"));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Database error while fetching users", e);
            System.out.println("Unable to retrieve user list. Please try again later.");
        }
    }

    public static void main(String[] args) {
        java.io.Console console = System.console();

        System.out.println("=== Registration Process ===");

        String name, email, phone;
        char[] password;

        if (console != null) {
            name     = console.readLine("Enter Name: ");
            email    = console.readLine("Enter Email: ");
            phone    = console.readLine("Enter Phone: ");
            password = console.readPassword("Enter Password (min 8 chars): ");
        } else {
            // Fallback for IDE / redirected input (password will be visible)
            java.util.Scanner scanner = new java.util.Scanner(System.in);
            System.out.print("Enter Name: ");
            name = scanner.nextLine();
            System.out.print("Enter Email: ");
            email = scanner.nextLine();
            System.out.print("Enter Phone: ");
            phone = scanner.nextLine();
            System.out.print("Enter Password (min 8 chars): ");
            password = scanner.nextLine().toCharArray();
            scanner.close();
        }

        registerUser(name, email, phone, password);
        displayUsers();
    }
}
