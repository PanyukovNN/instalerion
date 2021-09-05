package org.union.instalerion.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.union.common.exception.NotFoundException;
import org.union.common.model.Customer;
import org.union.common.service.CustomerService;

import java.util.List;

import static org.union.common.Constants.CUSTOMER_NOT_FOUND_ERROR_MSG;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
public class UserController {

    private final CustomerService customerService;

    @GetMapping("/all")
    public List<Customer> getAllUsers() {
        return customerService.findAll();
    }

    @PostMapping("/create")
    public String postCreateUser(@RequestParam String username,
                                 @RequestParam String password) {
        Customer customer = new Customer();
        customer.setUsername(username);
        customer.setPassword(password);

        customerService.save(customer);

        return String.format("User \"%s\" successfully saved.", username);
    }

    @PostMapping("/remove")
    public String postRemoveCustomer(@RequestParam String userId) {
        Customer customer = customerService.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format(CUSTOMER_NOT_FOUND_ERROR_MSG, userId)));

        customerService.remove(customer);

        return String.format("User \"%s\" successfully removed", customer.getUsername());
    }
}
