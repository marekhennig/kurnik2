package pl.hennig.kurnik.kurnik.model;


import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Entity

public class User implements UserDetails {
    public enum Revers {
        DEFAULT, DARK, LIGHT
    }
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private long id;
    private String username;
    private String email;
    private String password;
    private String role;
    private boolean enabled;
    private boolean isPrivate;
    @ElementCollection
    private List<String> friendsUsernames;
    private Revers revers;
    public User(String username, String email, String password, String role, boolean enabled) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.friendsUsernames = new ArrayList<>();
        revers = Revers.DEFAULT;
        isPrivate = false;
    }
    public User(String username, String email, String password, String role, boolean enabled, List<String> friendsUsernames) {
        this.username = username;
        this.email = email;
        this.password = password;
        this.role = role;
        this.enabled = enabled;
        this.friendsUsernames = friendsUsernames;
        revers = Revers.DEFAULT;
        isPrivate = false;
    }
    public User()
    {
        //This constructor is important to login, added again to fix login
    }
    public String getUsername() {
        return username;
    }
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    @Override
    public boolean isEnabled() {
        return enabled;
    }
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
    public void setUsername(String username) {
        this.username = username;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(new SimpleGrantedAuthority(role));
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public List<String> getFriendsUsernames() {
        return friendsUsernames;
    }
    public void setFriendsUsernames(List<String> friendsUsernames) {
        this.friendsUsernames = friendsUsernames;
    }
    public Revers getRevers() {
        return revers;
    }
    public void setRevers(Revers revers) {
        this.revers = revers;
    }
    public boolean isPrivate() {
        return isPrivate;
    }
    public void setPrivate(boolean aPrivate) {
        isPrivate = aPrivate;
    }
    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", username='" + username + '\'' +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +
                ", isActive=" + enabled +
                '}';
    }
}