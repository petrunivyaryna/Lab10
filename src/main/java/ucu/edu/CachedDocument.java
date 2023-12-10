package ucu.edu;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CachedDocument extends SmartDocument {
    private static final String DB_URL = "jdbc:sqlite:database.db";

    public CachedDocument(String gcsPath) {
        super(gcsPath);
    }

    @Override
    public String parse() {
        String cachedText = getCachedText();
        if (cachedText != null) {
            return cachedText;
        } else {
            String parsedText = super.parse();
            cacheText(parsedText);
            return parsedText;
        }
    }

    private String getCachedText() {
        try (Connection CONN = DriverManager.getConnection(DB_URL);
            PreparedStatement PSTMT = CONN.
                prepareStatement(
                    "SELECT content FROM cache WHERE gcsPath = ?")) {

            PSTMT.setString(1, getGcsPath());
            try (ResultSet RS = PSTMT.executeQuery()) {
                if (RS.next()) {
                    return RS.getString("content");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cacheText(String text) {
        try (Connection CONN = DriverManager.getConnection(DB_URL);
             PreparedStatement PSTMT = CONN.prepareStatement(
                "INSERT INTO cache (gcsPath, content) VALUES (?, ?)")) {

            PSTMT.setString(1, getGcsPath());
            PSTMT.setString(2, text);
            PSTMT.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
