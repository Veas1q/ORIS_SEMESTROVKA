package ru.itis.dis403.semestrovka.services;

import ru.itis.dis403.semestrovka.models.Post;
import ru.itis.dis403.semestrovka.models.Topic;
import ru.itis.dis403.semestrovka.models.User;
import ru.itis.dis403.semestrovka.repositories.PostRepository;

import java.sql.SQLException;
import java.util.List;

public class PostService {
    private PostRepository postRepository = new PostRepository();

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

    public Post updatePost(Long postId, Long currentUserId) throws SQLException {
        Post post = postRepository.getPostById(postId);
        if (post != null) {
            // Проверяем, что пользователь является автором поста
            if (!post.getUserId().equals(currentUserId)) {
                throw new SecurityException("You can only edit your own posts");
            }

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
}