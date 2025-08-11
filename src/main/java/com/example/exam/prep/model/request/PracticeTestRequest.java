package com.example.exam.prep.model.request;

import java.util.Set;
import java.util.UUID;

public class PracticeTestRequest {
    private Set<UUID> partIds;

    public PracticeTestRequest() {
    }

    public PracticeTestRequest(Set<UUID> partIds) {
        this.partIds = partIds;
    }

    public Set<UUID> getPartIds() {
        return partIds;
    }

    public void setPartIds(Set<UUID> partIds) {
        this.partIds = partIds;
    }
}
