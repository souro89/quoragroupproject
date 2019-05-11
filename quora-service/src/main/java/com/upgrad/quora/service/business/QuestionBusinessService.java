package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.QuestionDao;
import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.PersistenceContext;
import java.time.ZonedDateTime;
import java.util.List;

@Service
public class QuestionBusinessService {

    @Autowired
    private QuestionDao questionDao;

    @Autowired
    private UserDao userDao;

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity createQuestion(QuestionEntity questionEntity,String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to post a question");
        }else {
             questionEntity.setDate(ZonedDateTime.now());
             questionEntity.setUser(userAuthEntity.getUser());
             return questionDao.createQuestion(questionEntity);
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestions(String accessToken) throws AuthorizationFailedException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions");
        }else {
            return questionDao.getAllQuestions();
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public List<QuestionEntity> getAllQuestionsByUserId(String userId,String accessToken) throws AuthorizationFailedException, UserNotFoundException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get all questions posted by a specific user");
        }else {
            UserEntity userEntity = userDao.getUUID(userId);
            if (userEntity == null){
                throw new UserNotFoundException("USR-001","User with entered uuid whose question details are to be seen does not exist");
            }else{
                return questionDao.getAllQuestionsByUserId(userEntity);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity editQuestion(QuestionEntity editedQuestion,String uuid,String accessToken)
                throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        } else {
            QuestionEntity questionEntity = questionDao.getQuestion(uuid);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            } else if (questionEntity.getUser() == userAuthEntity.getUser()) {
                questionEntity.setContent(editedQuestion.getContent());
                return questionDao.editQuestion(questionEntity);
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner can edit the question");
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRED)
    public QuestionEntity getQuestionByUUID(String uuid,String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to edit the question");
        } else {
            QuestionEntity questionEntity = questionDao.getQuestion(uuid);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "The question with entered uuid whose details are to be seen does not exist");
            } else{
                return questionEntity;
            }

        }
    }


    @Transactional(propagation = Propagation.REQUIRED)
    public String deleteQuestion(String uuid,String accessToken)
            throws AuthorizationFailedException, InvalidQuestionException {
        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if (userAuthEntity == null) {
            throw new AuthorizationFailedException("ATHR-001", "User has not signed in");
        } else if (userAuthEntity.getLogoutAt() != null) {
            throw new AuthorizationFailedException("ATHR-002", "User is signed out.Sign in first to delete a question");
        } else {
            QuestionEntity questionEntity = questionDao.getQuestion(uuid);
            if (questionEntity == null) {
                throw new InvalidQuestionException("QUES-001", "Entered question uuid does not exist");
            } else if (questionEntity.getUser() == userAuthEntity.getUser() || userAuthEntity.getUser().getRole().equals("admin")) {
                questionDao.deleteQuestion(questionEntity);
                return uuid;
            } else {
                throw new AuthorizationFailedException("ATHR-003", "Only the question owner or admin can delete the question");
            }
        }
    }



}
