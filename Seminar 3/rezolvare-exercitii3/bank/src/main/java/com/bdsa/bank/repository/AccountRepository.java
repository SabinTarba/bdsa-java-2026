package com.bdsa.bank.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import com.bdsa.bank.entity.Account;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    // Spring Data genereaza SQL din numele metodei
    Optional<Account> findByIban(String iban);

    List<Account> findByStatus(String status);

    List<Account> findByOwnerNameContainingIgnoreCase(String name);

    // JPQL — query pe obiecte Java, nu pe tabele SQL
    @Query("SELECT a FROM Account a WHERE a.balance > :minBalance AND a.status = 'ACTIVE'")
    List<Account> findActiveWithBalanceAbove(@Param("minBalance") Double minBalance);

    // Native SQL — pentru query-uri specifice Oracle
    @Query(value = "SELECT *\n" +
            "FROM (\n" +
            "    SELECT *\n" +
            "    FROM accounts\n" +
            "    ORDER BY balance DESC\n" +
            ")\n" +
            "WHERE ROWNUM <= :limit", nativeQuery = true)
    List<Account> findTopByBalance(@Param("limit") int limit);
}