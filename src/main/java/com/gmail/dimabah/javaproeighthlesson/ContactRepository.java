package com.gmail.dimabah.javaproeighthlesson;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ContactRepository extends JpaRepository<Contact, Long> {

    @Query("SELECT c FROM Contact c WHERE LOWER(c.name) LIKE LOWER(CONCAT('%', :pattern, '%')) OR " +
            "LOWER(c.surname) LIKE LOWER(CONCAT('%', :pattern, '%')) OR " +
            "LOWER(c.phone) LIKE LOWER(CONCAT('%', :pattern, '%')) OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :pattern, '%'))")
    List<Contact> findByPattern(@Param("pattern") String pattern, Pageable pageable);
    List<Contact> findByGroup(Group group, Pageable pageable);
    long countByGroup(Group group);
}
