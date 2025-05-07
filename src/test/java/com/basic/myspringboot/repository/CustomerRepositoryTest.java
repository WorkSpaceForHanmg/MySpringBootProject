package com.basic.myspringboot.repository;

import com.basic.myspringboot.entity.Customer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
//@DataJpaTest
class CustomerRepositoryTest {
    @Autowired
    CustomerRepository customerRepository;

    @Test
    @Rollback(value = false)
    void testDeleteCustomer(){
        Customer customer = customerRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));
        customerRepository.delete(customer);
    }


    @Test
    @Rollback(value = false)
    void testUpdateCustomer(){
        Customer customer = customerRepository.findById(1L)
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));
        //수정하려면 Entity의 Setter method를 호출한다.
        //
        customer.setCustomerName("SpringBoot");
        //customerRepository.save(customer);        //dirty read : save를 호출하지 않고도 DB에 저장되었다. Why? @Transactional 때문에
        assertThat(customer.getCustomerName()).isEqualTo("SpringBoot");
    }


    @Test
    void testByNotFoundException(){
        //<X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier)
        //Supplier 의 추상메서드 T get()
        Customer customer = customerRepository.findByCustomerId("A004")
                .orElseThrow(() -> new RuntimeException("Customer Not Found"));
//        assertThat(customer.getCustomerId()).isEqualTo("A001");
    }


    @Test
    void testFindBy() {
        Optional<Customer> optionalCustomer = customerRepository.findById(1L);
//        assertThat(optionalCustomer).isNotEmpty();
        if (optionalCustomer.isPresent()) {
            Customer existCustomer = optionalCustomer.get();
            assertThat(existCustomer.getId()).isEqualTo(1L);
        }
        //Optional 의 T orElseGet(Supplier<? extneds T> supplier)
        //Supplier 의 추상메서드 T get()
        //고객 번호가 존재하는 경우
        Optional<Customer> optionalCustomer2 = customerRepository.findByCustomerId("A001");
        Customer a001customer = optionalCustomer.orElseGet(() -> new Customer());
        assertThat(a001customer.getCustomerName()).isEqualTo("스프링");

        //고객 번호가 존재하지 않는 경우
        Customer notFoundCustomer = customerRepository.findByCustomerId("A004")
                .orElseGet(() -> new Customer());
        assertThat(notFoundCustomer.getCustomerName()).isNull();
    }
    @Test
    @Rollback(value = false) //Rollback 처리하지 마라
    @Disabled
    void testCreateCustomer() {
        //Given (준비 단계)
        Customer customer = new Customer();
        customer.setCustomerId("A002");
        customer.setCustomerName("스프링2");
        //When (실행 단계)
        Customer addCustomer = customerRepository.save(customer);
        //Then (검증 단계)
        assertThat(addCustomer).isNotNull();
        assertThat(addCustomer.getCustomerName()).isEqualTo("스프링2");
    }
}