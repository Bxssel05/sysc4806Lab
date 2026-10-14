package org.mycompany.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AddressBookTest {

    @Test
    void newAddressBookIsEmpty() {
        AddressBook addressBook = new AddressBook();

        assertEquals(0, addressBook.getBuddies().size());
    }

    @Test
    void addBuddyAddsBuddyToAddressBook() {
        AddressBook addressBook = new AddressBook();
        BuddyInfo buddy = new BuddyInfo("Ada Lovelace", "555-0100");

        addressBook.addBuddy(buddy);

        assertEquals(1, addressBook.getBuddies().size());
        assertSame(buddy, addressBook.getBuddies().get(0));
    }

    @Test
    void containsFindsDifferentBuddyInstanceWithSameNameAndPhoneNumber() {
        AddressBook addressBook = new AddressBook();
        addressBook.addBuddy(new BuddyInfo("Grace Hopper", "555-0101"));

        BuddyInfo matchingBuddy = new BuddyInfo("Grace Hopper", "555-0101");

        assertTrue(addressBook.getBuddies().contains(matchingBuddy));
    }

    @Test
    void addBuddyIgnoresNullBuddy() {
        AddressBook addressBook = new AddressBook();

        addressBook.addBuddy(null);

        assertEquals(0, addressBook.getBuddies().size());
    }

    @Test
    void addBuddyKeepsBuddiesInInsertionOrder() {
        AddressBook addressBook = new AddressBook();
        BuddyInfo firstBuddy = new BuddyInfo("Ada Lovelace", "555-0100");
        BuddyInfo secondBuddy = new BuddyInfo("Grace Hopper", "555-0101");

        addressBook.addBuddy(firstBuddy);
        addressBook.addBuddy(secondBuddy);

        assertEquals(2, addressBook.getBuddies().size());
        assertSame(firstBuddy, addressBook.getBuddies().get(0));
        assertSame(secondBuddy, addressBook.getBuddies().get(1));
    }

    @Test
    void addBuddyAllowsBuddiesWithMatchingDetails() {
        AddressBook addressBook = new AddressBook();

        addressBook.addBuddy(new BuddyInfo("Ada Lovelace", "555-0100"));
        addressBook.addBuddy(new BuddyInfo("Ada Lovelace", "555-0100"));

        assertEquals(2, addressBook.getBuddies().size());
    }

    @Test
    void removeBuddyReturnsAndRemovesBuddyAtValidIndex() {
        AddressBook addressBook = new AddressBook();
        BuddyInfo buddy = new BuddyInfo("Grace Hopper", "555-0101");
        addressBook.addBuddy(buddy);

        BuddyInfo removedBuddy = addressBook.removeBuddy(0);

        assertSame(buddy, removedBuddy);
        assertEquals(0, addressBook.getBuddies().size());
    }

    @Test
    void removeBuddyKeepsOtherBuddiesInAddressBook() {
        AddressBook addressBook = new AddressBook();
        BuddyInfo firstBuddy = new BuddyInfo("Ada Lovelace", "555-0100");
        BuddyInfo secondBuddy = new BuddyInfo("Grace Hopper", "555-0101");
        addressBook.addBuddy(firstBuddy);
        addressBook.addBuddy(secondBuddy);

        BuddyInfo removedBuddy = addressBook.removeBuddy(0);

        assertSame(firstBuddy, removedBuddy);
        assertEquals(1, addressBook.getBuddies().size());
        assertSame(secondBuddy, addressBook.getBuddies().get(0));
    }

    @Test
    void removeBuddyReturnsNullForInvalidIndex() {
        AddressBook addressBook = new AddressBook();

        assertNull(addressBook.removeBuddy(-1));
        assertNull(addressBook.removeBuddy(0));
    }

    @Test
    void removeBuddyReturnsNullForIndexPastLastBuddy() {
        AddressBook addressBook = new AddressBook();
        addressBook.addBuddy(new BuddyInfo("Ada Lovelace", "555-0100"));

        assertNull(addressBook.removeBuddy(1));
        assertEquals(1, addressBook.getBuddies().size());
    }
}
