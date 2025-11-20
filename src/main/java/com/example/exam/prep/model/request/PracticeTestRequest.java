package com.example.exam.prep.model.request;

import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class PracticeTestRequest {
    private Set<UUID> partIds;
    private Optional<UUID> refId;

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

    public Optional<UUID> getRefId() {
        return refId;
    }

    public void setRefId(Optional<UUID> refId) {
        this.refId = refId;
    }
}
