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

    public Service(UserDBRepository repositoryUser, FriendshipDBRepository repositoryFriendship, RequestDBRepository repositoryRequest) {
        this.userRepo = repositoryUser;
        this.friendshipRepo = repositoryFriendship;
        this.requestRepo = repositoryRequest;

        this.userObserver = new ArrayList<>();
        this.friendshipObserver = new ArrayList<>();
        this.requestObserver = new ArrayList<>();

        initializeCounters();
    }

    private void initializeCounters() {
        userRepo.findAll().forEach(user -> userIdCounter = Math.max(userIdCounter, user.getId() + 1));
        friendshipRepo.findAll().forEach(friendship -> friendshipIdCounter = Math.max(friendshipIdCounter, friendship.getId() + 1));
        requestRepo.findAll().forEach(request -> requestIdCounter = Math.max(requestIdCounter, request.getId() + 1));
    }

    public Long getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(Long currentUserId) {
        this.currentUserId = currentUserId;
    }

    public Long getSelectedUserId() {
        return selectedUserId;
    }

    public void setSelectedUserId(Long selectedUserId) {
        this.selectedUserId = selectedUserId;
    }





    // User-related methods
    public Iterable<User> getUsers() {
        return userRepo.findAll();
    }

    public Optional<User> getUserById(Long userId) {
        return userRepo.findOne(userId);
    }

    public Optional<User> addUser(User user) {
        user.setId(userIdCounter++);
        Optional<User> savedUser = userRepo.save(user);
        notifyUserObservers(new UserEvent(EventEnum.ADD, user));
        return savedUser;
    }

    public Optional<User> updateUser(User user) {
        Optional<User> updatedUser = userRepo.update(user);
        updatedUser.ifPresent(u -> notifyUserObservers(new UserEvent(EventEnum.UPDATE, u)));
        return updatedUser;
    }

    public Optional<User> deleteUser(Long userId) {
        deleteFriendshipsOfUser(userId);
        deleteRequestsOfUser(userId);
        Optional<User> deletedUser = userRepo.delete(userId);
        deletedUser.ifPresent(user -> notifyUserObservers(new UserEvent(EventEnum.DELETE, user)));
        return deletedUser;
    }

    public Optional<User> findUserByUsername(String username) {
        for (User user : getUsers()) {
            if (user.getUsername().equals(username)) {
                return Optional.of(user);
            }
        }
        return Optional.empty();
    }





    // Friendship-related methods
    public Iterable<Friendship> getFriendships() {
        return friendshipRepo.findAll();
    }

    public Iterable<Friendship> getFriendshipsOfUser(Long userId) {
        List<Friendship> friendships = new ArrayList<>();
        for (Friendship friendship : getFriendships()) {
            if (friendship.getUser1Id().equals(userId) || friendship.getUser2Id().equals(userId)) {
                friendships.add(friendship);
            }
        }
        return friendships;
    }

    public Optional<Friendship> addFriendship(Long userId1, Long userId2) {
        boolean friendshipExists = false;
        for (Friendship friendship : getFriendships()) {
            if ((friendship.getUser1Id().equals(userId1) && friendship.getUser2Id().equals(userId2)) ||
                    (friendship.getUser1Id().equals(userId2) && friendship.getUser2Id().equals(userId1))) {
                friendshipExists = true;
                break;
            }
        }

        if (friendshipExists) {
            throw new IllegalStateException("A friendship already exists between these users.");
        }

        Friendship friendship = new Friendship(userId1, userId2, LocalDateTime.now());
        friendship.setId(friendshipIdCounter++);
        Optional<Friendship> savedFriendship = friendshipRepo.save(friendship);
        savedFriendship.ifPresent(f -> notifyFriendshipObservers(new FriendshipEvent(EventEnum.ADD, f)));
        return savedFriendship;
    }

    public Optional<Friendship> deleteFriendship(Long friendshipId) {
        Optional<Friendship> deletedFriendship = friendshipRepo.delete(friendshipId);
        deletedFriendship.ifPresent(f -> notifyFriendshipObservers(new FriendshipEvent(EventEnum.DELETE, f)));
        return deletedFriendship;
    }

    public void deleteFriendshipsOfUser(Long userId) {
        getFriendships().forEach(friendship -> {
            if (friendship.getUser1Id().equals(userId) || friendship.getUser2Id().equals(userId)) {
                deleteFriendship(friendship.getId());
            }
        });
    }





    // Request-related methods
    public Iterable<Request> getRequests() {
        return requestRepo.findAll();
    }

    public Iterable<Request> getRequestsByReceiver(Long receiverId) {
        List<Request> requests = new ArrayList<>();
        for (Request request : getRequests()) {
            if (request.getReceiverId().equals(receiverId)) {
                requests.add(request);
            }
        }
        return requests;
    }

    public Iterable<Request> getRequestsToUser(Long userId) {
        List<Request> requests = new ArrayList<>();
        for (Request request : getRequests()) {
            if (request.getSenderId().equals(userId)) {
                requests.add(request);
            }
        }
        return requests;
    }

    public Optional<Request> addRequest(Long senderId, Long receiverId) {
        boolean friendshipExists = false;
        for (Friendship friendship : getFriendships()) {
            if ((friendship.getUser1Id().equals(senderId) && friendship.getUser2Id().equals(receiverId)) ||
                    (friendship.getUser1Id().equals(receiverId) && friendship.getUser2Id().equals(senderId))) {
                friendshipExists = true;
                break;
            }
        }

        if (friendshipExists) {
            throw new IllegalStateException("A friendship already exists between these users.");
        }

        boolean requestExists = false;
        for (Request request : getRequests()) {
            if (request.getSenderId().equals(senderId) && request.getReceiverId().equals(receiverId)) {
                requestExists = true;
                break;
            }
        }

        if (requestExists) {
            throw new IllegalStateException("A request already exists from user " + senderId + " to user " + receiverId);
        }

        Optional<Request> reciprocalRequest = Optional.empty();
        for (Request request : getRequests()) {
            if (request.getSenderId().equals(receiverId) && request.getReceiverId().equals(senderId)) {
                reciprocalRequest = Optional.of(request);
                break;
            }
        }

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

    public Optional<Request> updateRequest(Request request) {
        Optional<Request> updatedRequest = requestRepo.update(request);
        updatedRequest.ifPresent(r -> notifyRequestObservers(new RequestEvent(EventEnum.UPDATE, r)));
        return updatedRequest;
    }

    public Optional<Request> deleteRequest(Long requestId) {
        Optional<Request> deletedRequest = requestRepo.delete(requestId);
        deletedRequest.ifPresent(r -> notifyRequestObservers(new RequestEvent(EventEnum.DELETE, r)));
        return deletedRequest;
    }

    public void deleteRequestsOfUser(Long userId) {
        getRequests().forEach(request -> {
            if (request.getSenderId().equals(userId) || request.getReceiverId().equals(userId)) {
                deleteRequest(request.getId());
            }
        });
    }

    // Observer-related methods for users
    @Override
    public void addUserObserver(Observer<UserEvent> observer) {
        userObserver.add(observer);
    }

    @Override
    public void removeUserObserver(Observer<UserEvent> observer) {
        userObserver.remove(observer);
    }

    @Override
    public void notifyUserObservers(UserEvent event) {
        userObserver.forEach(observer -> observer.update(event));
    }

    // Observer-related methods for friendships
    @Override
    public void addFriendshipObserver(Observer<FriendshipEvent> observer) {
        friendshipObserver.add(observer);
    }

    @Override
    public void removeFriendshipObserver(Observer<FriendshipEvent> observer) {
        friendshipObserver.remove(observer);
    }

    @Override
    public void notifyFriendshipObservers(FriendshipEvent event) {
        friendshipObserver.forEach(observer -> observer.update(event));
    }

    // Observer-related methods for requests
    @Override
    public void addRequestObserver(Observer<RequestEvent> observer) {
        requestObserver.add(observer);
    }

    @Override
    public void removeRequestObserver(Observer<RequestEvent> observer) {
        requestObserver.remove(observer);
    }

    @Override
    public void notifyRequestObservers(RequestEvent event) {
        requestObserver.forEach(observer -> observer.update(event));
    }
}
