package com.example.minierp.application.customer;

import com.example.minierp.domain.customer.*;
import com.example.minierp.domain.sales.OrderPaidEvent;
import com.example.minierp.domain.shared.DomainEventPublisher;
import com.example.minierp.interfaces.rest.customer.*;
import com.example.minierp.domain.common.exceptions.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CustomerService {

    private final CustomerRepository repository;
    private final DomainEventPublisher eventPublisher;


    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        String customerNumber = "CUST-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        Customer customer = Customer.builder()
                .customerNumber(customerNumber)
                .economicCode(request.economicCode())
                .name(request.name())
                .type(request.type())
                .contactPerson(request.contactPerson())
                .phone(request.phone())
                .email(request.email())
                .billingAddress(request.billingAddress())
                .shippingAddress(request.shippingAddress())
                .creditLimit(request.creditLimit())
                .active(true)
                .build();

        Customer saved = repository.save(customer);

        eventPublisher.publish(new CustomerCreatedEvent(saved));

        return mapToResponse(saved);
    }

    @Transactional
    public CustomerResponse update(Long id, UpdateCustomerRequest request) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "Customer"));

        customer.setName(request.name());
        customer.setEconomicCode(request.economicCode());
        customer.setType(request.type());
        customer.setContactPerson(request.contactPerson());
        customer.setPhone(request.phone());
        customer.setEmail(request.email());
        customer.setBillingAddress(request.billingAddress());
        customer.setShippingAddress(request.shippingAddress());
        customer.setCreditLimit(request.creditLimit());
        customer.setActive(request.active());

        Customer saved = repository.save(customer);

        eventPublisher.publish(new CustomerUpdatedEvent(id,saved));

        return mapToResponse(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse findById(Long id) {
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "Customer"));
        return mapToResponse(customer);
    }

    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAll(Boolean active, Pageable pageable) {
        Page<Customer> page;
        if (active != null) {
            page = repository.findAllByActive(active, pageable);
        } else {
            page = repository.findAll(pageable);
        }
        return page.map(this::mapToResponse);
    }
    @Transactional(readOnly = true)
    public Page<CustomerResponse> findAllDeleted(Pageable pageable) {
        Page<Customer> page;
        page = repository.findAllDeleted(pageable);
        return page.map(this::mapToResponse);
    }

    @Transactional
    public void delete(Long id) { //soft delete
        Customer customer = repository.findById(id)
                .orElseThrow(() -> new NotFoundException(id, "مشتری "));

        customer.setActive(false);
        customer.setDeletedAt(LocalDateTime.now());
        repository.save(customer);

        eventPublisher.publish(new CustomerDeletedEvent(id));

    }

    private CustomerResponse mapToResponse(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getCustomerNumber(),
                customer.getEconomicCode(),
                customer.getName(),
                customer.getType(),
                customer.getContactPerson(),
                customer.getPhone(),
                customer.getEmail(),
                customer.getBillingAddress(),
                customer.getShippingAddress(),
                customer.getCreditLimit(),
                customer.getActive(),
                customer.getCreatedAt(),
                customer.getUpdatedAt(),
                customer.getCreatedBy(),
                customer.getLastModifiedBy()
        );
    }

    @Transactional
    public CustomerResponse restore(Long customerId) {
        Customer customer = repository.findById(customerId)
                .orElseThrow(() -> new NotFoundException(customerId, "مشتری "));

        customer.setDeletedAt(null);
        repository.save(customer);

        return mapToResponse(customer);
    }
}
