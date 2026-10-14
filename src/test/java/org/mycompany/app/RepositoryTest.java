package org.mycompany.app;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class RepositoryTest {
    @Autowired
    private BuddyInfoRepository buddies;
    @Autowired
    private AddressBookRepository addressBooks;
    @Autowired
    private PlatformTransactionManager transactionManager;

    @Test
    @Transactional(propagation = Propagation.NOT_SUPPORTED)
    void savesAndRetrievesAddressBookAcrossTransactions() {
        TransactionTemplate transaction = new TransactionTemplate(transactionManager);
        Long bookId = transaction.execute(status -> {
            BuddyInfo ada = new BuddyInfo("Ada Lovelace", "555-0100");
            ada.setAddress("1125 Colonel By Drive");
            ada = buddies.save(ada);
            BuddyInfo grace = buddies.save(new BuddyInfo("Grace Hopper", "555-0101"));
            assertThat(ada.getId()).isNotNull();
            assertThat(grace.getId()).isNotNull();
            AddressBook book = new AddressBook();
            book.addBuddy(ada);
            book.addBuddy(grace);
            return addressBooks.save(book).getId();
        });

        try {
            transaction.executeWithoutResult(status -> {
                AddressBook loaded = addressBooks.findById(bookId).orElseThrow();
                assertThat(loaded.getBuddies()).containsExactlyInAnyOrder(
                        new BuddyInfo("Ada Lovelace", "555-0100"),
                        new BuddyInfo("Grace Hopper", "555-0101"));
                for (BuddyInfo buddy : loaded.getBuddies()) {
                    assertThat(buddies.findById(buddy.getId())).contains(buddy);
                    if (buddy.getName().equals("Ada Lovelace")) {
                        assertThat(buddy.getAddress()).isEqualTo("1125 Colonel By Drive");
                    }
                }
                assertThat(addressBooks.findAll()).extracting(AddressBook::getId).contains(bookId);
            });
        } finally {
            transaction.executeWithoutResult(status -> {
                addressBooks.deleteAll();
                buddies.deleteAll();
            });
        }
    }
}
