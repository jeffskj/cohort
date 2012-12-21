package org.cohort.group;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.cohort.msg.MessageSenderService;
import org.cohort.msg.ResponseHandler;
import org.junit.Test;

import com.google.common.collect.Sets;

public class GroupServiceTest {

    @Test
    public void canJoinGroup() throws IOException {
        final Member currentMember = new Member(UUID.randomUUID());
        final Member remoteMember = new Member(UUID.randomUUID());
        
        final Group toJoin = new Group(UUID.randomUUID());
        Group remoteGroup = new Group(toJoin.getId());
        remoteGroup.setMembers(Sets.newHashSet(remoteMember, new Member(UUID.randomUUID())));
        remoteMember.getMemberOf().add(remoteGroup);
        
        toJoin.getMembers().add(remoteMember);
        
        final GroupService remoteService = new GroupService(null, remoteMember, null);
        final Set<Member> otherCallers = new HashSet<>();
        
        MessageSenderService sender = new MessageSenderService(null) {
            @Override
            @SuppressWarnings("unchecked")
            public <REQ, RESP> void sendMessage(Member member, REQ request, ResponseHandler<RESP> responseHandler) throws IOException {
                if (member == remoteMember) { //prevent infinite loop
                    JoinResponse response = remoteService.handle((JoinRequest)request);
                    responseHandler.handle((RESP) response);
                } else {
                    otherCallers.add(member);
                }                
            };
        };
        
        GroupService localService = new GroupService(null, currentMember, sender);
        localService.joinGroup(toJoin);
        
        Set<Member> members = currentMember.getGroup(toJoin.getId()).getMembers();
        System.out.println(members);
        assertEquals(2, members.size());
        assertEquals(1, otherCallers.size());
    }
}