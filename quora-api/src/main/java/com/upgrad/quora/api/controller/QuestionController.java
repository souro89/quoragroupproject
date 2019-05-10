package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
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

import javax.print.attribute.standard.Media;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Controller
public class QuestionController {

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST,path = "/question/create",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionResponse> createQuestion(@RequestHeader("authorization") String authorization, QuestionRequest questionRequest) throws AuthorizationFailedException, UserNotFoundException {

        String bearer[] = authorization.split("Bearer ");
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionRequest.getContent());
        questionEntity.setUuid(UUID.randomUUID().toString());
        QuestionEntity createdQuestion = questionBusinessService.createQuestion(questionEntity,bearer[1]);
        QuestionResponse questionResponse = new QuestionResponse().id(createdQuestion.getUuid()).status("QUESTION CREATED");
        return new ResponseEntity<QuestionResponse>(questionResponse, HttpStatus.CREATED);
    }

    @RequestMapping(method = RequestMethod.GET,path = "/question/all",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestions(@RequestHeader("authorization") String authorization) throws AuthorizationFailedException {
        String bearer[]= authorization.split("Bearer ");
        List<QuestionEntity> questions = questionBusinessService.getAllQuestions(bearer[1]);

        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();
        for(QuestionEntity questionEntity : questions){
            questionDetailsResponses.add(new QuestionDetailsResponse()
                        .id(questionEntity.getUuid()).content(questionEntity.getContent()));
        }
        return  new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.GET,path = "question/all/{userId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<QuestionDetailsResponse>> getAllQuestionsByUserId(@PathVariable("userId") String userId,
                                    @RequestHeader("authorization") String authorization) throws AuthorizationFailedException, UserNotFoundException {
        String bearer[]= authorization.split("Bearer ");
        List<QuestionEntity> questions = questionBusinessService.getAllQuestionsByUserId(userId,bearer[1]);

        List<QuestionDetailsResponse> questionDetailsResponses = new ArrayList<>();
        for(QuestionEntity questionEntity : questions){
            questionDetailsResponses.add(new QuestionDetailsResponse()
                    .id(questionEntity.getUuid()).content(questionEntity.getContent()));
        }
        return  new ResponseEntity<List<QuestionDetailsResponse>>(questionDetailsResponses,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.PUT,path = "/question/edit/{questionId}",consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionEditResponse> editQuestion(@PathVariable("questionId") String questionId,
                                                             @RequestHeader("authorization") String authorization,
                                                             QuestionEditRequest questionEditRequest) throws AuthorizationFailedException, InvalidQuestionException {
        String bearer[] = authorization.split("Bearer ");
        QuestionEntity questionEntity = new QuestionEntity();
        questionEntity.setContent(questionEditRequest.getContent());
        QuestionEntity editedQuestion = questionBusinessService.editQuestion(questionEntity,questionId,bearer[1]);
        QuestionEditResponse questionEditResponse = new QuestionEditResponse().id(editedQuestion.getUuid()).status("QUESTION EDITED");
        return new ResponseEntity<QuestionEditResponse>(questionEditResponse,HttpStatus.OK);
    }

    @RequestMapping(method = RequestMethod.DELETE,path = "/question/delete/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<QuestionDeleteResponse> editQuestion(@PathVariable("questionId") String questionId,
                                                             @RequestHeader("authorization") String authorization
                                                             ) throws AuthorizationFailedException, InvalidQuestionException {
        String bearer[] = authorization.split("Bearer ");
        String uuid = questionBusinessService.deleteQuestion(questionId,bearer[1]);
        QuestionDeleteResponse questiondeleteResponse = new QuestionDeleteResponse().id(uuid).status("QUESTION DELETED");
        return new ResponseEntity<QuestionDeleteResponse>(questiondeleteResponse,HttpStatus.OK );
    }

}
