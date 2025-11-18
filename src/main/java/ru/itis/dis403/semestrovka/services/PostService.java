package ru.itis.dis403.semestrovka.services;

import jakarta.servlet.ServletContext;
import ru.itis.dis403.semestrovka.models.Post;
import ru.itis.dis403.semestrovka.repositories.PostRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.sql.SQLException;
import java.util.List;

public class PostService {
    private final PostRepository postRepository;
    private ServletContext servletContext;

    public PostService(PostRepository postRepo) {
        this.postRepository = postRepo;
    }

    // Сеттер
    public void setServletContext(ServletContext servletContext) {
        this.servletContext = servletContext;
    }

    public List<Post> getPostsByTopicId(Long topicId) throws SQLException {
        return postRepository.getAllPostFromTopic(topicId);
    }


    public List<Post> getPostsByUserId(Long userId) throws SQLException {
        return postRepository.getAllPostsByUserId(userId);
    }

    public Post getPostById(Long id) throws SQLException {
        Post post = postRepository.getPostById(id);
        if (post != null) {
            return post;
        }
        throw new IllegalArgumentException("Post not found");
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

    public void deleteReactionsFromPost(Long postId) {
        postRepository.deleteReactionsFromPost(postId);
    }

    public void deleteReactionsByCategoryId (Long categoryId) {
        postRepository.deleteReactionsFromPost(categoryId);
    }



    public void deletePost(Long id) throws SQLException {
        Post post = postRepository.getPostById(id);
        if (post != null) {
            postRepository.deletePost(id);
        } else {
            throw new IllegalArgumentException("Post not found");
        }
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

    public void deleteReactionsByTopicId(Long topicId)  {
        List<Post> posts = postRepository.getAllPostFromTopic(topicId);
        for (Post post : posts) {
            postRepository.deleteReactionsByPostId(post.getId());
        }
    }

}