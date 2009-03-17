package org.kuali.student.lum.ui.requirements.client.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.kuali.student.commons.ui.mvc.client.widgets.ModelWidget;
import org.kuali.student.lum.ui.requirements.client.model.ILumModelObject;

import com.google.gwt.user.client.ui.TextArea;

public class LumTextArea<T extends ILumModelObject> extends TextArea implements ModelWidget<T>{
    
    private T modelObject;
    private String modelObjectFieldName;
    
    public LumTextArea(String modelObjectfieldName) {
        this.modelObjectFieldName = modelObjectfieldName; 
    }
    
    public void add(T modelObject) {
        this.modelObject = modelObject;
    }

    public void addBulk(Collection<T> collection) {
        if (collection != null && !collection.isEmpty()) {
            this.modelObject = collection.iterator().next();
        }
    }

    public void clear() {
        modelObject = null;
        setText("");
    }

    public List<T> getItems() {
        List<T> items = new ArrayList<T>();
        items.add(modelObject);
        return items;
    }

    public T getSelection() {
        return modelObject;
    }

    public void remove(T modelObject) {
        clear();
    }

    public void select(T modelObject) {
    }

    public void update(T modelObject) {
        this.modelObject = modelObject;
        setText((String)modelObject.getValue(modelObjectFieldName));
    }

}
