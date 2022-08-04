/*
 * Copyright (c) 2022. URKEI.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of URKEI.
 * The program(s) may be used and/or copied only with the written permission
 * of the owner or in accordance with the terms and conditions stipulated in
 * the contract under which the program(s) have been supplied.
 */

package com.urkei.gitscrapper.dto.tui;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.urkei.gitscrapper.dto.tui.NewBranch;

import java.util.ArrayList;
import java.util.List;

/**
 * Our representation of github repo.
 *
 * Todo: rename to something not containing New
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class NewRepo {

    private String name;

    private String owner;

    private List<NewBranch> branches;

    public NewRepo() {
    }

    public NewRepo(String repoName, String repoOwner) {
        this.name = repoName;
        this.owner = repoOwner;
        this.branches = new ArrayList<>();
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOwner() {
        return owner;
    }

    public void setOwner(String owner) {
        this.owner = owner;
    }

    public List<NewBranch> getBranches() {
        return branches;
    }

    public void setBranches(List<NewBranch> branches) {
        this.branches = branches;
    }

    @Override
    public String toString() {
        return "Repo{" +
                "name='" + name + "', " +
                "owner=" + owner + ", " +
                "branches=" + branches +
                "}";
    }
}
