package com.upgrad.quora.api.controller;


import com.upgrad.quora.api.model.SigninResponse;
import com.upgrad.quora.api.model.SignoutResponse;
import com.upgrad.quora.api.model.SignupUserRequest;
import com.upgrad.quora.api.model.SignupUserResponse;
import com.upgrad.quora.service.business.SignOutBusinessService;
import com.upgrad.quora.service.business.SignUpBusinessService;
import com.upgrad.quora.service.business.SigninBusinessService;
import com.upgrad.quora.service.entity.UserAuthEntity;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthenticationFailedException;
import com.upgrad.quora.service.exception.SignOutRestrictedException;
import com.upgrad.quora.service.exception.SignUpRestrictedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.Base64;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private SignUpBusinessService signUpBusinessService;

    @Autowired
    private SigninBusinessService signinBusinessService;

    @Autowired
    private SignOutBusinessService signOutBusinessService;

    @RequestMapping(method = RequestMethod.POST,path="/user/signup",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupUserResponse> signup(final SignupUserRequest signupUserRequest) throws SignUpRestrictedException {

        UserEntity userEntity = new UserEntity();

        userEntity.setUuid(UUID.randomUUID().toString());
        userEntity.setFirstName(signupUserRequest.getFirstName());
        userEntity.setLastName(signupUserRequest.getLastName());
        userEntity.setUsername(signupUserRequest.getUserName());
        userEntity.setPassword(signupUserRequest.getPassword());
        userEntity.setEmail(signupUserRequest.getEmailAddress());
        userEntity.setSalt("quora123");
        userEntity.setCountry(signupUserRequest.getCountry());
        userEntity.setAboutMe(signupUserRequest.getAboutMe());
        userEntity.setDob(signupUserRequest.getDob());
        userEntity.setContactNumber(signupUserRequest.getContactNumber());
        userEntity.setRole("nonadmin");

        UserEntity creaUserEntity = signUpBusinessService.createUser(userEntity);
        SignupUserResponse signupUserResponse = new SignupUserResponse().id(creaUserEntity.getUuid()).status("REGISTERED");

        return new ResponseEntity<SignupUserResponse>(signupUserResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.POST,path="/user/signin",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SigninResponse> signin(@RequestHeader("authorization") String authorization  ) throws AuthenticationFailedException {

        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText =  new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthEntity authenticate = signinBusinessService.authenticate(decodedArray[0], decodedArray[1]);
        UserEntity userEntity = authenticate.getUser();

        SigninResponse signinResponse = new SigninResponse().id(userEntity.getUuid())
                .message("SIGNED IN SUCCESSFULLY");

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("access-token",authenticate.getAccessToken());
        return new ResponseEntity<SigninResponse>(signinResponse,httpHeaders, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.POST,path = "/user/signout", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignoutResponse> signout(@RequestHeader("authorization") String authorization) throws SignOutRestrictedException {

        String [] bearerToken = authorization.split("Bearer ");
        UserAuthEntity userAuthEntity = signOutBusinessService.signout(bearerToken[1]);
        SignoutResponse signoutResponse = new SignoutResponse().id(userAuthEntity.getUuid()).message("SIGNED OUT SUCCESSFULLY");
        return new ResponseEntity<SignoutResponse>(signoutResponse, HttpStatus.OK );
    }

}
