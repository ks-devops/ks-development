package org.kuali.student.ui.kitchensink.client.kscommons.datepicker;

import org.kuali.student.common.ui.client.widgets.KSDatePicker;
import org.kuali.student.common.ui.client.widgets.KSLabel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DatePickerExample extends Composite {

    final VerticalPanel main = new VerticalPanel();
    
    final KSDatePicker datePicker = GWT.create(KSDatePicker.class);
    final KSLabel label = GWT.create(KSLabel.class);

    public DatePickerExample() {
        
// Can't do this as have no access to DatePicker in KSDate Picker
//        ksDatePicker.addValueChangeHandler(new ValueChangeHandler<Date>(){
//
//            public void onValueChange(ValueChangeEvent<Date> event) {
//                
//              Window.alert("You picked " + ksDatePicker.getValue());
//            }   
//        });
        
        label.setText("Click in the box to open the date picker: ");
        
        main.add(label);
        main.add(datePicker);

        super.initWidget(main);
    }
}
