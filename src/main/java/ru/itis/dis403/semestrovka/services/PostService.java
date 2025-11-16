package ru.itis.dis403.semestrovka.services;

import jakarta.servlet.ServletContext;
import jakarta.servlet.http.Part;
import ru.itis.dis403.semestrovka.models.Attachment;
import ru.itis.dis403.semestrovka.models.Post;
import ru.itis.dis403.semestrovka.models.Topic;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.repositories.AttachmentRepository;
import ru.itis.dis403.semestrovka.repositories.PostRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.List;

public class PostService {
    private PostRepository postRepository = new PostRepository();
    private AttachmentRepository attachmentRepository = new AttachmentRepository();
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024; // 10 МБ
    private static final String UPLOAD_DIR = "/uploads";private ServletContext servletContext;  // ← ДОБАВЬ ПОЛЕ

    // Сеттер
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public List<Post> getPostsByTopicId(Long topicId) throws SQLException {
        return postRepository.getAllPostFromTopic(topicId);
    }

    public Post getPostById(Long id) throws SQLException {
        Post post = postRepository.getPostById(id);
        if (post != null) {
            return post;
        }
        throw new IllegalArgumentException("Post not found");
    }

    public Post getFirstPostInTopic(Long topicId) throws SQLException {
        Post post = postRepository.getFirstPostInTopic(topicId);
        if (post != null) {
            return post;
        }
        throw new IllegalArgumentException("First post not found in topic");
    }

    public Post createPost(Post post) throws SQLException {
        postRepository.addPost(post);
        return post;
    }

    public Post updatePost(Long postId, Long currentUserId, String newText) throws SQLException {
        Post post = postRepository.getPostById(postId);
        if (post != null) {
            // Проверяем, что пользователь является автором поста
            if (!post.getUserId().equals(currentUserId)) {
                throw new SecurityException("You can only edit your own posts");
            }
            post.setPostText(newText);

            postRepository.updatePost(post);
            return post;
        }
        throw new IllegalArgumentException("Post not found");
    }

    public void deletePost(Long id, User currentUser) throws SQLException {
        Post post = postRepository.getPostById(id);

        if (post != null) {
            boolean canDelete = post.getUserId().equals(currentUser.getId()) ||
                    "ADMIN".equals(currentUser.getRole()) ||
                    "MODERATOR".equals(currentUser.getRole());

            if (!canDelete) {
                throw new SecurityException("You don't have permission to delete this post");
            }

            attachmentRepository.deleteByPostId(post.getId());

            // 2. Удаляем файлы с диска
            List<Attachment> atts = attachmentRepository.findByPostId(post.getId());
            for (Attachment a : atts) {
                ServletContext servletContext = null;
                File file = new File(servletContext.getRealPath(a.getFilePath()));
                if (file.exists()) file.delete();
            }

            postRepository.deletePost(id);
        } else {
            throw new IllegalArgumentException("Post not found");
        }
    }

    public void setPostPinStatus(Long postId, Long userId, String userRole, boolean pinStatus) throws SQLException {
        Post post = postRepository.getPostById(postId);
        if (post != null) {
            TopicService topicService = new TopicService();
            Topic topic = topicService.getTopicById(post.getTopicId());

            boolean canModifyPin = topic.getUserId().equals(userId) ||
                    "ADMIN".equals(userRole) ||
                    "MODERATOR".equals(userRole);

            if (!canModifyPin) {
                throw new SecurityException("You don't have permission to modify pin status in this topic");
            }

            if (pinStatus) {
                 postRepository.setPostPinned(postId, true, userId);
            } else {
                 postRepository.setPostPinned(postId, false, null);
            }

        } else {
            throw new IllegalArgumentException("Post not found");
        }
    }



    public Post getPinnedPostInTopic(Long topicId) throws SQLException {
        List<Post> posts = postRepository.getAllPostFromTopic(topicId);
        return posts.stream()
                .filter(Post::getPinnedInTopic) // Предполагая, что есть метод isPinnedInTopic()
                .findFirst()
                .orElse(null);
    }

    public int getPostCountInTopic(Long topicId) throws SQLException {
        List<Post> posts = postRepository.getAllPostFromTopic(topicId);
        return posts.size();
    }

    public void toggleLike(Long postId, Long userId) throws SQLException {
        postRepository.toggleLike(postId, userId);
    }

    public int getLikesCount(Long postId) throws SQLException {
        return postRepository.getLikesCount(postId);
    }

    public boolean isLiked(Long postId, Long userId) throws SQLException {
        return postRepository.isLikedByUser(postId, userId);
    }

    public void toggleReaction(Long postId, Long userId, String reactionType) throws SQLException {
        postRepository.toggleReaction(postId, userId, reactionType);
    }

    public int getReactionCount(Long postId, String reactionType) throws SQLException {
        return postRepository.getReactionCount(postId, reactionType);
    }

    public boolean isReaction(Long postId, Long userId, String reactionType) throws SQLException {
        return postRepository.isReaction(postId, userId, reactionType);
    }

    public void deletePostsByTopicId(Long topicId) throws SQLException {
        postRepository.deletePostsByTopicId(topicId);
    }

    public void deleteReactionsByPostId(Long topicId)  {
        List<Post> posts = postRepository.getAllPostFromTopic(topicId);
        for (Post post : posts) {
            postRepository.deleteReactionsByPostId(post.getId());
        }
    }
    public void createPostWithFiles(Post post, List<Part> fileParts) throws SQLException, IOException {
        postRepository.addPost(post);
        Long postId = post.getId();

        // ПРАВИЛЬНЫЙ ПУТЬ — через ServletContext
        String realPath = servletContext.getRealPath(UPLOAD_DIR);
        File dir = new File(realPath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                throw new IOException("Не удалось создать папку: " + realPath);
            }
        }

        if (fileParts != null && !fileParts.isEmpty()) {
            for (Part part : fileParts) {
                if (part.getSize() == 0) continue;
                if (part.getSize() > MAX_FILE_SIZE) {
                    throw new IllegalArgumentException("Файл слишком большой");
                }

                String origName = part.getSubmittedFileName();
                if (origName == null || origName.isEmpty()) continue;

                String fileName = System.currentTimeMillis() + "_" + origName;
                String filePath = UPLOAD_DIR + "/" + fileName;  // /uploads/...

                File targetFile = new File(dir, fileName);

                try (var in = part.getInputStream()) {
                    Files.copy(in, targetFile.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
                }

                System.out.println("Сохраняем в: " + targetFile.getAbsolutePath());
                System.out.println("Существует: " + targetFile.exists());

                String hash = calculateSHA256(targetFile);

                Attachment att = new Attachment();
                att.setPostId(postId);
                att.setFileSize((int) part.getSize());
                att.setFilename(origName);
                att.setFilePath(filePath);  // /uploads/...
                att.setMimeType(part.getContentType());
                att.setFileHash(hash);
                attachmentRepository.save(att);
            }
        }
    }
    public static String calculateSHA256(File file) throws IOException {
        try {
            byte[] data = Files.readAllBytes(file.toPath());
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(data);

            StringBuilder sb = new StringBuilder();
            for (byte b : hash) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (Exception e) {
            throw new IOException("Ошибка вычисления хэша", e);
        }
    }
}