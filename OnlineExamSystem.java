import java.util.*;
import java.sql.*;

public class OnlineExamSystem {

    
    static class Question {
        String text;
        String optionA;
        String optionB;
        String optionC;
        String optionD;
        char correctOption; 

        Question(String text, String optionA, String optionB, String optionC, String optionD, char correctOption) {
            this.text = text;
            this.optionA = optionA;
            this.optionB = optionB;
            this.optionC = optionC;
            this.optionD = optionD;
            this.correctOption = Character.toUpperCase(correctOption);
        }
    }

    static class User {
        String username;
        String password;
        String role; 

        User(String username, String password, String role) {
            this.username = username;
            this.password = password;
            this.role = role;
        }
    }

    private static final List<User> users = new ArrayList<>();

    private static final Scanner scanner = new Scanner(System.in);


    private static final String DB_URL  = "jdbc:mysql://localhost:3306/online_exam";
    private static final String DB_USER = "root";       
    private static final String DB_PASS = "password";   

    public static void main(String[] args) {
        seedUsers();
        showWelcomeMenu();
    }


    private static void seedUsers() {
        users.add(new User("admin", "admin123", "ADMIN"));
        users.add(new User("student", "stud123", "STUDENT"));
    }
    private static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
    }

    private static void showWelcomeMenu() {
        while (true) {
            System.out.println("\n===== ONLINE EXAMINATION SYSTEM =====");
            System.out.println("1. Login");
            System.out.println("2. Exit");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    login();
                    break;
                case "2":
                    System.out.println("Exiting... Goodbye!");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void login() {
        System.out.print("\nEnter username: ");
        String username = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine().trim();

        User loggedIn = authenticate(username, password);
        if (loggedIn == null) {
            System.out.println("Invalid credentials. Try again.");
            return;
        }

        System.out.println("\nLogin successful. Welcome, " + loggedIn.username + " (" + loggedIn.role + ")");
        if ("ADMIN".equalsIgnoreCase(loggedIn.role)) {
            adminMenu();
        } else if ("STUDENT".equalsIgnoreCase(loggedIn.role)) {
            studentMenu(loggedIn);
        }
    }

    private static User authenticate(String username, String password) {
        for (User u : users) {
            if (u.username.equals(username) && u.password.equals(password)) {
                return u;
            }
        }
        return null;
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n===== ADMIN MENU =====");
            System.out.println("1. View Questions");
            System.out.println("2. Add Question");
            System.out.println("3. Logout");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    viewQuestionsFromDB();
                    break;
                case "2":
                    addQuestionToDB();
                    break;
                case "3":
                    System.out.println("Logging out admin...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    private static void studentMenu(User student) {
        while (true) {
            System.out.println("\n===== STUDENT MENU =====");
            System.out.println("1. Take Quiz");
            System.out.println("2. Logout");
            System.out.print("Enter choice: ");
            String choice = scanner.nextLine();

            switch (choice) {
                case "1":
                    takeQuiz(student);
                    break;
                case "2":
                    System.out.println("Logging out student...");
                    return;
                default:
                    System.out.println("Invalid choice. Try again.");
            }
        }
    }

    
    private static void viewQuestionsFromDB() {
        System.out.println("\n===== LIST OF QUESTIONS (DB) =====");
        String sql = "SELECT id, text, option_a, option_b, option_c, option_d, correct_option FROM questions";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            int idx = 1;
            boolean any = false;
            while (rs.next()) {
                any = true;
                String text = rs.getString("text");
                String a = rs.getString("option_a");
                String b = rs.getString("option_b");
                String c = rs.getString("option_c");
                String d = rs.getString("option_d");
                char correct = rs.getString("correct_option").toUpperCase().charAt(0);

                System.out.println(idx++ + ". " + text);
                System.out.println("   A) " + a);
                System.out.println("   B) " + b);
                System.out.println("   C) " + c);
                System.out.println("   D) " + d);
                System.out.println("   Correct: " + correct);
            }

            if (!any) {
                System.out.println("No questions in database.");
            }
        } catch (Exception e) {
            System.out.println("Error reading questions: " + e.getMessage());
        }
    }

    private static void addQuestionToDB() {
        System.out.println("\n===== ADD NEW QUESTION (DB) =====");
        System.out.print("Enter question text: ");
        String text = scanner.nextLine();

        System.out.print("Option A: ");
        String a = scanner.nextLine();
        System.out.print("Option B: ");
        String b = scanner.nextLine();
        System.out.print("Option C: ");
        String c = scanner.nextLine();
        System.out.print("Option D: ");
        String d = scanner.nextLine();

        System.out.print("Correct option (A/B/C/D): ");
        String correctInput = scanner.nextLine().trim().toUpperCase();
        char correct = (correctInput.isEmpty() ? 'A' : correctInput.charAt(0));

        String sql = "INSERT INTO questions (text, option_a, option_b, option_c, option_d, correct_option) " +
                     "VALUES (?, ?, ?, ?, ?, ?)";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setString(1, text);
            ps.setString(2, a);
            ps.setString(3, b);
            ps.setString(4, c);
            ps.setString(5, d);
            ps.setString(6, String.valueOf(correct));

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("Question added successfully to database!");
            } else {
                System.out.println("Failed to add question.");
            }

        } catch (Exception e) {
            System.out.println("Error inserting question: " + e.getMessage());
        }
    }

    private static void takeQuiz(User student) {
        System.out.println("\n===== START QUIZ =====");

        int count;
        System.out.print("How many questions do you want to answer? ");
        try {
            count = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Invalid number. Defaulting to 3 questions.");
            count = 3;
        }

        List<Question> quizQuestions = loadRandomQuestionsFromDB(count);
        if (quizQuestions.isEmpty()) {
            System.out.println("No questions available in database. Contact admin.");
            return;
        }
        
        long totalTimeMillis = 60_000; 
        long startTime = System.currentTimeMillis();

        int score = 0;
        int qNum = 1;

        for (Question q : quizQuestions) {
            long elapsed = System.currentTimeMillis() - startTime;
            long remaining = totalTimeMillis - elapsed;
            if (remaining <= 0) {
                System.out.println("\nTime is up! Auto-submitting your exam...");
                break;
            }

            System.out.println("\nTime left: " + (remaining / 1000) + " seconds");
            System.out.println("\nQ" + qNum++ + ": " + q.text);
            System.out.println("A) " + q.optionA);
            System.out.println("B) " + q.optionB);
            System.out.println("C) " + q.optionC);
            System.out.println("D) " + q.optionD);

            System.out.print("Your answer (A/B/C/D): ");
            String ansInput = scanner.nextLine().trim().toUpperCase();
            char ans = ansInput.isEmpty() ? ' ' : ansInput.charAt(0);

            if (ans == q.correctOption) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Wrong. Correct answer: " + q.correctOption);
            }
        }

        int total = quizQuestions.size();
        System.out.println("\n===== QUIZ COMPLETED =====");
        System.out.println("Student: " + student.username);
        System.out.println("Score: " + score + " / " + total);
        double percentage = (total > 0) ? (score * 100.0 / total) : 0;
        System.out.printf("Percentage: %.2f%%\n", percentage);

        if (percentage >= 80) {
            System.out.println("Grade: A");
        } else if (percentage >= 60) {
            System.out.println("Grade: B");
        } else if (percentage >= 40) {
            System.out.println("Grade: C");
        } else {
            System.out.println("Grade: D");
        }
    }

    private static List<Question> loadRandomQuestionsFromDB(int limit) {
        List<Question> list = new ArrayList<>();
        String sql = "SELECT text, option_a, option_b, option_c, option_d, correct_option " +
                     "FROM questions ORDER BY RAND() LIMIT ?";

        try (Connection con = getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {

            ps.setInt(1, limit);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String text = rs.getString("text");
                    String a = rs.getString("option_a");
                    String b = rs.getString("option_b");
                    String c = rs.getString("option_c");
                    String d = rs.getString("option_d");
                    char correct = rs.getString("correct_option").toUpperCase().charAt(0);
                    list.add(new Question(text, a, b, c, d, correct));
                }
            }
        } catch (Exception e) {
            System.out.println("Error loading questions from DB: " + e.getMessage());
        }

        return list;
    }
}