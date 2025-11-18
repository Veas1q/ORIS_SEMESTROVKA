package ru.itis.dis403.semestrovka.services;

import ru.itis.dis403.semestrovka.models.Topic;
import ru.itis.dis403.semestrovka.repositories.TopicRepository;

import java.sql.SQLException;
import java.util.List;

public class TopicService {
    private final TopicRepository topicRepository;

    public TopicService(TopicRepository topicRepository) {
        this.topicRepository = topicRepository;
    }

    public List<Topic> getAllTopics() throws SQLException {
        return topicRepository.findAll();
    }

    public List<Topic> getTopicsByCategoryId(Long categoryId) throws SQLException {
        return topicRepository.findByCategoryId(categoryId);
    }

    public Topic getTopicById(Long id) throws SQLException {
        Topic topic = topicRepository.findById(id);
        if (topic != null) {
            return topic;
        }
        throw new IllegalArgumentException("Topic not found");
    }

    public List<Topic> getRecentTopics(int limit) throws SQLException {
        return topicRepository.findRecent(limit);
    }

    public List<Topic> getPinnedTopics() throws SQLException {
        return topicRepository.findPinned();
    }

    public List<Topic> searchTopicsByTitle(String title) throws SQLException {
        return topicRepository.searchTopicsByTitle(title);
    }

    public List<Topic> getTopicsByUserId(Long userId) throws SQLException {
        return topicRepository.findByUserId(userId);
    }

    public void deleteTopicsByCategoryId (Long categoryId) throws SQLException {
        topicRepository.deleteTopicsByCategoryId(categoryId);
    }

    public Topic createTopic(Topic topic) throws SQLException {
        topicRepository.addTopic(topic);
        return topic;
    }

    public Topic updateTopic(Topic topic) throws SQLException {
        Topic existingTopic = topicRepository.findById(topic.getId());
        if (existingTopic != null) {
            topicRepository.updateTopic(topic);
            return topic;
        }
        throw new IllegalArgumentException("Topic not found");
    }

    public void deleteTopic(Long id) throws SQLException {
        Topic topic = topicRepository.findById(id);
        if (topic != null) {
            topicRepository.deleteTopic(id);
        } else {
            throw new IllegalArgumentException("Topic not found");
        }
    }

    public void incrementViewCount(Long topicId) throws SQLException {
        Topic topic = topicRepository.findById(topicId);
        if (topic != null) {
            topicRepository.updateViewCount(topicId);
        } else {
            throw new IllegalArgumentException("Topic not found");
        }
    }



    public void togglePinTopic(Long topicId, boolean pinned, Long pinnedByUserId) throws SQLException {
        Topic topic = topicRepository.findById(topicId);
        if (topic != null) {
            topicRepository.togglePinned(topicId, pinned, pinnedByUserId);
        } else {
            throw new IllegalArgumentException("Topic not found");
        }
    }

    public void toggleCloseTopic(Long topicId, boolean closed, Long closedByUserId) throws SQLException {
        Topic topic = topicRepository.findById(topicId);
        if (topic != null) {
            topicRepository.toggleClosed(topicId, closed, closedByUserId);
        } else {
            throw new IllegalArgumentException("Topic not found");
        }
    }
}