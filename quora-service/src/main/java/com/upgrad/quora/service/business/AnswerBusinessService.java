package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.AnswerDao;
import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.UUID;

@Service
public class AnswerBusinessService {

    @Autowired
    private AnswerDao answerDao;

    @Autowired
    private UserDao userDao;

    @Autowired
    private QuestionDao questionDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity createAnswer(String questionId,AnswerEntity answerEntity,String accessToken)
                    throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to post an answer");
        } else {
            QuestionEntity questionEntity = questionDao.getQuestion(questionId);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "The question entered is invalid");
            } else {
                answerEntity.setDate(ZonedDateTime.now());
                answerEntity.setUuid(UUID.randomUUID().toString());
                answerEntity.setQe(questionEntity);
                answerEntity.setUser(userAuthEntity.getUser());
                answerDao.createAnswer(answerEntity);
                return answerEntity;
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public AnswerEntity editAnswer(String answerId,String answerContent,String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit an answer");
        } else {
            AnswerEntity answerEntity = answerDao.getAnswer(answerId);
            if (answerEntity == null) {
                throw new InvalidQuestionException("ANS-001", "Entered answer uuid does not exist");
            } else if (answerEntity.getUser() == userAuthEntity.getUser()) {
                answerEntity.setAns(answerContent);
                return answerDao.editAnswer(answerEntity);
            }else{
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner can edit the answer");
            }
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteAnswer(String uuid,String accessToken)
            throws AuthorizationFailedException, AnswerNotFoundException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete an answer");
        } else {
            AnswerEntity answerEntity = answerDao.getAnswer(uuid);
            if (answerEntity == null) {
                throw new AnswerNotFoundException("ANS-001", "Entered answer uuid does not exist");
            } else if (answerEntity.getUser() == userAuthEntity.getUser() || userAuthEntity.getUser().getRole().equals("admin")) {
                answerDao.deleteAnswer(answerEntity);
                return uuid;
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the answer owner or admin can delete the answer");
            }
        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public List<AnswerEntity> getAnswerByQuestion(QuestionEntity questionEntity,String accessToken)
            throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to get the answers");
        } else {
            List<AnswerEntity> answerEntities = answerDao.getAnswerByQuestion(questionEntity);
            return answerEntities;
        }
    }


}
