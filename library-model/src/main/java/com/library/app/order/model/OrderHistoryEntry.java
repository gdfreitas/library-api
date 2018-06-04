package com.library.app.order.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.library.app.order.model.Order.OrderStatus;

/**
 * @author gabriel.freitas
 */
@Embeddable
public class OrderHistoryEntry implements Serializable {

    @Enumerated(EnumType.STRING)
    @NotNull(message = "may not be null")
    private OrderStatus status;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    @NotNull(message = "may not be null")
    private Date createdAt;

    public OrderHistoryEntry() {
    }

    public OrderHistoryEntry(final OrderStatus status) {
        this.status = status;
        this.createdAt = new Date();
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((status == null) ? 0 : status.hashCode());
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final OrderHistoryEntry other = (OrderHistoryEntry) obj;
        if (status != other.status)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "OrderHistoryEntry [status=" + status + ", createdAt=" + createdAt + "]";
    }

}
