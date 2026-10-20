package org.mycompany.app;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AddressBookViewController {
    private final AddressBookRepository addressBooks;

    public AddressBookViewController(AddressBookRepository addressBooks) {
        this.addressBooks = addressBooks;
    }

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("addressBooks", addressBooks.findAll());
        return "index";
    }

    @PostMapping("/addressbooks")
    public String createAddressBook() {
        addressBooks.save(new AddressBook());
        return "redirect:/";
    }

    @GetMapping("/addressbooks/{id}/view")
    @Transactional(readOnly = true)
    public String view(@PathVariable Long id, Model model, jakarta.servlet.http.HttpServletResponse response) {
        AddressBook book = addressBooks.findById(id).orElse(null);
        if (book == null) {
            response.setStatus(HttpStatus.NOT_FOUND.value());
            model.addAttribute("missingId", id);
        } else {
            book.getBuddies().size();
            model.addAttribute("addressBook", book);
        }
        return "addressbook";
    }
}
