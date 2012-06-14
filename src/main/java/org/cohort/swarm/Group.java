package org.cohort.swarm;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Group {
    private UUID id;

    @XmlElementWrapper(name = "members")
    private Set<Member> members = new HashSet<Member>();

    public void setMembers(Set<Member> nodes) {
        members = nodes;
    }

    public Set<Member> getMembers() {
        return members;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getId() {
        return id;
    }
}