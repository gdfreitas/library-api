package com.library.app.order.model;

import java.io.Serializable;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.ElementCollection;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.library.app.book.model.Book;
import com.library.app.user.model.Customer;

/**
 * @author gabriel.freitas
 */
@Entity
@Table(name = "lib_order")
public class Order implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @ManyToOne
    @JoinColumn(name = "customer_id")
    @NotNull
    private Customer customer;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lib_order_item", joinColumns = @JoinColumn(name = "order_id"))
    @NotNull
    @Size(min = 1)
    @Valid
    private Set<OrderItem> items;

    @NotNull
    private Double total;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "lib_order_history", joinColumns = @JoinColumn(name = "order_id"))
    @NotNull
    @Size(min = 1)
    @Valid
    private Set<OrderHistoryEntry> historyEntries;

    @Column(name = "current_status")
    @Enumerated(EnumType.STRING)
    @NotNull
    private OrderStatus currentStatus;

    public Order() {
        this.createdAt = new Date();
    }

    public Long getId() {
        return id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(final Date createdAt) {
        this.createdAt = createdAt;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(final Customer customer) {
        this.customer = customer;
    }

    public Set<OrderItem> getItems() {
        if (items == null) {
            items = new HashSet<>();
        }
        return items;
    }

    public void setItems(final Set<OrderItem> items) {
        this.items = items;
    }

    public boolean addItem(final Book book, final Integer quantity) {
        final OrderItem item = new OrderItem(book, quantity);
        return getItems().add(item);
    }

    public Double getTotal() {
        return total;
    }

    public void setTotal(final Double total) {
        this.total = total;
    }

    public void calculateTotal() {
        this.total = 0D;
        for (final OrderItem item : getItems()) {
            item.calculatePrice();
            this.total += item.getPrice();
        }
    }

    public Set<OrderHistoryEntry> getHistoryEntries() {
        if (historyEntries == null) {
            historyEntries = new HashSet<>();
        }
        return historyEntries;
    }

    public void setHistoryEntries(final Set<OrderHistoryEntry> historyEntries) {
        this.historyEntries = historyEntries;
    }

    public void addHistoryEntry(final OrderStatus status) {
        if (this.currentStatus != null) {
            if (this.currentStatus != OrderStatus.RESERVED) {
                throw new IllegalArgumentException("An order in the state " + currentStatus
                        + " cannot have its state changed");
            }
            if (this.currentStatus == status) {
                throw new IllegalArgumentException("The new state must be different from the current one");
            }
        }

        getHistoryEntries().add(new OrderHistoryEntry(status));
        this.currentStatus = status;
    }

    public OrderStatus getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(final OrderStatus currentStatus) {
        this.currentStatus = currentStatus;
    }

    public void setInitialStatus() {
        getHistoryEntries().clear();
        setCurrentStatus(null);
        addHistoryEntry(OrderStatus.RESERVED);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
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
        final Order other = (Order) obj;
        if (id == null) {
            if (other.id != null)
                return false;
        } else if (!id.equals(other.id))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return "Order [id=" + id + ", createdAt=" + createdAt + ", customer=" + customer + ", total=" + total
                + ", currentStatus=" + currentStatus + "]";
    }

    public enum OrderStatus {
        RESERVED,
        RESERVATION_EXPIRED,
        DELIVERED,
        CANCELLED
    }

}