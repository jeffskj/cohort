package org.cohort.swarm;

import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;

import com.google.common.collect.Sets;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Member {
    private UUID id;
    private String ipAddress;
    private int port;
    private String name;

    @XmlTransient
    private Set<Group> memberOf = Sets.newHashSet();

    public Member() {
    }

    public Member(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public int getPort() {
        return port;
    }

    public void setPort(int port) {
        this.port = port;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<Group> getMemberOf() {
        return memberOf;
    }
    
    public Group getGroup(UUID groupId) {
        for (Group g : memberOf) {
            if (g.getId().equals(groupId)) {
                return g;
            }
        }
        
        return null;
    }
}
