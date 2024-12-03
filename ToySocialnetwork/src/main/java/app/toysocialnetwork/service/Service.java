package app.toysocialnetwork.service;

import app.toysocialnetwork.domain.Friendship;
import app.toysocialnetwork.domain.Request;
import app.toysocialnetwork.domain.User;
import app.toysocialnetwork.repository.database.FriendshipDBRepository;
import app.toysocialnetwork.repository.database.RequestDBRepository;
import app.toysocialnetwork.repository.database.UserDBRepository;
import app.toysocialnetwork.utils.event.EventEnum;
import app.toysocialnetwork.utils.event.FriendshipEvent;
import app.toysocialnetwork.utils.event.RequestEvent;
import app.toysocialnetwork.utils.event.UserEvent;
import app.toysocialnetwork.utils.observer.FriendshipObservable;
import app.toysocialnetwork.utils.observer.Observer;
import app.toysocialnetwork.utils.observer.RequestObservable;
import app.toysocialnetwork.utils.observer.UserObservable;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class Service implements UserObservable, FriendshipObservable, RequestObservable {
    private final UserDBRepository userRepo;
    private final FriendshipDBRepository friendshipRepo;
    private final RequestDBRepository requestRepo;

    private final List<Observer<UserEvent>> userObserver;
    private final List<Observer<FriendshipEvent>> friendshipObserver;
    private final List<Observer<RequestEvent>> requestObserver;

    private Long currentUserId;
    private Long selectedUserId;
    private Long userIdCounter = 1L;
    private Long friendshipIdCounter = 1L;
    private Long requestIdCounter = 1L;

    /**
     * Constructor that creates a new Service
     * @param repositoryUser - the repository for users
     * repositoryUser must not be null
     * @param repositoryFriendship - the repository for friendships
     * repositoryFriendship must not be null
     * @param repositoryRequest - the repository for requests
     * repositoryRequest must not be null
     */
    public Service(UserDBRepository repositoryUser, FriendshipDBRepository repositoryFriendship, RequestDBRepository repositoryRequest) {
        this.userRepo = repositoryUser;
        this.friendshipRepo = repositoryFriendship;
        this.requestRepo = repositoryRequest;

        this.userObserver = new ArrayList<>();
        this.friendshipObserver = new ArrayList<>();
        this.requestObserver = new ArrayList<>();

        initializeCounters();
    }

    /**
     * Initialize the counters for the ids of the entities
     */
    private void initializeCounters() {
        userRepo.findAll().forEach(user -> userIdCounter = Math.max(userIdCounter, user.getId() + 1));
        friendshipRepo.findAll().forEach(friendship -> friendshipIdCounter = Math.max(friendshipIdCounter, friendship.getId() + 1));
        requestRepo.findAll().forEach(request -> requestIdCounter = Math.max(requestIdCounter, request.getId() + 1));
    }

    /**
     * Get the current user id
     * currentUserId must not be null
     */
    public Long getCurrentUserId() {
        return currentUserId;
    }

    /**
     * Get the current user id
     * @param currentUserId - the id of the current user
     * currentUserId must not be null
     */
    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    /**
     * Get the selected user id
     * selectedUserId must not be null
     */
    public Long getSelectedUserId() {
        return selectedUserId;
    }

    /**
     * Set the selected user id
     * @param selectedUserId - the id of the selected user
     * selectedUserId must not be null
     */
    public void setSelectedUserId(Long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }





    // User-related methods

    /**
     * Get all users
     * @return an {@code Iterable} encapsulating all users
     */
    public Iterable<User> getUsers() {
        return userRepo.findAll();
    }

    /**
     * Get the user with the given id
     * @param userId - the id of the user to be returned
     * userId must not be null
     * @return an {@code Optional} encapsulating the user with the given id
     */
    public Optional<User> getUserById(Long userId) {
        return userRepo.findOne(userId);
    }

    /**
     * Add a user
     * @param user - the user to be added
     * user must not be null
     * @return an {@code Optional} encapsulating the added user
     */
    public Optional<User> addUser(User user) {
        user.setId(userIdCounter++);
        Optional<User> savedUser = userRepo.save(user);
        notifyUserObservers(new UserEvent(EventEnum.ADD, user));
        return savedUser;
    }

    /**
     * Update a user
     * @param user - the user to be updated
     * user must not be null
     * @return an {@code Optional} encapsulating the updated user
     */
    public Optional<User> updateUser(User user) {
        Optional<User> updatedUser = userRepo.update(user);
        updatedUser.ifPresent(u -> notifyUserObservers(new UserEvent(EventEnum.UPDATE, u)));
        return updatedUser;
    }

    /**
     * Delete a user
     * @param userId - the id of the user to be deleted
     * userId must not be null
     * @return an {@code Optional} encapsulating the deleted user
     */
    public Optional<User> deleteUser(Long userId) {
        deleteFriendshipsOfUser(userId);
        deleteRequestsOfUser(userId);
        Optional<User> deletedUser = userRepo.delete(userId);
        deletedUser.ifPresent(user -> notifyUserObservers(new UserEvent(EventEnum.DELETE, user)));
        return deletedUser;
    }

    /**
     * Find the user with the given username
     * @param username - the username of the user to be returned
     * username must not be null
     * @return an {@code Optional} encapsulating the user with the given username
     */
    public Optional<User> findUserByUsername(String username) {
        return StreamSupport.stream(getUsers().spliterator(), false)
                .filter(user -> user.getUsername().equals(username))
                .findFirst();
    }





    // Friendship-related methods

    /**
     * Get all friendships
     * @return an {@code Iterable} encapsulating all friendships
     */
    public Iterable<Friendship> getFriendships() {
        return friendshipRepo.findAll();
    }

    /**
     * Get all friendships of a user
     * @param userId - the id of the user whose friendships are to be returned
     * userId must not be null
     * @return an {@code Iterable} encapsulating all friendships of the user
     */
    public Iterable<Friendship> getFriendshipsOfUser(Long userId) {
        return StreamSupport.stream(getFriendships().spliterator(), false)
                .filter(friendship -> friendship.getUser1Id().equals(userId) || friendship.getUser2Id().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Add a friendship
     * @param userId1 - the id of the first user
     * userId1 must not be null
     * @param userId2 - the id of the second user
     * userId2 must not be null
     * @return an {@code Optional} encapsulating the added friendship
     */
    public Optional<Friendship> addFriendship(Long userId1, Long userId2) {
        boolean friendshipExists = StreamSupport.stream(getFriendships().spliterator(), false)
                .anyMatch(friendship -> (friendship.getUser1Id().equals(userId1) && friendship.getUser2Id().equals(userId2)) ||
                        (friendship.getUser1Id().equals(userId2) && friendship.getUser2Id().equals(userId1)));

        if (friendshipExists) {
            throw new IllegalStateException("A friendship already exists between these users.");
        }

        Friendship friendship = new Friendship(userId1, userId2, LocalDateTime.now());
        friendship.setId(friendshipIdCounter++);
        Optional<Friendship> savedFriendship = friendshipRepo.save(friendship);

        savedFriendship.ifPresent(f -> notifyFriendshipObservers(new FriendshipEvent(EventEnum.ADD, f)));

        return savedFriendship;
    }

    /**
     * Update a friendship
     * @param friendshipId - the id of the friendship to be updated
     * friendshipId must not be null
     * @return an {@code Optional} encapsulating the updated friendship
     */
    public Optional<Friendship> deleteFriendship(Long friendshipId) {
        Optional<Friendship> deletedFriendship = friendshipRepo.delete(friendshipId);
        deletedFriendship.ifPresent(f -> notifyFriendshipObservers(new FriendshipEvent(EventEnum.DELETE, f)));
        return deletedFriendship;
    }

    /**
     * Delete all friendships of a user
     * @param userId - the id of the user whose friendships are to be deleted
     * userId must not be null
     */
    public void deleteFriendshipsOfUser(Long userId) {
        getFriendships().forEach(friendship -> {
            if (friendship.getUser1Id().equals(userId) || friendship.getUser2Id().equals(userId)) {
                deleteFriendship(friendship.getId());
            }
        });
    }





    // Request-related methods

    /**
     * Get all requests
     * @return an {@code Iterable} encapsulating all requests
     */
    public Iterable<Request> getRequests() {
        return requestRepo.findAll();
    }

    /**
     * Get all requests received by a user
     * @param receiverId - the id of the user who received the requests
     * receiverId must not be null
     * @return an {@code Iterable} encapsulating all requests received by the user
     */
    public Iterable<Request> getRequestsByReceiver(Long receiverId) {
        return StreamSupport.stream(getRequests().spliterator(), false)
                .filter(request -> request.getReceiverId().equals(receiverId))
                .collect(Collectors.toList());
    }

    /**
     * Get all requests sent by a user
     * @param userId - the id of the user who sent the requests
     * userId must not be null
     * @return an {@code Iterable} encapsulating all requests sent by the user
     */
    public Iterable<Request> getRequestsToUser(Long userId) {
        return StreamSupport.stream(getRequests().spliterator(), false)
                .filter(request -> request.getSenderId().equals(userId))
                .collect(Collectors.toList());
    }

    /**
     * Add a request
     * @param senderId - the id of the user who sent the request
     * senderId must not be null
     * @param receiverId - the id of the user who received the request
     * receiverId must not be null
     * @return an {@code Optional} encapsulating the added request
     */
    public Optional<Request> addRequest(Long senderId, Long receiverId) {
        boolean friendshipExists = StreamSupport.stream(getFriendships().spliterator(), false)
                .anyMatch(friendship -> (friendship.getUser1Id().equals(senderId) && friendship.getUser2Id().equals(receiverId)) ||
                        (friendship.getUser1Id().equals(receiverId) && friendship.getUser2Id().equals(senderId)));

        if (friendshipExists) {
            throw new IllegalStateException("A friendship already exists between these users.");
        }

        boolean requestExists = StreamSupport.stream(getRequests().spliterator(), false)
                .anyMatch(request -> request.getSenderId().equals(senderId) && request.getReceiverId().equals(receiverId));

        if (requestExists) {
            throw new IllegalStateException("A request already exists from user " + senderId + " to user " + receiverId);
        }

        Optional<Request> reciprocalRequest = StreamSupport.stream(getRequests().spliterator(), false)
                .filter(request -> request.getSenderId().equals(receiverId) && request.getReceiverId().equals(senderId))
                .findFirst();

        if (reciprocalRequest.isPresent()) {
            addFriendship(senderId, receiverId);
            requestRepo.delete(reciprocalRequest.get().getId());
            notifyRequestObservers(new RequestEvent(EventEnum.DELETE, reciprocalRequest.get()));
            return Optional.empty();
        }

        Request request = new Request(senderId, receiverId);
        request.setId(requestIdCounter++);
        Optional<Request> savedRequest = requestRepo.save(request);
        savedRequest.ifPresent(r -> notifyRequestObservers(new RequestEvent(EventEnum.ADD, r)));
        return savedRequest;
    }

    /**
     * Update a request
     * @param request - the request to be updated
     * request must not be null
     * @return an {@code Optional} encapsulating the updated request
     */
    public Optional<Request> updateRequest(Request request) {
        Optional<Request> updatedRequest = requestRepo.update(request);
        updatedRequest.ifPresent(r -> notifyRequestObservers(new RequestEvent(EventEnum.UPDATE, r)));
        return updatedRequest;
    }

    /**
     * Delete a request
     * @param requestId - the id of the request to be deleted
     * requestId must not be null
     * @return an {@code Optional} encapsulating the deleted request
     */
    public Optional<Request> deleteRequest(Long requestId) {
        Optional<Request> deletedRequest = requestRepo.delete(requestId);
        deletedRequest.ifPresent(r -> notifyRequestObservers(new RequestEvent(EventEnum.DELETE, r)));
        return deletedRequest;
    }

    /**
     * Delete all requests of a user
     * @param userId - the id of the user whose requests are to be deleted
     * userId must not be null
     */
    public void deleteRequestsOfUser(Long userId) {
        getRequests().forEach(request -> {
            if (request.getSenderId().equals(userId) || request.getReceiverId().equals(userId)) {
                deleteRequest(request.getId());
            }
        });
    }

    // Observer-related methods for users

    /**
     * Add an observer for users
     * @param observer - the observer to be added
     * observer must not be null
     */
    @Override
    public void addUserObserver(Observer<UserEvent> observer) {
        userObserver.add(observer);
    }

    /**
     * Remove an observer for users
     * @param observer - the observer to be removed
     * observer must not be null
     */
    @Override
    public void removeUserObserver(Observer<UserEvent> observer) {
        userObserver.remove(observer);
    }

    /**
     * Notify all user observers
     * @param event - the event to be sent to the observers
     * event must not be null
     */
    @Override
    public void notifyUserObservers(UserEvent event) {
        userObserver.forEach(observer -> observer.update(event));
    }

    // Observer-related methods for friendships

    /**
     * Add an observer for friendships
     * @param observer - the observer to be added
     * observer must not be null
     */
    @Override
    public void addFriendshipObserver(Observer<FriendshipEvent> observer) {
        friendshipObserver.add(observer);
    }

    /**
     * Remove an observer for friendships
     * @param observer - the observer to be removed
     * observer must not be null
     */
    @Override
    public void removeFriendshipObserver(Observer<FriendshipEvent> observer) {
        friendshipObserver.remove(observer);
    }

    /**
     * Notify all friendship observers
     * @param event - the event to be sent to the observers
     * event must not be null
     */
    @Override
    public void notifyFriendshipObservers(FriendshipEvent event) {
        friendshipObserver.forEach(observer -> observer.update(event));
    }

    // Observer-related methods for requests

    /**
     * Add an observer for requests
     * @param observer - the observer to be added
     * observer must not be null
     */
    @Override
    public void addRequestObserver(Observer<RequestEvent> observer) {
        requestObserver.add(observer);
    }

    /**
     * Remove an observer for requests
     * @param observer - the observer to be removed
     * observer must not be null
     */
    @Override
    public void removeRequestObserver(Observer<RequestEvent> observer) {
        requestObserver.remove(observer);
    }

    /**
     * Notify all request observers
     * @param event - the event to be sent to the observers
     * event must not be null
     */
    @Override
    public void notifyRequestObservers(RequestEvent event) {
        requestObserver.forEach(observer -> observer.update(event));
    }
}
