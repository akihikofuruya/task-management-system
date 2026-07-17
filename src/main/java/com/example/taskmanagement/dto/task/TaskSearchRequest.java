package com.example.taskmanagement.dto.task;

import jakarta.validation.constraints.AssertTrue;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

import java.time.LocalDate;

public class TaskSearchRequest {

    @Size(max = 100)
    private String title;

    @Min(0)
    @Max(2)
    private Integer status;

    @Min(1)
    @Max(3)
    private Integer priority;

    private LocalDate fromDate;

    private LocalDate toDate;

    public TaskSearchRequest() {
    }

    @AssertTrue(message = "期限開始日は期限終了日以前の日付を指定してください。")
    public boolean isValidDateRange() {
        if (fromDate == null || toDate == null) {
            return true;
        }

        return !fromDate.isAfter(toDate);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public LocalDate getFromDate() {
        return fromDate;
    }

    public void setFromDate(LocalDate fromDate) {
        this.fromDate = fromDate;
    }

    public LocalDate getToDate() {
        return toDate;
    }

    public void setToDate(LocalDate toDate) {
        this.toDate = toDate;
    }
}
