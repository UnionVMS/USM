package eu.europa.ec.mare.usm.administration.service.organisation.impl;

import eu.europa.ec.mare.usm.administration.domain.Channel;
import eu.europa.ec.mare.usm.administration.domain.EndPoint;
import eu.europa.ec.mare.usm.administration.domain.EndPointContact;
import eu.europa.ec.mare.usm.administration.domain.Organisation;
import eu.europa.ec.mare.usm.information.entity.ChannelEntity;
import eu.europa.ec.mare.usm.information.entity.EndPointContactEntity;
import eu.europa.ec.mare.usm.information.entity.EndPointEntity;
import eu.europa.ec.mare.usm.information.entity.OrganisationEntity;

import javax.ejb.Stateless;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Provides operations for the conversion of Organisations between their
 * domain-object and JPA entity representation
 */
@Stateless
public class OrganisationConverter {

    @Inject
    private EndPointContactJpaDao endPointContactJpaDao;

    @Inject
    private ChannelJpaDao channelJpaDao;

    public OrganisationConverter() {
    }

    /**
     * Converts from a domain-object representation to an entity representation.
     *
     * @param src the domain-object
     * @return the entity
     */
    public OrganisationEntity convert(Organisation src) {
        OrganisationEntity ret = null;

        if (src != null) {
            ret = new OrganisationEntity();
            ret.setDescription(src.getDescription());
            ret.setIsoa3code(src.getNation());
            ret.setName(src.getName());
            ret.setOrganisationId(src.getOrganisationId());
            ret.setStatus(src.getStatus());
            ret.setEmail(src.getEmail());
        }

        return ret;
    }

    /**
     * Converts from an entity representation to a domain-object representation.
     *
     * @param src the entity
     * @return the domain-object
     */
    public Organisation convert(OrganisationEntity src, int assigned) {
        Organisation ret = null;

        if (src != null) {
            ret = new Organisation();
            ret.setDescription(src.getDescription());
            ret.setNation(src.getIsoa3code());
            ret.setName(src.getName());
            ret.setOrganisationId(src.getOrganisationId());
            ret.setStatus(src.getStatus());
            ret.setAssignedUsers(assigned);
            ret.setEmail(src.getEmail());
            if (src.getParentOrganisation() != null) {
                ret.setParent(src.getParentOrganisation().getName());
            }
            List<EndPoint> endPoints = new ArrayList<>();
            List<EndPointEntity> endPointEntities = src.getEndPointList();
            if (endPointEntities != null && !endPointEntities.isEmpty()) {
                for (EndPointEntity endPointEntity : endPointEntities) {
                    endPoints.add(convertEndPointEntityToDomain(endPointEntity, true));
                }
                ret.setEndpoints(endPoints);
            }
        }
        return ret;
    }

    /**
     * Converts from an entity representation to a domain-object representation.
     *
     * @param entity the entity
     * @return the domain-object
     */
    public EndPoint convertEndPointEntityToDomain(EndPointEntity entity, boolean allLayers) {
        EndPoint ret = new EndPoint();
        ret.setEndpointId(entity.getEndPointId());
        ret.setName(entity.getName());
        ret.setDescription(entity.getDescription());
        ret.setUri(entity.getUri());
        ret.setEmail(entity.getEmail());
        ret.setStatus(entity.getStatus());
        ret.setOrganisationName(entity.getOrganisation().getName());
        if (allLayers) {
            List<Channel> channels = new ArrayList<>();// entity.getChannel();
            List<ChannelEntity> channelEntities = channelJpaDao.findByEndPointId(entity.getEndPointId());
            if (channelEntities != null && !channelEntities.isEmpty()) {
                for (ChannelEntity channel : channelEntities) {
                    channels.add(convertChannelEntityToDomain(channel));
                }
            }
            ret.setChannelList(channels);
            List<EndPointContact> contacts = new ArrayList<>();
            List<EndPointContactEntity> contactEntities = endPointContactJpaDao
                    .findContactByEndPointId(entity.getEndPointId());
            if (contactEntities != null && !contactEntities.isEmpty()) {
                for (EndPointContactEntity contact : contactEntities) {
                    contacts.add(convertEndPointContactEntityToDomain(contact));
                }
            }
            ret.setPersons(contacts);
        }
        return ret;
    }

