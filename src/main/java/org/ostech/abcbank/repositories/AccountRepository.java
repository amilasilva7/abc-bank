package org.ostech.abcbank.repositories;

import org.ostech.abcbank.models.Account;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;


@Repository
public interface AccountRepository extends JpaRepository<Account, Long> {

    boolean existsByAccountNumber(String accountNumber);

    @Query(value = "SELECT NEXT VALUE FOR account_seq", nativeQuery = true)
    Long nextSequenceValue();
}
