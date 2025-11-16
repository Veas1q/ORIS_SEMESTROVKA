package ru.itis.dis403.semestrovka.repositories;

import ru.itis.dis403.semestrovka.models.Attachment;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class AttachmentRepository  {
    private static final String INSERT = "INSERT INTO attachments (post_id, file_size, filename, file_path, mime_type, file_hash) VALUES (?, ?, ?, ?, ?, ?)";
    private static final String SELECT_BY_POST = "SELECT * FROM attachments WHERE post_id = ?";
    private static final String DELETE_BY_POST = "DELETE FROM attachments WHERE post_id = ?";

    public void save(Attachment a) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT)) {
            ps.setLong(1, a.getPostId());
            ps.setInt(2, a.getFileSize());
            ps.setString(3, a.getFilename());
            ps.setString(4, a.getFilePath());
            ps.setString(5, a.getMimeType());
            ps.setString(6, a.getFileHash());
            ps.executeUpdate();
        }
    }

    public List<Attachment> findByPostId(Long postId) throws SQLException {
        List<Attachment> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(SELECT_BY_POST)) {
            ps.setLong(1, postId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    Attachment a = new Attachment();
                    a.setId(rs.getLong("id"));
                    a.setPostId(rs.getLong("post_id"));
                    a.setFileSize(rs.getInt("file_size"));
                    a.setFilename(rs.getString("filename"));
                    a.setFilePath(rs.getString("file_path"));
                    a.setMimeType(rs.getString("mime_type"));
                    a.setFileHash(rs.getString("file_hash"));
                    a.setCreatedAt(rs.getTimestamp("created_at"));
                    list.add(a);
                }
            }
        }
        return list;
    }

    public void deleteByPostId(Long postId) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(DELETE_BY_POST)) {
            ps.setLong(1, postId);
            ps.executeUpdate();
        }
    }
}