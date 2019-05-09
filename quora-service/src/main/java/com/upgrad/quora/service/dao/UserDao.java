package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;

@Repository
public class UserDao {

    @PersistenceContext
    private EntityManager entityManager;

    public UserEntity createUser(UserEntity userEntity){
        entityManager.persist(userEntity);
        return userEntity;
    }

    public UserAuthEntity createAuthToken(UserAuthEntity userAuthEntity){
        entityManager.persist(userAuthEntity);
        return userAuthEntity;
    }

    public UserEntity getUserName(String username){
        try {
            return entityManager.createNamedQuery("userName", UserEntity.class).setParameter("username", username)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getEmail(String email){
        try {
            return entityManager.createNamedQuery("email", UserEntity.class).setParameter("email", email)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public UserEntity getUUID(String UUID){
        try {
            return entityManager.createNamedQuery("UUID", UserEntity.class).setParameter("UUID", UUID)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public UserAuthEntity validateAccessToken(String accessToken){
        try {
            return entityManager.createNamedQuery("userAuthToken", UserAuthEntity.class).setParameter("accessToken", accessToken)
                    .getSingleResult();
        }catch (NoResultException nre){
            return null;
        }
    }

    public void updateUserAuth(UserAuthEntity updatedUserAuth){
        entityManager.merge(updatedUserAuth);
    }

    public void deleteUser(UserEntity userEntity) {
        entityManager.remove(userEntity);
    }
}
