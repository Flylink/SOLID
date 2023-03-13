package org.example.sales;

import org.example.customer.CustomerService;

public interface SalesRegistrationService {

    void runSalesMenu(CustomerService customerService);

    void recordSale(Basket basket);

}
