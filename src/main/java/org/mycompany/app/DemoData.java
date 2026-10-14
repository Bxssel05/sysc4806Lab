package org.mycompany.app;

import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@Profile("demo")
public class DemoData implements CommandLineRunner {
    private final BuddyInfoRepository buddies;
    private final AddressBookRepository addressBooks;

    public DemoData(BuddyInfoRepository buddies, AddressBookRepository addressBooks) {
        this.buddies = buddies;
        this.addressBooks = addressBooks;
    }

    @Override
    @Transactional
    public void run(String... args) {
        BuddyInfo ada = buddies.save(new BuddyInfo("Ada Lovelace", "555-0100"));
        BuddyInfo grace = buddies.save(new BuddyInfo("Grace Hopper", "555-0101"));

        AddressBook addressBook = new AddressBook();
        addressBook.addBuddy(ada);
        addressBook.addBuddy(grace);
        addressBooks.save(addressBook);

        System.out.println("Address books retrieved through Spring Data repositories:");
        for (AddressBook book : addressBooks.findAll()) {
            System.out.println("Address book (id=" + book.getId() + "):");
            for (BuddyInfo buddy : book.getBuddies()) {
                System.out.println("  " + buddy.getName() + " (id=" + buddy.getId()
                        + ", phone=" + buddy.getPhoneNumber() + ")");
            }
        }
    }
}
