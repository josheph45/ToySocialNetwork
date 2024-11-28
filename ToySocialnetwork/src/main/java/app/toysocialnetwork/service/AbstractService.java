package app.toysocialnetwork.service;

import app.toysocialnetwork.domain.Entity;
import app.toysocialnetwork.domain.validators.ValidationException;
import app.toysocialnetwork.repository.AbstractRepository;

import java.util.Optional;

public class AbstractService<ID, E extends Entity<ID>> {
    protected AbstractRepository<ID, E> repo;

    public AbstractService(AbstractRepository<ID, E> repo) {
        this.repo = repo;
    }

    public Optional<E> add(E entity) throws ValidationException {
        return repo.save(entity);
    }

    public Optional<E> delete(ID id) {
        return repo.delete(id);
    }

    public Optional<E> update(E entity) throws ValidationException {
        return repo.update(entity);
    }

    public Optional<E> findOne(ID id) {
        return repo.findOne(id);
    }

    public Iterable<E> findAll() {
        return repo.findAll();
    }
}