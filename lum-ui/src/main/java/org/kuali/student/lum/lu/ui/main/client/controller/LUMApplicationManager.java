package org.kuali.student.lum.lu.ui.main.client.controller;

import org.kuali.student.common.ui.client.mvc.Controller;
import org.kuali.student.common.ui.client.mvc.View;
import org.kuali.student.lum.lu.ui.course.client.configuration.LUCreateUpdateView;
import org.kuali.student.lum.lu.ui.course.client.configuration.history.KSHistory;
import org.kuali.student.lum.lu.ui.home.client.view.HomeMenuController;
import org.kuali.student.lum.lu.ui.main.client.events.ChangeViewStateEvent;
import org.kuali.student.lum.lu.ui.main.client.events.ChangeViewStateHandler;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.SimplePanel;

public class LUMApplicationManager extends Controller{
    
    private final SimplePanel viewPanel = new SimplePanel();
    
    private final View homeMenu = new HomeMenuController(this);
    //private CourseProposalManager createCourse = new CourseProposalManager(this);
    KSHistory history;
    
    //Wire in validator
    private View courseCreateUpdate = new LUCreateUpdateView("course", "proposed", null);
    
    public LUMApplicationManager(){
        super();
        history = new KSHistory(this);
        super.initWidget(viewPanel);
    }
    
    protected void onLoad() {
        addApplicationEventHandler(ChangeViewStateEvent.TYPE, new ChangeViewStateHandler() {
            public void onViewStateChange(ChangeViewStateEvent event) {
                showView(event.getViewType());  
            }
        });
    }
  
    public enum LUMViews {
        HOME_MENU, CREATE_COURSE
    }

    @Override
    protected <V extends Enum<?>> View getView(V viewType) {
        switch ((LUMViews) viewType) {
            case HOME_MENU:
                return homeMenu;
            case CREATE_COURSE:
                /*
                createCourse.setCourseProposalType(CourseProposalType.NEW_COURSE);                
                return createCourse;
                */
                ((LUCreateUpdateView)courseCreateUpdate).addLayoutToHistory(history, LUMViews.CREATE_COURSE);
                return courseCreateUpdate;
            default:
                return null;
        }
    }

    @Override
    protected void hideView(View view) {
        viewPanel.clear();
        
    }

    @Override
    protected void renderView(View view) {
        // TODO Bsmith - THIS METHOD NEEDS JAVADOCS
        viewPanel.setWidget((Composite)view);
    }
    
    

    @Override
    public void showDefaultView() {
        this.showView(LUMViews.HOME_MENU);
    }

    public Class<? extends Enum<?>> getViewsEnum() {
        return LUMViews.class;
    }        

}
