package com.kelvin.jpastorage.entity;

import lombok.*;

import javax.persistence.*;

@NamedQueries({
        @NamedQuery(name="getUserByUsername", query="select u from UserEntity u where u.username = :username"),
        @NamedQuery(name="getUserByEmail", query="select u from UserEntity u where u.email = :email"),
        @NamedQuery(name="getUserCount", query="select count(u) from UserEntity u"),
        @NamedQuery(name="getAllUsers", query="select u from UserEntity u"),
        @NamedQuery(name="searchForUser", query="select u from UserEntity u where " +
                "( lower(u.username) like :search or u.email like :search ) order by u.username"),
})
@Entity
@Table(name = "user_entity")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserEntity {
    public static final String PHONE = "phone";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "firstName";
    public static final String LAST_NAME = "lastName";

    @Id
    private String id;
    @Column(name = "username")
    private String username;
    @Column(name = "password")
    private String password;
    @Column(name = "email")
    private String email;
    @Column(name = "first_name")
    private String firstName;
    @Column(name = "last_name")
    private String lastName;
    @Column(name = "phone")
    private String phone;
}
