package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class QuestionDao {

    @PersistenceContext
    private EntityManager entityManager;

    public QuestionEntity createQuestion(QuestionEntity questionEntity){
        entityManager.persist(questionEntity);
        return questionEntity;
    }

    public List<QuestionEntity> getAllQuestions(){
        return entityManager.createNamedQuery("question",QuestionEntity.class).getResultList();
    }

    public List<QuestionEntity> getAllQuestionsByUserId(UserEntity userEntity){
        return entityManager.createNamedQuery("getQuestionByUserId",QuestionEntity.class).setParameter("user",userEntity)
                .getResultList();
    }

    public QuestionEntity getQuestion(String uuid){
        try {
            return entityManager.createNamedQuery("getQuestion", QuestionEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nxe){
            return null;
        }
    }

    public QuestionEntity editQuestion(QuestionEntity questionEntity){
        return entityManager.merge(questionEntity);
    }

    public void deleteQuestion(QuestionEntity questionEntity){
        entityManager.remove(questionEntity);
    }

}
