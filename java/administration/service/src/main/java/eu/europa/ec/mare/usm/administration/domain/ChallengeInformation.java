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

public class ChallengeInformation implements Serializable {

    private static final long serialVersionUID = 1L;
    
    private Long challengeId;
    
    private String challenge;
    
    private String response;
    
    /**
     * @return the challengeId
     */
    public Long getChallengeId() {
        return challengeId;
    }

    /**
     * @param challengeId the challengeId to set
     */
    public void setChallengeId(Long challengeId) {
        this.challengeId = challengeId;
    }



    /**
     * @return the challenge
     */
    public String getChallenge() {
        return challenge;
    }

    /**
     * @param challenge the challenge to set
     */
    public void setChallenge(String challenge) {
        this.challenge = challenge;
    }

    /**
     * @return the response
     */
    public String getResponse() {
        return response;
    }

    /**
     * @param response the response to set
     */
    public void setResponse(String response) {
        this.response = response;
    }
}