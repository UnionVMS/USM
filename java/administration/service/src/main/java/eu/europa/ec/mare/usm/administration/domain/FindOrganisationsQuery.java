/*
 * Developed by the European Commission - Directorate General for Maritime 
 * Affairs and Fisheries © European Union, 2015-2016.
 * 
 * This file is part of the Integrated Fisheries Data Management (IFDM) Suite.
 * The IFDM Suite is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the Free
 * Software Foundation, either version 3 of the License, or any later version.
 * The IFDM Suite is distributed in the hope that it will be useful, but 
 * WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY
 * or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for
 * more details. You should have received a copy of the GNU General Public 
 * License along with the IFDM Suite. If not, see http://www.gnu.org/licenses/.
 */
package eu.europa.ec.mare.usm.administration.domain;

import java.io.Serializable;

public class FindOrganisationsQuery implements Serializable{
    private static final long serialVersionUID = 7199118791716052294L;

    private String name;
    private String nation;
    private String status;
    private Paginator paginator;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNation() {
        return nation;
    }

    public void setNation(String nation) {
        this.nation = nation;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Paginator getPaginator() {
        return paginator;
    }

     public void setPaginator(Paginator paginator) {
        this.paginator = paginator;
    }

    /**
     * Formats a human-readable view of this instance.
     *
     * @return a human-readable view
     */
    @Override
    public String toString() {
        return "FindOrganisationsQuery{" +
                "name='" + name + '\'' +
                ", nation='" + nation + '\'' +
                ", status='" + status + '\'' +
                '}';
    }
}