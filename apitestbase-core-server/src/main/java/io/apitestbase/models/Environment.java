package io.apitestbase.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import io.apitestbase.models.endpoint.Endpoint;
import io.apitestbase.resources.ResourceJsonViews;

import java.util.List;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class Environment {
    private long id;
    @JsonView({ResourceJsonViews.TeststepEdit.class, ResourceJsonViews.DataTableUIGrid.class})
    private String name;
    private String description;
    private List<Endpoint> endpoints;

    public Environment() {}

    public Environment(long id, String name, String description, List<Endpoint> endpoints) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.endpoints = endpoints;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public void setEndpoints(List<Endpoint> endpoints) {
        this.endpoints = endpoints;
    }
}
