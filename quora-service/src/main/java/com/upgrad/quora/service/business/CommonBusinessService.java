package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CommonBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserEntity getUserDetails(String id ,String accessToken) throws AuthorizationFailedException, UserNotFoundException {

        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);
        if(userAuthEntity==null){
            throw new AuthorizationFailedException("ATHR-001","User has not signed in");
        }else if(userAuthEntity.getLogoutAt()!=null){
            throw new AuthorizationFailedException("ATHR-002","User is signed out.Sign in first to get user details");
        }else{
            UserEntity userEntity = userDao.getUUID(id);
            if(userEntity == null){
                throw new UserNotFoundException("USR-001","User with entered uuid does not exist");
            }else{
                return userEntity;
            }
        }
    }

}
