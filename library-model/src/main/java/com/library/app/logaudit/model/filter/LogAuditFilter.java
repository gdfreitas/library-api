package com.library.app.logaudit.model.filter;

import com.library.app.common.model.filter.GenericFilter;

import java.util.Date;

public class LogAuditFilter extends GenericFilter {

    private Date startDate;
    private Date endDate;
    private Long userId;

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "LogAuditFilter{startDate=" + startDate + ", endDate=" + endDate + ", userId=" + userId + '}';
    }

}
