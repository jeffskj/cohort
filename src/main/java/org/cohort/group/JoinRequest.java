package org.cohort.group;

import java.util.UUID;

class JoinRequest {
    private UUID groupId;
    private Member member;

    public JoinRequest() {
    }
    
    public JoinRequest(Member currentMember, UUID groupId) {
        member = currentMember;
        this.groupId = groupId;
    }
    
    public Member getMember() {
        return member;
    }
    
    public void setMember(Member member) {
        this.member = member;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
}