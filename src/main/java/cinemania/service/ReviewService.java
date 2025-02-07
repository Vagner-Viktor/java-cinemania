package cinemania.service;

import cinemania.exception.NotFoundException;
import cinemania.model.EventType;
import cinemania.model.OperationType;
import cinemania.model.Review;
import cinemania.model.UserFeed;
import cinemania.storage.FilmStorage;
import cinemania.storage.ReviewStorage;
import cinemania.storage.UsabilityStateStorage;
import cinemania.storage.UserFeedStorage;
import cinemania.storage.UserStorage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReviewService {

    private final ReviewStorage reviewStorage;
    private final UserStorage userStorage;
    private final FilmStorage filmStorage;
    private final UserFeedStorage userFeedStorage;
    private final UsabilityStateStorage usabilityStateStorage;


    private static final String NOT_FOUND_REVIEW_MESSAGE = "Ревью с таким id не существует.";
    private static final String NOT_FOUND_USER_MESSAGE = "Пользователь с таким id не существует.";
    private static final String NOT_FOUND_FILM_MESSAGE = "Фильм с таким id не существует.";

    public Review createReview(Review review) {
        if (!userStorage.isUserExists(review.getUserId()))
            throw new NotFoundException(NOT_FOUND_USER_MESSAGE);
        if (!filmStorage.isFilmExists(review.getFilmId()))
            throw new NotFoundException(NOT_FOUND_FILM_MESSAGE);
        long id = reviewStorage.createReview(review);
        review.setReviewId(id);
        userFeedStorage.create(UserFeed.builder()
                .eventId(null)
                .userId(review.getUserId())
                .entityId(id)
                .timestamp(Instant.now())
                .eventType(EventType.REVIEW.name())
                .operation(OperationType.ADD.name())
                .build());
        return review;
    }

    public Review updateReview(Review review) {
        if (!reviewStorage.isReviewExists(review.getReviewId()))
            throw new NotFoundException(NOT_FOUND_REVIEW_MESSAGE);
        if (!userStorage.isUserExists(review.getUserId()))
            throw new NotFoundException(NOT_FOUND_USER_MESSAGE);
        if (!filmStorage.isFilmExists(review.getFilmId()))
            throw new NotFoundException(NOT_FOUND_FILM_MESSAGE);
        reviewStorage.updateReview(review);
        review = getReview(review.getReviewId());
        userFeedStorage.create(UserFeed.builder()
                .eventId(null)
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .timestamp(Instant.now())
                .eventType(EventType.REVIEW.name())
                .operation(OperationType.UPDATE.name())
                .build());
        return review;
    }

    public boolean deleteReview(Long reviewId) {
        if (!reviewStorage.isReviewExists(reviewId))
            throw new NotFoundException(NOT_FOUND_REVIEW_MESSAGE);
        Review review = getReview(reviewId);
        userFeedStorage.create(UserFeed.builder()
                .eventId(null)
                .userId(review.getUserId())
                .entityId(review.getReviewId())
                .timestamp(Instant.now())
                .eventType(EventType.REVIEW.name())
                .operation(OperationType.REMOVE.name())
                .build());
        return reviewStorage.deleteReview(reviewId);
    }

    public Review getReview(Long reviewId) {
        if (!reviewStorage.isReviewExists(reviewId))
            throw new NotFoundException(NOT_FOUND_REVIEW_MESSAGE);
        return reviewStorage.getReview(reviewId).orElse(null);
    }

    public List<Review> getReviews(Long filmId, Integer count) {
        if (count == null || count <= 0) count = 10;
        if (filmId != null) {
            if (!filmStorage.isFilmExists(filmId))
                throw new NotFoundException(NOT_FOUND_FILM_MESSAGE);
            return reviewStorage.getReviewsForFilm(filmId, count);
        }
        return reviewStorage.getNReviewsForEachFilm(count);
    }

    public Review likeReview(Long reviewId, Long userId) {
        if (!reviewStorage.isReviewExists(reviewId))
            throw new NotFoundException(NOT_FOUND_REVIEW_MESSAGE);
        if (!userStorage.isUserExists(userId))
            throw new NotFoundException(NOT_FOUND_USER_MESSAGE);
        Integer state = usabilityStateStorage.getCurrentState(reviewId, userId).orElse(0);
        if (state == 0) {
            reviewStorage.setLike(reviewId, userId);
        } else if (state == -1) {
            reviewStorage.updateLike(reviewId, userId);
        }
        return getReview(reviewId);
    }

    public Review dislikeReview(Long reviewId, Long userId) {
        if (!reviewStorage.isReviewExists(reviewId))
            throw new NotFoundException(NOT_FOUND_REVIEW_MESSAGE);
        if (!userStorage.isUserExists(userId))
            throw new NotFoundException(NOT_FOUND_USER_MESSAGE);
        Integer state = usabilityStateStorage.getCurrentState(reviewId, userId).orElse(0);
        if (state == 0) {
            reviewStorage.setDislike(reviewId, userId);
        } else if (state == 1) {
            reviewStorage.updateDislike(reviewId, userId);
        }
        return getReview(reviewId);
    }

    public Review deleteLike(Long reviewId, Long userId) {
        if (!reviewStorage.isReviewExists(reviewId))
            throw new NotFoundException(NOT_FOUND_REVIEW_MESSAGE);
        if (!userStorage.isUserExists(userId))
            throw new NotFoundException(NOT_FOUND_USER_MESSAGE);
        reviewStorage.removeLike(reviewId, userId);
        return getReview(reviewId);
    }

    public Review deleteDislike(Long reviewId, Long userId) {
        if (!reviewStorage.isReviewExists(reviewId))
            throw new NotFoundException(NOT_FOUND_REVIEW_MESSAGE);
        if (!userStorage.isUserExists(userId))
            throw new NotFoundException(NOT_FOUND_USER_MESSAGE);
        reviewStorage.removeDislike(reviewId, userId);
        return getReview(reviewId);
    }
}
