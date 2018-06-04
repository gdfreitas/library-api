package com.library.app.order.model.filter;

import java.util.Date;

import com.library.app.common.model.filter.GenericFilter;
import com.library.app.order.model.Order.OrderStatus;

/**
 * @author gabriel.freitas
 */
public class OrderFilter extends GenericFilter {

    private Date startDate;
    private Date endDate;
    private Long customerId;
    private OrderStatus status;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(final Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(final Date endDate) {
        this.endDate = endDate;
    }

    public Long getCustomerId() {
        return customerId;
    }

    public void setCustomerId(final Long customerId) {
        this.customerId = customerId;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(final OrderStatus status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "OrderFilter [startDate=" + startDate + ", endDate=" + endDate + ", customerId=" + customerId
                + ", status=" + status + ", toString()=" + super.toString() + "]";
    }

}
