package org.ostech.abcbank.repositories;

import org.ostech.abcbank.enums.AccountType;
import org.ostech.abcbank.models.Transaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long>, JpaSpecificationExecutor<Transaction> {

    @Query("SELECT t FROM Transaction t JOIN FETCH t.account a " +
        "WHERE (:accountType IS NULL OR a.accountType = :accountType)")
    List<Transaction> findTransactionsWithAccount(@Param("accountType") AccountType accountType);
}
