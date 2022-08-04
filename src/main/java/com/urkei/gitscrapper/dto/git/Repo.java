/*
 * Copyright (c) 2022. URKEI.
 * All rights reserved.
 *
 * The copyright to the computer program(s) herein is the property of URKEI.
 * The program(s) may be used and/or copied only with the written permission
 * of the owner or in accordance with the terms and conditions stipulated in
 * the contract under which the program(s) have been supplied.
 */

package com.urkei.gitscrapper.dto.git;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.urkei.gitscrapper.dto.git.Owner;

/**
 * Github repo data.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class Repo {

    /** Repo name. */
    private String name;

    /** Repo owner. */
    private Owner owner;

    /** Is this repo a fork or not. */
    private Boolean fork;

    /** URI for obtaining branches of this repo. */
    private String branches_url;

    public Repo() {
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Owner getOwner() {
        return owner;
    }

    public void setOwner(Owner owner) {
        this.owner = owner;
    }

    public Boolean getFork() {
        return fork;
    }

    public void setFork(Boolean fork) {
        this.fork = fork;
    }

    public String getBranches_url() {
        return branches_url;
    }

    public void setBranches_url(String branches_url) {
        this.branches_url = branches_url;
    }

    @Override
    public String toString() {
        return "Repo{" +
                "name='" + name + "', " +
                "fork=" + fork + ", " +
                "owner=" + owner.getLogin() + ", " +
                "branches_url=" + branches_url +
                "}";
    }

}
