package org.mycompany.app;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;

import java.util.ArrayList;
import java.util.List;

@Entity
public class AddressBook {
    private Long id;
    private List<BuddyInfo> buddies;

    public AddressBook(){
        this.buddies = new ArrayList<>();
    }

    @Id
    @GeneratedValue
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void addBuddy(BuddyInfo buddy){
        if (buddy != null){
            this.buddies.add(buddy);
        }
    }

    public BuddyInfo removeBuddy(int index){
        if (index >= 0 && index < this.buddies.size()){
            return this.buddies.remove(index);
        }
        return null;
    }

    @OneToMany(cascade = CascadeType.ALL)
    public List<BuddyInfo> getBuddies(){
        return buddies;
    }

    public void setBuddies(List<BuddyInfo> buddies) {
        this.buddies = buddies;
    }
}
