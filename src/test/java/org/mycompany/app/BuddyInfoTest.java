package org.mycompany.app;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BuddyInfoTest {

    @Test
    void constructorSetsNameAndPhoneNumber() {
        BuddyInfo buddy = new BuddyInfo("Ada Lovelace", "555-0100");

        assertEquals("Ada Lovelace", buddy.getName());
        assertEquals("555-0100", buddy.getPhoneNumber());
    }

    @Test
    void setNameUpdatesName() {
        BuddyInfo buddy = new BuddyInfo("Ada Lovelace", "555-0100");

        buddy.setName("Grace Hopper");

        assertEquals("Grace Hopper", buddy.getName());
    }

    @Test
    void setPhoneNumberUpdatesPhoneNumber() {
        BuddyInfo buddy = new BuddyInfo("Ada Lovelace", "555-0100");

        buddy.setPhoneNumber("555-0101");

        assertEquals("555-0101", buddy.getPhoneNumber());
    }
}
