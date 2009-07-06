package org.kuali.student.lum.lu.ui.course.client.widgets;

import java.util.ArrayList;

import org.kuali.student.common.ui.client.widgets.KSButton;
import org.kuali.student.common.ui.client.widgets.KSLabel;
import org.kuali.student.common.ui.client.widgets.KSLightBox;
import org.kuali.student.common.ui.client.widgets.KSTextBox;
import org.kuali.student.lum.lu.ui.course.client.service.CluProposalRpcService;
import org.kuali.student.lum.lu.ui.course.client.service.CluProposalRpcServiceAsync;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class Collaborators extends Composite implements HasWorkflowId{
	
    CluProposalRpcServiceAsync cluProposalRpcServiceAsync = GWT.create(CluProposalRpcService.class);
	
    private static final String ricePersonLookupUrl = "http://localhost:8081/ks-rice-dev/kr/lookup.do?methodToCall=start&businessObjectClassName=org.kuali.rice.kim.bo.impl.PersonImpl&docFormKey=88888888&returnLocation="+GWT.getHostPageBaseURL()+"sendResponse.html&hideReturnLink=false";
    
	private String workflowId;
	
	private VerticalPanel collaboratorPanel = new VerticalPanel();
	private KSTextBox userIdField = new KSTextBox();
	private VerticalPanel userIds = new VerticalPanel();
    KSLightBox searchUserDialog = new KSLightBox();
    
	public Collaborators(){
        init();
    }
    private void init(){
        super.initWidget(collaboratorPanel);

        HorizontalPanel inputPanel = new HorizontalPanel();
        inputPanel.add(new KSLabel("User Id"));
        inputPanel.add(userIdField);
        
        final KRUserSearchIFrame userSearch = new KRUserSearchIFrame(ricePersonLookupUrl);
        userSearch.addSelectionHandler(new SelectionHandler<String>(){
			public void onSelection(SelectionEvent<String> event) {
				userIdField.setValue(event.getSelectedItem());
				searchUserDialog.hide();
			}
        });

        userSearch.setSize("600px", "500px");
        
        VerticalPanel userLookupPanel = new VerticalPanel();
        
		userLookupPanel.add(userSearch);
		
		KSButton closeSearchButton = new KSButton("Cancel");
		closeSearchButton.addClickHandler(new ClickHandler(){
        	public void onClick(ClickEvent event) {
        		searchUserDialog.hide();
        	}
        });
		
		userLookupPanel.add(closeSearchButton);
		
		searchUserDialog.setWidget(userLookupPanel);
        
        
        KSButton searchForPeopleButton = new KSButton("User Search");
        searchForPeopleButton.addClickHandler(new ClickHandler(){
        	public void onClick(ClickEvent event) {
        		userSearch.setUrl(ricePersonLookupUrl);
        		searchUserDialog.show();
        	}
        });
        inputPanel.add(searchForPeopleButton);
        
        
        KSButton inviteCollabButton = new KSButton("Invite Collaborators");
        inviteCollabButton.addClickHandler(new ClickHandler(){
			public void onClick(ClickEvent event) {
				addCollaborator(userIdField.getValue());
			}
        });
        inputPanel.add(inviteCollabButton);
        collaboratorPanel.add(inputPanel);
        collaboratorPanel.add(new KSLabel("Current Collaborators"));
        collaboratorPanel.add(userIds);
    }
    
    public void refreshCollaboratorList(){
    	if(workflowId!=null){
	        cluProposalRpcServiceAsync.getCollaborators(workflowId, new AsyncCallback<ArrayList<String>>(){
				public void onFailure(Throwable caught) {
				}
				public void onSuccess(ArrayList<String> result) {
					userIds.clear();
					for(String id:result){
						userIds.add(new KSLabel(id));
					}
				}
	        });
    	}
    }
    
    private void addCollaborator(final String recipientPrincipalId){
    	if(workflowId==null){
    		Window.alert("Workflow must be started before Collaborators can be added");
    	}else{
	    	cluProposalRpcServiceAsync.addCollaborator(workflowId, recipientPrincipalId, new AsyncCallback<Boolean>(){
				public void onFailure(Throwable caught) {
					Window.alert("Could not add Collaborator");
				}
	
				public void onSuccess(Boolean result) {
					userIdField.setValue("");
					//Add to the list and no refresh even though we should because rice has a timing issue
					userIds.add(new KSLabel(recipientPrincipalId));
					//refreshCollaboratorList();
				}
	    		
	    	});
    	}
    }
    
	@Override
	public String getWorkflowId() {
		return workflowId;
	}
	@Override
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	


}