    /**
     * It allows the conversion of a domain object into an entity for create and
     * update cases
     *
     * @param entity    the newly created or existing entity
     * @param domain    the domain object with users data
     * @param allLayers if we need to extract and go inside all children: true for
     *                  update and false for create action
     */
    public void convertEndPointDomainToEntity(EndPointEntity entity, EndPoint domain, boolean allLayers) {
        entity.setDescription(domain.getDescription());
        entity.setName(domain.getName());
        entity.setStatus(domain.getStatus());
        entity.setUri(domain.getUri());
        entity.setEmail(domain.getEmail());
    }

    /**
     * Converts from an entity representation to a domain-object representation.
     *
     * @param entity the entity
     * @return the domain-object
     */
    public EndPointContact convertEndPointContactEntityToDomain(EndPointContactEntity entity) {
        if (entity != null) {
            EndPointContact ret = new EndPointContact();
            ret.setEndPointContactId(entity.getEndPointContactId());
            ret.setFirstName(entity.getPerson().getFirstName());
            ret.setLastName(entity.getPerson().getLastName());
            ret.setPhoneNumber(entity.getPerson().getPhoneNumber());
            ret.setMobileNumber(entity.getPerson().getMobileNumber());
            ret.setFaxNumber(entity.getPerson().getFaxNumber());
            ret.setEndPointId(entity.getEndPoint().getEndPointId());
            ret.setEmail(entity.getPerson().getEMail());
            ret.setPersonId(entity.getPerson().getPersonId());
            return ret;
        }
        return null;
    }

    /**
     * Converts from an entity representation to a domain-object representation.
     *
     * @param entity the entity
     * @return the domain-object
     */
    public Channel convertChannelEntityToDomain(ChannelEntity entity) {
        if (entity != null) {
            Channel channel = new Channel();
            channel.setChannelId(entity.getChannelId());
            channel.setDataflow(entity.getDataflow());
            channel.setEndpointId(entity.getEndPoint().getEndPointId());
            channel.setPriority(Integer.valueOf(entity.getPriority()));
            channel.setService(entity.getService());
            return channel;
        }
        return null;
    }

    /**
     * Updates an entity with attributes of the domain-object.
     *
     * @param ret the entity representation
     * @param src the domain-object representation
     * @return the entity representation
     */
    public OrganisationEntity update(OrganisationEntity ret, Organisation src) {
        if (ret != null && src != null) {
            ret.setDescription(src.getDescription());
            ret.setIsoa3code(src.getNation());
            ret.setName(src.getName());
            ret.setOrganisationId(src.getOrganisationId());
            ret.setStatus(src.getStatus());
            ret.setEmail(src.getEmail());
        }
        return ret;
    }

    public void convertChannelDomainToEntity(ChannelEntity entity, Channel domain) {
        if (entity != null) {
            entity.setDataflow(domain.getDataflow());
            entity.setPriority(domain.getPriority());
            entity.setService(domain.getService());
        }
    }

    public Organisation convertEntityToDomain(OrganisationEntity entity) {
        Organisation ret = new Organisation();
        ret.setOrganisationId(entity.getOrganisationId());
        if (entity.getParentOrganisation() != null) {
            ret.setParent(entity.getParentOrganisation().getName());
        }
        ret.setName(entity.getName());
        ret.setDescription(entity.getDescription());
        ret.setNation(entity.getIsoa3code());
        ret.setEmail(entity.getEmail());
        ret.setStatus(entity.getStatus());

        return ret;
    }

}
