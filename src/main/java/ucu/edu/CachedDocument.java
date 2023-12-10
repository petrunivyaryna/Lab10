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
        try (Connection conn = DriverManager.getConnection(DB_URL);
            PreparedStatement pstmt = conn.
                prepareStatement(
                    "SELECT content FROM cache WHERE gcsPath = ?")) {

            pstmt.setString(1, getGcsPath());
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("content");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private void cacheText(String text) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(
                "INSERT INTO cache (gcsPath, content) VALUES (?, ?)")) {

            pstmt.setString(1, getGcsPath());
            pstmt.setString(2, text);
            pstmt.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
