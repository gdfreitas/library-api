package com.library.app.logaudit.model;

import com.library.app.user.model.User;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Date;

@Entity
@Table(name = "lib_log_audit")
public class LogAudit implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    @NotNull(message = "may not be null")
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "may not be null")
    private User user;
    @Enumerated(EnumType.STRING)
    @NotNull(message = "may not be null")
    private Action action;
    @NotNull
    private String element;

    public LogAudit() {
        this.createdAt = new Date();
    }

    public LogAudit(@NotNull(message = "my not be null") User user, @NotNull(message = "may not be null") Action action, @NotNull String element) {
        this();
        this.user = user;
        this.action = action;
        this.element = element;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Action getAction() {
        return action;
    }

    public void setAction(Action action) {
        this.action = action;
    }

    public String getElement() {
        return element;
    }

    public void setElement(String element) {
        this.element = element;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        LogAudit other = (LogAudit) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "LogAudit [id=" + id + ", createdAt=" + createdAt + ", user=" + user + ", action=" + action
                + ", element=" + element + "]";
    }

    public enum Action {
        ADD, UPDATE
    }

}
