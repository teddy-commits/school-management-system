package com.admas.management.modules.finance.repository;

import com.admas.management.modules.finance.model.entity.Invoice;
import com.admas.management.modules.shared.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface InvoiceRepository extends JpaRepository<Invoice, Long> {

    List<Invoice> findByStudent(User student);
    Optional<Invoice> findByInvoiceNumber(String invoiceNumber);
    List<Invoice> findByStatus(String status);
    List<Invoice> findByDueDateBeforeAndStatusNot(LocalDateTime date, String status);

    boolean existsByStudentAndSemesterAndAcademicYear(User student, String semester, Integer academicYear);

    List<Invoice> findByStudentAndSemesterAndAcademicYear(User student, String semester, Integer academicYear);
}