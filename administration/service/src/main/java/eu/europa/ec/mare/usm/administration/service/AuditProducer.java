/*
﻿Developed with the contribution of the European Commission - Directorate General for Maritime Affairs and Fisheries
© European Union, 2015-2016.

This file is part of the Integrated Fisheries Data Management (IFDM) Suite. The IFDM Suite is free software: you can
redistribute it and/or modify it under the terms of the GNU General Public License as published by the
Free Software Foundation, either version 3 of the License, or any later version. The IFDM Suite is distributed in
the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details. You should have received a
copy of the GNU General Public License along with the IFDM Suite. If not, see <http://www.gnu.org/licenses/>.
 */
package eu.europa.ec.mare.usm.administration.service;

import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.commons.message.impl.AbstractProducer;

import javax.annotation.Resource;
import javax.ejb.Stateless;
import javax.jms.Destination;
import javax.jms.JMSException;
import javax.jms.Queue;

@Stateless
public class AuditProducer extends AbstractProducer {

    @Resource(mappedName = "java:/" + MessageConstants.QUEUE_USER_RESPONSE)
    private Queue replyToQueue;

    @Resource(mappedName = "java:/" + MessageConstants.QUEUE_AUDIT_EVENT)
    private Queue destination;

    public String sendModuleMessage(String text) {
        try {
            return sendModuleMessage(text, replyToQueue);
        } catch (JMSException e) {
            throw new RuntimeException("Error while sending log to audit", e);
        }
    }

    @Override
    public Destination getDestination() {
        return destination;
    }
}
