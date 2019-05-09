package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SignOutBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity signout(String accessToken) throws SignOutRestrictedException {

        UserAuthEntity userAuthEntity = userDao.validateAccessToken(accessToken);

        if(userAuthEntity==null){
            throw new SignOutRestrictedException("SGR-001","User is not Signed in");
        }else{
            userAuthEntity.setLogoutAt(ZonedDateTime.now());
            userDao.updateUserAuth(userAuthEntity);
            return userAuthEntity;
        }

    }

}
