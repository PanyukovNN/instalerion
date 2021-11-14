package org.union.instalerion.service;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.powermock.modules.junit4.PowerMockRunner;
import org.union.common.model.Customer;
import org.union.common.repository.CustomerRepository;
import org.union.common.service.CustomerService;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

/**
 * Unit test of {@link CustomerService}
 */
@RunWith(PowerMockRunner.class)
public class CustomerServiceTest {

    private static final String CUSTOMER_ID = "CUSTOMER_ID";

    @Mock
    private Customer customer;
    @Mock
    private CustomerRepository repository;

    private CustomerService service;

    @Before
    public void setUp() {
        service = new CustomerService(repository);
    }

    @Test
    public void findAll() {
        service.findAll();

        verify(repository, times(1)).findAll();
    }

    @Test
    public void save() {
        service.save(customer);

        verify(repository, times(1)).save(customer);
    }

    @Test
    public void findById() {
        service.findById(CUSTOMER_ID);

        verify(repository, times(1)).findById(CUSTOMER_ID);
    }

    @Test
    public void remove() {
        service.remove(customer);

        verify(repository, times(1)).delete(customer);
    }
}
