package org.mycompany.app;

import java.net.URI;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping(value = "/api", produces = MediaType.APPLICATION_JSON_VALUE)
public class AddressBookRestController {
    private final AddressBookRepository addressBooks;
    private final BuddyInfoRepository buddies;

    public AddressBookRestController(AddressBookRepository addressBooks, BuddyInfoRepository buddies) {
        this.addressBooks = addressBooks;
        this.buddies = buddies;
    }

    @PostMapping(value = "/addressbooks", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<AddressBook> createAddressBook(@RequestBody Map<String, Object> request) {
        AddressBook book = addressBooks.save(new AddressBook());
        return ResponseEntity.created(URI.create("/api/addressbooks/" + book.getId())).body(book);
    }

    public record BuddyRequest(String name, String phoneNumber) {}

    @PostMapping(value = "/buddies", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<BuddyInfo> createBuddy(@RequestBody BuddyRequest request) {
        BuddyInfo buddy = buddies.save(new BuddyInfo(request.name(), request.phoneNumber()));
        return ResponseEntity.created(URI.create("/api/buddies/" + buddy.getId())).body(buddy);
    }

    @PostMapping("/addressbooks/{addressBookId}/buddies/{buddyId}")
    @Transactional
    public AddressBook addBuddy(@PathVariable Long addressBookId, @PathVariable Long buddyId) {
        AddressBook book = findBook(addressBookId);
        BuddyInfo buddy = buddies.findById(buddyId).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Buddy not found"));
        if (book.getBuddies().stream().noneMatch(existing -> existing.getId().equals(buddyId))) {
            book.addBuddy(buddy);
        }
        return addressBooks.save(book);
    }

    @GetMapping("/addressbooks/{addressBookId}")
    @Transactional(readOnly = true)
    public AddressBook getAddressBook(@PathVariable Long addressBookId) {
        AddressBook book = findBook(addressBookId);
        book.getBuddies().size();
        return book;
    }

    private AddressBook findBook(Long id) {
        return addressBooks.findById(id).orElseThrow(() ->
                new ResponseStatusException(HttpStatus.NOT_FOUND, "Address book not found"));
    }
}
