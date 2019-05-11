package com.upgrad.quora.service.dao;

import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import org.springframework.stereotype.Repository;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import java.util.List;

@Repository
public class AnswerDao {

    @PersistenceContext
    private EntityManager entityManager;

    public AnswerEntity createAnswer(AnswerEntity answerEntity){
        entityManager.persist(answerEntity);
        return answerEntity;
    }

    public AnswerEntity editAnswer(AnswerEntity answerEntity){
        entityManager.merge(answerEntity);
        return answerEntity;
    }

    public void deleteAnswer(AnswerEntity answerEntity){
        entityManager.remove(answerEntity);
    }

    public AnswerEntity getAnswer(String uuid){
        try {
            return entityManager.createNamedQuery("answerByUUID", AnswerEntity.class).setParameter("uuid", uuid)
                    .getSingleResult();
        } catch (NoResultException nxe){
            return null;
        }
    }

    public List<AnswerEntity> getAnswerByQuestion(QuestionEntity questionEntity){
        try {
            return entityManager.createNamedQuery("answerByQuestion", AnswerEntity.class).setParameter("qe", questionEntity)
                    .getResultList();
        } catch (NoResultException nxe){
            return null;
        }
    }

}
