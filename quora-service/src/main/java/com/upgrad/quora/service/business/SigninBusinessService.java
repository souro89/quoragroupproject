package com.upgrad.quora.service.business;

import com.upgrad.quora.service.dao.UserDao;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Service
public class SigninBusinessService {

    @Autowired
    private UserDao userDao;

    @Autowired
    private PasswordCryptographyProvider passwordCryptographyProvider;

    @Transactional(propagation = Propagation.REQUIRED)
    public UserAuthEntity authenticate(String username,String password) throws AuthenticationFailedException {
        UserEntity userEntity = userDao.getUserName(username);
        if(userEntity==null){
            throw new AuthenticationFailedException("ATH-001","This username does not exist");
        }
        String encryptedPassword = passwordCryptographyProvider.encrypt(password,userEntity.getSalt());
        if(encryptedPassword.equals(userEntity.getPassword())){
            JwtTokenProvider jwtTokenProvider = new JwtTokenProvider(encryptedPassword);
            UserAuthEntity userAuthEntity = new UserAuthEntity();
            userAuthEntity.setUser(userEntity);
            userAuthEntity.setUuid(userEntity.getUuid());
            ZonedDateTime now = ZonedDateTime.now();
            ZonedDateTime expiresAt = now.plusHours(8);
            userAuthEntity.setAccessToken(jwtTokenProvider.generateToken(userEntity.getUuid(),now,expiresAt));
            userAuthEntity.setLoginAt(now);
            userAuthEntity.setLogoutAt(null);
            userAuthEntity.setExpiresAt(expiresAt);
            userDao.createAuthToken(userAuthEntity);
            return userAuthEntity;
        }else{
            throw  new AuthenticationFailedException("ATH-002","Password failed");
        }


    }


}
