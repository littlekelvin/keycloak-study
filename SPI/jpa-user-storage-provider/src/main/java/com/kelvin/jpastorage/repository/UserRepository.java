package com.kelvin.jpastorage.repository;

import com.kelvin.jpastorage.entity.UserEntity;
import com.kelvin.jpastorage.utils.WriteTransactionBlock;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import java.util.List;

@Slf4j
public class UserRepository {
    private EntityManager em;

    public UserRepository(EntityManager em) {
        this.em = em;
    }

    public UserEntity findById(String id) {
        return em.find(UserEntity.class, id);
    }

    public List<UserEntity> findByUserName(String username) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByUsername", UserEntity.class);
        query.setParameter("username", username);
        return query.getResultList();
    }

    public List<UserEntity> findByEmail(String email) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getUserByEmail", UserEntity.class);
        query.setParameter("email", email);
        return query.getResultList();
    }

    public int count() {
        return em.createNamedQuery("getUserCount", Integer.class).getSingleResult();
    }

    public List<UserEntity> findAllUsers(int firstResult, int maxResults) {
        TypedQuery<UserEntity> query = em.createNamedQuery("getAllUsers", UserEntity.class);
        if (firstResult > -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    public List<UserEntity> findUserByUserNameOrEmailLike(String search, int firstResult, int maxResults) {
        TypedQuery<UserEntity> query = em.createNamedQuery("searchForUser", UserEntity.class);
        query.setParameter("search", "%" + search + "%");
        if (firstResult > -1) {
            query.setFirstResult(firstResult);
        }
        if (maxResults > 0) {
            query.setMaxResults(maxResults);
        }
        return query.getResultList();
    }

    public UserEntity save(UserEntity userEntity) {
        return new WriteTransactionBlock<UserEntity>() {

            @Override
            public UserEntity run() {
                em.persist(userEntity);
                return userEntity;
            }
        }.execute(em);
    }

    public boolean deleteById(String userId) {
        return new WriteTransactionBlock<Boolean>() {

            @Override
            public Boolean run() {
                UserEntity userEntity = findById(userId);
                if (null == userEntity) {
                    return false;
                }
                em.remove(userEntity);
                return true;
            }
        }.execute(em);
    }

    public boolean updateUserPassword(String username, String password) {
        return new WriteTransactionBlock<Boolean>() {

            @Override
            public Boolean run() {
                List<UserEntity> userEntities = findByUserName(username);
                if (userEntities.isEmpty()) {
                    return false;
                }
                UserEntity userEntity = userEntities.get(0);
                userEntity.setPassword(password);
                em.merge(userEntity);
                return true;
            }
        }.execute(em);
    }

    public boolean updateUser(UserEntity userEntity) {
        if (StringUtils.isEmpty(userEntity.getId())) {
            return false;
        }
        return new WriteTransactionBlock<Boolean>() {

            @Override
            public Boolean run() {
                UserEntity originEntity = findById(userEntity.getId());
                if (null == originEntity) {
                    return false;
                }
                convertEntity(originEntity, userEntity);
                em.merge(originEntity);
                return true;
            }
        }.execute(em);
    }

    private void convertEntity(UserEntity originEntity, UserEntity userEntity) {
        originEntity.setPhone(userEntity.getPhone());
        originEntity.setEmail(userEntity.getEmail());
        originEntity.setPhone(userEntity.getPhone());
        originEntity.setFirstName(userEntity.getFirstName());
        originEntity.setLastName(userEntity.getLastName());
    }
}
