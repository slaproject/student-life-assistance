package com.studentapp.backend.dto;

import lombok.Data;

@Data
public class TaxAgentRequest {
    private Boolean hasOnCampusJob;
    private Boolean hasWorkStudy;
    private String residentState;
    private String workState;

    public Boolean getHasOnCampusJob() {
        return hasOnCampusJob;
    }

    public void setHasOnCampusJob(Boolean hasOnCampusJob) {
        this.hasOnCampusJob = hasOnCampusJob;
    }

    public Boolean getHasWorkStudy() {
        return hasWorkStudy;
    }

    public void setHasWorkStudy(Boolean hasWorkStudy) {
        this.hasWorkStudy = hasWorkStudy;
    }

    public String getResidentState() {
        return residentState;
    }

    public void setResidentState(String residentState) {
        this.residentState = residentState;
    }

    public String getWorkState() {
        return workState;
    }

    public void setWorkState(String workState) {
        this.workState = workState;
    }
}

