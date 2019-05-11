package com.upgrad.quora.api.controller;

import com.upgrad.quora.api.model.*;
import com.upgrad.quora.service.business.AnswerBusinessService;
import com.upgrad.quora.service.business.QuestionBusinessService;
import com.upgrad.quora.service.entity.AnswerEntity;
import com.upgrad.quora.service.entity.QuestionEntity;
import com.upgrad.quora.service.exception.AnswerNotFoundException;
import com.upgrad.quora.service.exception.AuthorizationFailedException;
import com.upgrad.quora.service.exception.InvalidQuestionException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.ArrayList;
import java.util.List;

@Controller
public class AnswerController {

    @Autowired
    private AnswerBusinessService answerBusinessService;

    @Autowired
    private QuestionBusinessService questionBusinessService;

    @RequestMapping(method = RequestMethod.POST,path = "/question/{questionId}/answer/create", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerResponse> createAnswer(@PathVariable("questionId") String questionId,
                                                       @RequestHeader("authorization") String authorization,
                                                       AnswerRequest answerRequest) throws AuthorizationFailedException, InvalidQuestionException {
        String bearer[] = authorization.split("Bearer ");
        AnswerEntity answerEntity = new AnswerEntity();
        answerEntity.setAns(answerRequest.getAnswer());
        AnswerEntity createdAnswer = answerBusinessService.createAnswer(questionId,answerEntity,bearer[1]);
        AnswerResponse answerResponse = new AnswerResponse().id(createdAnswer.getUuid()).status("ANSWER CREATED");
        return new ResponseEntity<AnswerResponse>(answerResponse, HttpStatus.CREATED);

    }

    @RequestMapping(method = RequestMethod.PUT,path = "/answer/edit/{answerId}", consumes = MediaType.APPLICATION_JSON_UTF8_VALUE,produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerEditResponse> editAnswer(@PathVariable("answerId") String answerId,
                                                           @RequestHeader("authorization") String authorization,
                                                           AnswerEditRequest answerEditRequest) throws AuthorizationFailedException, InvalidQuestionException {
        String bearer[] = authorization.split("Bearer ");
        AnswerEntity editedAnswer = answerBusinessService.editAnswer(answerId,answerEditRequest.getContent(),bearer[1]);
        AnswerEditResponse answerEditResponse = new AnswerEditResponse().id(editedAnswer.getUuid()).status("ANSWER EDITED");
        return new ResponseEntity<AnswerEditResponse>(answerEditResponse, HttpStatus.OK);

    }

    @RequestMapping(method = RequestMethod.DELETE,path = "/answer/delete/{answerId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<AnswerDeleteResponse> deleteAnswer(@PathVariable("answerId") String answerId,
                                                             @RequestHeader("authorization") String authorization
                                                             ) throws AuthorizationFailedException, AnswerNotFoundException {
        String bearer[] = authorization.split("Bearer ");
        String deletedAnswer = answerBusinessService.deleteAnswer(answerId,bearer[1]);
        AnswerDeleteResponse answerDeleteResponse = new AnswerDeleteResponse().id(deletedAnswer).status("ANSWER DELETED");
        return new ResponseEntity<AnswerDeleteResponse>(answerDeleteResponse, HttpStatus.OK);

    }

    //Below endpoint returns a List of answers along with question as a JSON output
    @RequestMapping(method = RequestMethod.GET,path = "answer/all/{questionId}",produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<List<AnswerDetailsResponse>> getAllAnswersToQuestion(@PathVariable("questionId") String questionId,
                                                             @RequestHeader("authorization") String authorization
    ) throws AuthorizationFailedException, InvalidQuestionException {
        String bearer[] = authorization.split("Bearer ");
        QuestionEntity questionEntity = questionBusinessService.getQuestionByUUID(questionId,bearer[1]);
        List<AnswerEntity> answerEntities = answerBusinessService.getAnswerByQuestion(questionEntity,bearer[1]);
        List<AnswerDetailsResponse> answerDetailsResponses = new ArrayList<>();
        for(AnswerEntity answerEntity : answerEntities){
            answerDetailsResponses.add(new AnswerDetailsResponse()
                                        .questionContent(questionEntity.getContent()).id(answerEntity.getUuid())
                                        .answerContent(answerEntity.getAns()));
        }
        return new ResponseEntity<List<AnswerDetailsResponse>>(answerDetailsResponses,HttpStatus.OK);
    }


}
