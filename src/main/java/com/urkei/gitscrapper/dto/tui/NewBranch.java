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

/**
 * Our representation of github branch.
 *
 * Todo: rename to something not containing New
 */
public class NewBranch {

    /** Branch name. */
    private String name;

    /** Last commit SHA value. */
    private String sha;

    public NewBranch() {
    }

    public NewBranch(String name, String sha) {
        this.name = name;
        this.sha = sha;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSha() {
        return sha;
    }

    public void setSha(String sha) {
        this.sha = sha;
    }

    @Override
    public String toString() {
        return "Branch{" +
                "name='" + name + "', " +
                "sha=" + sha +
                "}";
    }

}
