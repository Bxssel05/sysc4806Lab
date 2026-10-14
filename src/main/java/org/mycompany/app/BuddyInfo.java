package org.mycompany.app;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import java.util.Objects;

@Entity
public class BuddyInfo {
    private Long id;
    private String name;
    private String phoneNumber;

    public BuddyInfo(){

    }

    public BuddyInfo(String name, String phoneNumber){
        this.name = name;
        this.phoneNumber = phoneNumber;
    }

    /*
     * Gets ID of buddy.
     */
    @Id
    @GeneratedValue
    public Long getId(){
        return this.id;
    }

    /*
     * Sets ID of buddy.
     */
    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public String getPhoneNumber(){
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof BuddyInfo)) {
            return false;
        }
        BuddyInfo other = (BuddyInfo) obj;
        return Objects.equals(name, other.name)
                && Objects.equals(phoneNumber, other.phoneNumber);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, phoneNumber);
    }
}
