package com.upgrad.FoodOrderingApp.service.businness;

import com.upgrad.FoodOrderingApp.service.dao.CustomerDoa;
import com.upgrad.FoodOrderingApp.service.entity.CustomerEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignupBusinessService{
    @Autowired
    private CustomerDoa customerDoa;
    public CustomerEntity signup(CustomerEntity customerEntity){
        return customerDoa.createCustomer(customerEntity);
    }
}
