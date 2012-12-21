package org.cohort.group;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Group {
    private UUID id;

    @XmlElementWrapper(name = "members")
    private Set<Member> members = new HashSet<Member>();

    public Group() {
    }
    
    public Group(UUID id) {
        this.id = id;
    }
    
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

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id).build();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        Group other = (Group) obj;
        return new EqualsBuilder().append(id, other.id).isEquals();
    }
}