package ru.itis.dis403.semestrovka.services;

import ru.itis.dis403.semestrovka.models.Attachment;
import ru.itis.dis403.semestrovka.repositories.AttachmentRepository;

import java.sql.SQLException;
import java.util.List;

public class AttachmentService {
    private AttachmentRepository attachmentRepository = new AttachmentRepository();

    public List<Attachment> findByPostId(Long postId) throws SQLException {
        return attachmentRepository.findByPostId(postId);
    }
}
