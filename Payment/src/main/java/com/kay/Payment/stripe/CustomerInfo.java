package com.kay.Payment.stripe;

import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerSearchResult;
import com.stripe.param.CustomerCreateParams;
import com.stripe.param.CustomerSearchParams;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class CustomerInfo {

    public static Customer findCustomerByEmail(String email) {
        Customer result = null;
        try {
            CustomerSearchParams searchParams = CustomerSearchParams
                    .builder()
                    .setQuery("email:'" + email + "'")
                    .build();

            CustomerSearchResult cust = Customer.search(searchParams);
            result =  !cust.getData().isEmpty() ? cust.getData().getFirst() : null;
        } catch (StripeException e) {
            log.info("Stripe Exception: {} ", e.getMessage());
        }
        return result;
    }

    public static Customer findOrCreateNewCustomer(String email, String name){
        Customer customer = null;
        try {
            CustomerSearchParams searchParams = CustomerSearchParams
                    .builder()
                    .setQuery("email:'" + email + "'")
                    .build();

            CustomerSearchResult cust = Customer.search(searchParams);

            if(cust.getData().isEmpty()){
                CustomerCreateParams createParams = CustomerCreateParams
                        .builder()
                        .setName(name)
                        .setEmail(email)
                        .build();

                customer = Customer.create(createParams);
            }else {
                customer = cust.getData().getFirst();
            }
        } catch (StripeException e) {
            log.info("Unable to find or create Customer: {} ", e.getMessage());
        }

        return customer;

    }
}
