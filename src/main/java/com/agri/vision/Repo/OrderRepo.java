package com.agri.vision.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.agri.vision.Model.Order;
<<<<<<< HEAD
import java.util.List;
import java.util.Optional;
=======
import java.util.*;
>>>>>>> 585740887e5f96ec3be9e63fca9b05e8a93f87ef

@Repository
public interface OrderRepo extends JpaRepository<Order, Long> {

<<<<<<< HEAD
    List<Order> findAll();

    List<Order> findByUserId(Long id);

    Optional<Order> findById(Long id);

    Order findByIdAndUserId(Long orderId, Long userId);

=======
    // @Query("SELECT o.invoiceUrl FROM Order o") // Fetch user invoices
    // List<String> findAllUserInvoices();

    // @Query("SELECT o.shipmentInvoiceUrl FROM Order o") // Fetch shipment invoices
    // List<String> findAllShipmentInvoices();
>>>>>>> 585740887e5f96ec3be9e63fca9b05e8a93f87ef
}
