package com.beauty_salon.model;

public enum FeedbackStatus {

    // Waiting for admin to approve or reject
    PENDING,

    // Approved and visible to all users
    APPROVED,

    // Rejected — hidden from public view
    REJECTED
}
