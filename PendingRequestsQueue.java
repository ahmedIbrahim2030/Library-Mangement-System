package librarysystem;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.LinkedList;
import java.util.Queue;

public class PendingRequestsQueue {

    private Queue<String> requests;
    private Connection databaseConnection;
        databaseConnection dbConnection = new databaseConnection();

    public PendingRequestsQueue(databaseConnection dbConnection) {
        this.requests = new LinkedList<>();
        this.dbConnection = dbConnection;
    }

    // Enqueue a new request
    public void enqueueRequest(String request, String bookISBN) {
        // Check if the book is available
        boolean isBookAvailable = checkBookAvailability(bookISBN);

        if (isBookAvailable) {
            System.out.println("Book with ISBN " + bookISBN + " is available. Fulfilling request: " + request);
        } else {
            requests.offer(request);
            System.out.println("Request added to the pending queue: " + request);
        }
    }

    // Dequeue a request when the book becomes available
    public String dequeueRequest() {
        if (!requests.isEmpty()) {
            String request = requests.poll();
            System.out.println("Request dequeued: " + request);
            return request;
        } else {
            System.out.println("No pending requests in the queue.");
            return null;
        }
    }

    // Check if a book is available in the database
    private boolean checkBookAvailability(String bookISBN) {
        try {
            String query = "SELECT is_borrowable FROM books WHERE ISBN=?";
            PreparedStatement preparedStatement = databaseConnection.prepareStatement(query);
            preparedStatement.setString(1, bookISBN);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                String isBorrowableString = resultSet.getString("is_borrowable");
                return Boolean.parseBoolean(isBorrowableString);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle the exception appropriately
        }
        return false;
    }
    
    
    
public void insertIntoOrderQueue(String username, String email, String password, String mobile, int bookId,
                                 String title, String author, String category, String ISBN, double price,
                                 LocalDate dateBorrowed, LocalDate dateReturned, long daysBorrowed) {
    String insertQuery = "INSERT INTO orderqueue (username, email, password, mobile, book_id, title, author, category, ISBN, price, date_borrowed, date_returned, days_borrowed) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

    try (Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/librarysystem", "root", "");
         PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
        preparedStatement.setString(1, username);
        preparedStatement.setString(2, email);
        preparedStatement.setString(3, password);
        preparedStatement.setString(4, mobile);
        preparedStatement.setInt(5, bookId);
        preparedStatement.setString(6, title);
        preparedStatement.setString(7, author);
        preparedStatement.setString(8, category);
        preparedStatement.setString(9, ISBN);
        preparedStatement.setDouble(10, price);
        preparedStatement.setDate(11, java.sql.Date.valueOf(dateBorrowed));
        preparedStatement.setDate(12, java.sql.Date.valueOf(dateReturned));
        preparedStatement.setLong(13, daysBorrowed);

        int rowsAffected = preparedStatement.executeUpdate();

        if (rowsAffected > 0) {
            System.out.println("Data inserted into OrderQueue table.");
        } else {
            System.out.println("Failed to insert data into OrderQueue table.");
        }
    } catch (SQLException e) {
        e.printStackTrace();
        // Handle the exception appropriately
    }
}

    // ... (other methods remain unchanged)
}
