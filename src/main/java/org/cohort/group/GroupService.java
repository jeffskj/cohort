package org.cohort.group;

import java.io.File;
import java.io.IOException;

import org.cohort.msg.MessageSenderService;
import org.cohort.msg.RequestHandler;
import org.cohort.msg.ResponseHandler;

/**
 * This class is responsible for:
 *  joining a group
 *  updating member stats in group
 *  
 * 
 *  
 * @author jeffskj@github.com
 */
public class GroupService implements RequestHandler<JoinRequest, JoinResponse>, ResponseHandler<JoinResponse> {

    // need to store current group config
    private File workingDir;
    
    private Member currentMember;
    private MessageSenderService messageService;

    public GroupService(File workingDir, Member currentMember, MessageSenderService messageService) {
        this.workingDir = workingDir;
        this.currentMember = currentMember;
        this.messageService = messageService;
    }
    
    public void joinGroup(Group group) throws IOException {
        currentMember.getMemberOf().add(group);
        Member initialMember = group.getMembers().iterator().next();
        messageService.sendMessage(initialMember, new JoinRequest(currentMember, group.getId()), this);
    }
    
    @Override
    public void handle(JoinResponse response) {
        // successfully recieved response, add to group
        Group localGroup = currentMember.getGroup(response.getGroup().getId());
        localGroup.getMembers().add(response.getResponder());
        localGroup.getMembers().add(currentMember);
        
        for (Member m : response.getGroup().getMembers()) {
            // make sure we already joined everying this member is in a group with
            if (!localGroup.getMembers().contains(m) && !m.equals(currentMember)) {
                try {
                    messageService.sendMessage(m, new JoinRequest(currentMember, response.getGroup().getId()), this);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public JoinResponse handle(JoinRequest messageValue) {
        Group group = currentMember.getGroup(messageValue.getGroupId());
        group.getMembers().add(messageValue.getMember()); 
        return new JoinResponse(group, currentMember);
    }
}
