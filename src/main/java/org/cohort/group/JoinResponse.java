package org.cohort.group;

public class JoinResponse {
    private Group group;
    private Member responder; 

    public JoinResponse() {
    }
    
    public JoinResponse(Group group, Member responder) {
        this.group = group;
        this.responder = responder;
    }
    
    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Member getResponder() {
        return responder;
    }
    
    public void setResponder(Member member) {
        responder = member;
    }
}
