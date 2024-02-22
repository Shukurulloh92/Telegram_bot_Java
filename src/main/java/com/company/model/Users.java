package com.company.model;

import com.company.enums.Role;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.Objects;

@AllArgsConstructor
@NoArgsConstructor
@Data

public class Users {

    public Integer id = null;
    public Long userId = null;
    public String firstName = null;
    public String lastName = null;
    public Role role;
    public String phoneNumber;

    public String currentQuery = null;
    public String currentSubject = null;
    public Integer currenTestNumber = null;
    public ArrayList<String> theyAnswer = new ArrayList<>();
    public Integer score = null;

    public Users(Long userId, String firstName, String lastName, Role role, String phoneNumber) {
        this.userId = userId;
        this.firstName = firstName;
        this.lastName = lastName;
        this.role = role;
        this.phoneNumber = phoneNumber;
    }

    public static Users users(Message message, Contact contact) {
        return new Users(message.getFrom().getId(), message.getFrom().getFirstName(),
                message.getFrom().getLastName(), Role.USER, contact.getPhoneNumber());
    }

    @Override
    public String toString() {
        return "Users{" +
                "userId=" + userId +
                ", firstName='" + firstName + '\'' +
//                ", lastName='" + lastName + '\'' +
//                ", role=" + role +
//                ", phoneNumber='" + phoneNumber + '\'' +
                ", currentQuery='" + currentQuery + '\'' +
                ", currentSubject='" + currentSubject + '\'' +
                ", currenTestNumber=" + currenTestNumber +
                ", theyAnswer=" + theyAnswer +
                ", score=" + score +
                '}';
    }

//    @Override
//    public boolean equals(Object o) {
//        if (this == o) return true;
//        if (!(o instanceof Users)) return false;
//        Users users = (Users) o;
//        return getId().equals(users.getId()) && getUserId().equals(users.getUserId()) && getFirstName().equals(users.getFirstName()) && getLastName().equals(users.getLastName()) && getRole() == users.getRole() && getPhoneNumber().equals(users.getPhoneNumber()) && getCurrentQuery().equals(users.getCurrentQuery()) && getCurrentSubject().equals(users.getCurrentSubject()) && getCurrenTestNumber().equals(users.getCurrenTestNumber()) && getTheyAnswer().equals(users.getTheyAnswer()) && getScore().equals(users.getScore());
//    }
//
//    @Override
//    public int hashCode() {
//        return Objects.hash(getId(), getUserId(), getFirstName(), getLastName(), getRole(), getPhoneNumber(), getCurrentQuery(), getCurrentSubject(), getCurrenTestNumber(), getTheyAnswer(), getScore());
//    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Users)) return false;
        Users users = (Users) o;
        return Objects.equals(getId(), users.getId()) && Objects.equals(getUserId(), users.getUserId()) && Objects.equals(getFirstName(), users.getFirstName()) && Objects.equals(getLastName(), users.getLastName()) && getRole() == users.getRole() && Objects.equals(getPhoneNumber(), users.getPhoneNumber()) && Objects.equals(getCurrentQuery(), users.getCurrentQuery()) && Objects.equals(getCurrentSubject(), users.getCurrentSubject()) && Objects.equals(getCurrenTestNumber(), users.getCurrenTestNumber()) && Objects.equals(getTheyAnswer(), users.getTheyAnswer()) && Objects.equals(getScore(), users.getScore());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId(), getUserId(), getFirstName(), getLastName(), getRole(), getPhoneNumber(), getCurrentQuery(), getCurrentSubject(), getCurrenTestNumber(), getTheyAnswer(), getScore());
    }
}