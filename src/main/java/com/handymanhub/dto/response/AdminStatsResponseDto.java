package com.handymanhub.dto.response;

// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
// Single object with all platform metrics.
// Frontend makes ONE call to populate the entire admin dashboard.
// ━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━

public class AdminStatsResponseDto {

    private final long totalBookings;
    private final long pendingBookings;
    private final long inProgressBookings;
    private final long completedBookings;
    private final long cancelledBookings;
    private final long totalWorkers;
    private final long totalCustomers;
    private final long totalContractors;
    private final long verifiedContractors;
    private final long estimatedRevenue;

    private AdminStatsResponseDto(Builder builder) {
        this.totalBookings = builder.totalBookings;
        this.pendingBookings = builder.pendingBookings;
        this.inProgressBookings = builder.inProgressBookings;
        this.completedBookings = builder.completedBookings;
        this.cancelledBookings = builder.cancelledBookings;
        this.totalWorkers = builder.totalWorkers;
        this.totalCustomers = builder.totalCustomers;
        this.totalContractors = builder.totalContractors;
        this.verifiedContractors = builder.verifiedContractors;
        this.estimatedRevenue = builder.estimatedRevenue;
    }

    public long getTotalBookings()        { return totalBookings; }
    public long getPendingBookings()      { return pendingBookings; }
    public long getInProgressBookings()   { return inProgressBookings; }
    public long getCompletedBookings()    { return completedBookings; }
    public long getCancelledBookings()    { return cancelledBookings; }
    public long getTotalWorkers()         { return totalWorkers; }
    public long getTotalCustomers()       { return totalCustomers; }
    public long getTotalContractors()     { return totalContractors; }
    public long getVerifiedContractors()  { return verifiedContractors; }
    public long getEstimatedRevenue()     { return estimatedRevenue; }

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private long totalBookings, pendingBookings, inProgressBookings;
        private long completedBookings, cancelledBookings;
        private long totalWorkers, totalCustomers;
        private long totalContractors, verifiedContractors, estimatedRevenue;

        public Builder totalBookings(long v)       { this.totalBookings = v; return this; }
        public Builder pendingBookings(long v)     { this.pendingBookings = v; return this; }
        public Builder inProgressBookings(long v)  { this.inProgressBookings = v; return this; }
        public Builder completedBookings(long v)   { this.completedBookings = v; return this; }
        public Builder cancelledBookings(long v)   { this.cancelledBookings = v; return this; }
        public Builder totalWorkers(long v)        { this.totalWorkers = v; return this; }
        public Builder totalCustomers(long v)      { this.totalCustomers = v; return this; }
        public Builder totalContractors(long v)    { this.totalContractors = v; return this; }
        public Builder verifiedContractors(long v) { this.verifiedContractors = v; return this; }
        public Builder estimatedRevenue(long v)    { this.estimatedRevenue = v; return this; }

        public AdminStatsResponseDto build() { return new AdminStatsResponseDto(this); }
    }
}