package com.upgrad.FoodOrderingApp.api.controller;

import com.upgrad.FoodOrderingApp.api.model.LoginResponse;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerRequest;
import com.upgrad.FoodOrderingApp.api.model.SignupCustomerResponse;
import com.upgrad.FoodOrderingApp.service.businness.AuthenticationService;
import com.upgrad.FoodOrderingApp.service.businness.SignupBusinessService;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import com.upgrad.FoodOrderingApp.service.entity.UserAuthTokenEntity;
import com.upgrad.FoodOrderingApp.service.exception.AuthenticationFailedException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.ZonedDateTime;
import java.util.Base64;
import java.util.UUID;

@RestController
@RequestMapping("/")
public class CustomerController {
    @Autowired
    private SignupBusinessService signupBusinessService;
    @RequestMapping(method = RequestMethod.POST, path="/customer/signup", consumes= MediaType.APPLICATION_JSON_UTF8_VALUE, produces=MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<SignupCustomerResponse> signup(SignupCustomerRequest signupCustomerRequest)
    {
    final CustomerEntity customerEntity = new CustomerEntity();
    customerEntity.setUuid(UUID.randomUUID().toString());
    customerEntity.setFirstName(signupCustomerRequest.getFirstName());
    customerEntity.setLastName(signupCustomerRequest.getLastName());
    customerEntity.setEmail(signupCustomerRequest.getEmailAddress());
    customerEntity.setMobilePhone(signupCustomerRequest.getContactNumber());
    customerEntity.setPassword(signupCustomerRequest.getPassword());
    customerEntity.setSalt("1234abc");
    customerEntity.setStatus(4);
    customerEntity.setCreatedAt(ZonedDateTime.now());
    customerEntity.setCreatedBy("api-backend");


    final CustomerEntity createdEntity = signupBusinessService.signup(customerEntity);
    SignupCustomerResponse customerResponse = new SignupCustomerResponse().id(createdEntity.getUuid()).status("REGISTERD");
    return new ResponseEntity(customerResponse,HttpStatus.CREATED);

   }
    @Autowired
    private AuthenticationService authenticationService;

    @RequestMapping(method = RequestMethod.POST, path = "/customer/login", produces = MediaType.APPLICATION_JSON_UTF8_VALUE)
    public ResponseEntity<LoginResponse> login(@RequestHeader("authorization") final String authorization) throws AuthenticationFailedException {


        byte[] decode = Base64.getDecoder().decode(authorization.split("Basic ")[1]);
        String decodedText = new String(decode);
        String[] decodedArray = decodedText.split(":");

        UserAuthTokenEntity userAuthToken = authenticationService.authenticate(decodedArray[0], decodedArray[1]);
        CustomerEntity user = userAuthToken.getUser();

        LoginResponse loginResponse = new LoginResponse().id(UUID.fromString(user.getUuid()))
                .firstName(user.getFirstName()).lastName(user.getLastName())
                .emailAddress(user.getEmail()).contactNumber(user.getMobilePhone())


        HttpHeaders headers = new HttpHeaders();
        headers.add("access-token", userAuthToken.getAccessToken());
        return new ResponseEntity<LoginResponse>(loginResponse, headers, HttpStatus.OK);
    }
}
