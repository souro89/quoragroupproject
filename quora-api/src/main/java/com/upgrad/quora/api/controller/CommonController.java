package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.UserDetailsResponse;
import com.upgrad.quora.service.business.CommonBusinessService;
import com.upgrad.quora.service.entity.UserEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.UserNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.lang.invoke.MethodType;

@Controller
public class CommonController {

    @Autowired
    private CommonBusinessService commonBusinessService;

    @RequestMapping(method = RequestMethod.POST,path="/userprofile/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<UserDetailsResponse> getuserDetails(@PathVariable("userId") String userId,
                                                              @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {
        String bearer[] = authorization.split("Bearer ");
        System.out.println(bearer[1]);
        UserEntity userEntity = commonBusinessService.getUserDetails(userId,bearer[1]);

        UserDetailsResponse userDetailsResponse = new UserDetailsResponse().userName(userEntity.getUsername())
                .aboutMe(userEntity.getAboutMe()).contactNumber(userEntity.getContactNumber())
                .country(userEntity.getCountry()).dob(userEntity.getDob())
                .emailAddress(userEntity.getEmail()).firstName(userEntity.getFirstName())
                .lastName(userEntity.getLastName());

        return new ResponseEntity<UserDetailsResponse>(userDetailsResponse, HttpStatus.OK );

    }

}
