package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.Order;
import java.util.List;
import java.util.Optional;
import java.util.*;

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

    List<Order> findAll();

    List<Order> findByUserId(Long id);

    Optional<Order> findById(Long id);

    Order findByIdAndUserId(Long orderId, Long userId);

    @Query("SELECT o FROM Order o JOIN o.orderItems oi WHERE oi.customer.id = :customerId")
    List<Order> findOrdersByCustomerId(@Param("customerId") Long customerId);

    // @Query("SELECT o.invoiceUrl FROM Order o") // Fetch user invoices
    // List<String> findAllUserInvoices();

    // @Query("SELECT o.shipmentInvoiceUrl FROM Order o") // Fetch shipment invoices
    // List<String> findAllShipmentInvoices();

}
