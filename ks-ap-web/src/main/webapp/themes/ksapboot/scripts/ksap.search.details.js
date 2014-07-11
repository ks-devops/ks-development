//Called onDocumentReady in CourseDetailsUI.xml (Course Details page) to detect page referrer
function detectReferrerForBackLinkText() {
    var backLink = jQuery('.uif-link.cdp-back-link:visible');

    //check for empty document.referrer
    if (document.referrer != null && document.referrer.length > 0) {
        var referrer = document.referrer.split('kr-krad/')[1].split('?')[0];

        if ( referrer == "course" ) {
            //from course search, change link text
            backLink.text('Return to search results');
        } else if ( referrer == "planner" ) {
            //from planner, change link test
            backLink.text('Return to plan');
        }
    }
}


//Sections with multiple formats or offerings
function checkboxSelectAndHighlight() {

    //Add and remove class when radio is toggled
    jQuery(".uif-verticalRadioControl").change(function () {
        if(jQuery(this).is(":checked")){
            jQuery(this).closest('.uif-tooltip').addClass('ksap-selected-row').siblings().removeClass('ksap-selected-row');
        }
    });

    //Add and remove class when checkbox is toggled
    jQuery('.uif-checkboxControl').change(function () {
        if(jQuery(this).is(":checked")){
            jQuery(this).closest('tr').addClass('ksap-selected-row');
        }else{
            jQuery(this).closest('tr').removeClass('ksap-selected-row');
        }
    });

    //Replace slashes with plus signs in multiple format offering options
    jQuery('.ksap-section-format-header label').each(function() {
        jQuery(this).html(jQuery(this).html().replace(/\//g," + "));
    });

}

// Registering Course Search Results Facets events
function registerCourseSectionEvents(jqObjects){
    jQuery(jqObjects)
        .on('COURSE_SECTION_ADDED', function(event, data) {
            ksapAddCourseSection(data);
        })
        .on('FILTER_COURSE_OFFERING', function(event, data) {
            ksapFilterCourseOffering(data);
        });
}

/**
 * Dynamic updating event for when an registration group is added to the page
 *
 * @param data - Information about the event
 */
function ksapAddCourseSection (data){

    // Display the planned marker on the course offering header if needed
    resetCheckBoxes();
    var courseOfferingMarker = jQuery("#"+data.termId+"_"+data.courseOfferingCode+"_section .ksap-section-planned-marker");
    if(courseOfferingMarker.length){
        courseOfferingMarker.removeClass("ksap-hide");
    }

    // Show the planned groups if needed and update the number of planned registration groups displayed in header
    var plannedContainer = jQuery("#KSAP-CourseSectionDetails-PlannedSections_"+data.courseOfferingId);
    plannedContainer.removeClass("ksap-hide");
    var plannedCount = parseInt(plannedContainer.attr("data-numberplanned"));
    plannedCount = plannedCount+1;
    plannedContainer.attr("data-numberplanned",plannedCount);
    var plannedHeaderText = plannedContainer.find(".uif-headerText-span");
    var newText = "Planned Registration Groups ("+plannedCount+")";
    plannedHeaderText[0].innerHTML = newText;

    // Add the new planned activities to the planned groups
    var plannedTemplate = jQuery("#KSAP-CourseSectionDetails-PlannedSection-Template");

    if(plannedTemplate.length){
        var item = plannedTemplate.html();

        // Replace placeholders
        for (var key in data)
            if (data.hasOwnProperty(key))
                item = eval("item.replace(/__KSAP__"+key.toUpperCase()+"__/gi,'"+data[key]+"')");
        item = item.replace(/id=\"(u\d+)\"/gi,"id=\""+data.uid+"_$1\"");
        var itemElement = jQuery("<div/>").html(item).attr("class", "uif-collectionItem uif-boxCollectionItem clearfix");

        // Add Activities
        var activities = data.activities;
        for(var i = 0; i<activities.length; i++){
            var activity = activities[i];
            var plannedActivityTemplate = jQuery("#KSAP-CourseSectionDetails-PlannedSection-Activity-Template");
            var item2 = plannedActivityTemplate.html();
            // Replace placeholders
            for (var key2 in activity)
                if (activity.hasOwnProperty(key2))
                    item2 = eval("item2.replace(/__KSAP__"+key2.toUpperCase()+"__/gi,'"+activity[key2]+"')");
            var item2Element = jQuery("<div/>").html(item2).attr("class","uif-collectionItem uif-boxCollectionItem clearfix");

            // Hide needed fields
            if(activity.honors == false){
                item2Element.find("#honors").addClass("ksap-hide");
            }

            if(activity.classUrl.length == 0){
                item2Element.find("#classUrl").addClass("ksap-hide");
            }

            if(activity.activityOfferingRequisites == false){
                item2Element.find("#requirementsUrl").addClass("ksap-hide");
            }

            // For now modify the add to plan button to indicate that it is added and display click functionality
            var addLink = jQuery("#"+activity.activityOfferingId+"_addLink");
            if(addLink.length){
                addLink.removeClass("ks-fontello-icon-hollow-circled-plus");
                addLink.addClass("ks-fontello-icon-ok-circled");
            }

            // Add activity
            item2Element.appendTo(itemElement.find("#Activities-Placeholder"));
        }


        // Add new element to the planned groups section
        itemElement.appendTo("#KSAP-CourseSectionDetails-PlannedSections_"+data.courseOfferingId)
            .css({backgroundColor:"#ffffcc"})
            .hide()
            .fadeIn(250, function() {
            })
            .animate({backgroundColor:"#ffffff"}, 1500, function() {
                itemElement.css({background: "none"});
                runHiddenScripts(data.uid);
            });
    }
}

/**
 * Dynamic updating event when recalculating valid status of AOs and FOs
 *
 * @param data - Information about the event
 */
function ksapFilterCourseOffering (data){
    // Get DOM Object for the course offering being updated
    var courseOffering = jQuery("#"+data.termId+"_"+data.courseOfferingCode+"_section");

    // Get the AO rows under the course offering
    var activities = courseOffering.find(".ksap-activity-row");
    var validActivityIds = data.activities;

    // Check each activity under the course offering
    for(var i = 0; i < activities.length; i++){
        var activity = activities[i];
        var activityId = activity.getAttribute("id");
        var valid = jQuery.inArray(activityId,validActivityIds);
        // inArray returns index of found object, -1 represents not found
        if(valid<0){
            jQuery(activity).addClass("ksap-invalid-activity");
        }else{
            jQuery(activity).removeClass("ksap-invalid-activity");
            jQuery(activity).removeClass("ksap-hide");
        }
    }

    // Get the FO radio objects under the course offering
    var formatRadioOptions = jQuery("#"+data.termId+"_"+data.courseOfferingCode+"_formatOfferingOptions");
    var formatRadios = formatRadioOptions.find(".ksap-format-radio");
    var validFormatIds = data.formatOfferings;

    // Check each radio under the course offering
    for(var i = 0; i < formatRadios.length; i++){
        var format = formatRadios[i];
        var formatId = format.getAttribute("value");
        var valid = jQuery.inArray(formatId,validFormatIds);
        // inArray returns index of found object, -1 represents not found
        if(valid<0){
            jQuery(format).addClass("ksap-invalid-format");

            // Include label in changes
            var radioId = jQuery(format).attr("id");
            var label = jQuery("[for='"+radioId+"']");
            label.addClass("ksap-invalid-format");
        }else{
            // Include label in changes
            jQuery(format).removeClass("ksap-invalid-format");
            var radioId = jQuery(format).attr("id");
            var label = jQuery("[for='"+radioId+"']");
            label.removeClass("ksap-invalid-format");
        }
    }


    // Setup the add button if reg group is returned.
    var regGroupId = data.regGroupId;
    var addButton = jQuery("#"+data.termId+"_"+data.courseOfferingCode+"_"+data.formatOfferingId+"_addButton");
    if(regGroupId.length){
        addButton.removeClass("disabled");
        addButton.removeAttr("disabled");
        addButton.attr("data-reggroupid",regGroupId);

    }else{
        addButton.addClass("disabled");
        addButton.attr("disabled","disabled");
    }
    hideInvalidActivities();
    updateCourseOffering(courseOffering);
}

/**
 * Sets the activity and format offerings since they cannot be set up in kard
 */
function setupActivityAndFormatOfferings(){

    // Sets the activity id for a row in ao lists since default krad does not allow setting ids on table rows
    var rowIds = jQuery(".rowActivityOfferingId");
    for(var i = 0;i<rowIds.length;i++){
        var rowId = rowIds[i];
        var parentRow = jQuery("#"+rowId.getAttribute("id")).parents(".ksap-activity-row");
        parentRow.attr("id",rowId.innerHTML.trim());
    }

    // Sets the invalid class for radio options of FOs
    var validFormatOfferings = jQuery("[data-validformatoffering='False']");
    for(var i = 0; i<validFormatOfferings.length; i++){
        // Set class on radio input
        var format = validFormatOfferings[i];
        var formatId = format.getAttribute("data-formatid");
        var formatRadios = jQuery(".ksap-format-radio[value='"+formatId+"']");
        formatRadios.addClass("ksap-invalid-format");

        // Set class on radio label
        var radioId = formatRadios.attr("id");
        var label = jQuery("[for='"+radioId+"']");
        label.addClass("ksap-invalid-format");
    }
}

/**
 * Toggle the divs on/off based on the selected FormatOffering radio option
 * @param element - Element containing the radio control
 * @param divIdPrefix - Prefix of the ID that will be built to toggle on/off
 */
function toggleFormatOfferingSections(element, divIdPrefix) {
    // Find selected format offering list
    var selectedElement = jQuery(element);
    var selectedInput = selectedElement.find('input:checked');
    var selectedFormatOfferingId = selectedInput.val();
    var idToToggleOn = divIdPrefix + selectedFormatOfferingId+"_section";
    var toggleOn = jQuery(idToToggleOn);

    // Hide all format offering lists
    jQuery.each(jQuery(element).find('.uif-verticalRadioControl'), function() {
        var unselectedElement = jQuery(this);
        var unselectedFormatOfferingId = unselectedElement.val();
        var idToToggleOff = divIdPrefix + unselectedFormatOfferingId +"_section";
        var toggleOff = jQuery(idToToggleOff);
        toggleOff.addClass('ksap-hide');
    });

    // Show selected format offering list
    jQuery(idToToggleOn).removeClass('ksap-hide');


}

/**
 * Handles the selection of an activity offering by gathering all the currently selected activity offerings and
 * submitting them to the controller.
 *
 * @param selected - The selected object
 * @param e - Current event going on.
 */
function clickActivity(selected,e){
    // Gather selected and checked information
    var checkedCheckboxes = jQuery("[type='checkbox']:checked");
    var selectedObjectId = selected.getAttribute("data-activityid");
    var checkedIds = [];
    for(var i = 0; i<checkedCheckboxes.length; i++){
        var checkedItem = checkedCheckboxes[i];
        var activityId = checkedItem.getAttribute("data-activityid");
        checkedIds.push(activityId);
    }

    // Setup form submit
    stopEvent(e);
    var form = jQuery('<form />').attr("id", "tempSubmit").attr("action", "course/details").attr("method", "post");
    jQuery("body").append(form);
    var additionalFormData = {
        methodToCall:"filterAOs",
        selectedActivityId:selectedObjectId,
        checkedActivities: checkedIds.toString()
    }

    // Submit to the controller
    form.ajaxSubmit({
        data : additionalFormData,
        dataType : 'json',
        success : ksapAjaxSubmitSuccessCallback,
        error : ksapAjaxSubmitErrorCallback
    });

    // Clean up after submit
    fnClosePopup();
    jQuery("form#tempSubmit").remove();
}

/**
 * Resets all checkboxes to an unchecked state.
 */
function resetCheckBoxes(){
    var checkedCheckboxes = jQuery("[type='checkbox']:checked");
    checkedCheckboxes.attr("checked",false);

}

/**
 * Toggles the display of the invalid activity offerings
 *
 * @param showButtonId - Id of toggle button
 */
function toggleHideShowInvalidActivities(showButtonId){
    var sectionId = jQuery("#"+showButtonId).attr("data-relatedtable");
    var table = jQuery("#"+sectionId);
    var invalidActivities = table.find(".ksap-invalid-activity");
    var status = jQuery("#"+showButtonId).attr("data-show");
    if(status == "true"){
        jQuery("#"+showButtonId).attr("data-show","false");
        jQuery("#"+showButtonId).html("Show all");
        invalidActivities.addClass("ksap-hide");
    }else{
        jQuery("#"+showButtonId).attr("data-show","true");
        jQuery("#"+showButtonId).html("Hide ineligible");
        invalidActivities.removeClass("ksap-hide");
    }
}

/**
 * Hides or shows the activity offerings based on the toggle selection
 */
function hideInvalidActivities(){
    var invalidActivities = jQuery(".ksap-invalid-activity");
    invalidActivities.addClass("ksap-hide");
}

/**
 * Hides or shows the button for toggling the display of invalid activity offerings based on whether there are
 * any invalid activities for the table
 *
 * @param showButtonId - Id of toggle button
 */
function hideShowToggleHideShowInvalidActivities(showButtonId){
    var sectionId = jQuery("#"+showButtonId).attr("data-relatedtable");
    var table = jQuery("#"+sectionId);
    var invalidActivities = table.find(".ksap-invalid-activity");
    if(invalidActivities.length){
        jQuery("#"+showButtonId).removeClass("ksap-hide");
    }else{
        jQuery("#"+showButtonId).addClass("ksap-hide");
    }
}

/**
 * Update the information on a course offering
 *
 * @param courseOffering - Course offering section to update
 */
function updateCourseOffering(courseOffering){

    // Find format type sections headers in the course offering
    var headers = courseOffering.find(".ksap-section-format-list-header");
    headers.each(function (){

        // Find section data and objects
        var id = this.getAttribute("id");
        var sectionHeaders = jQuery("#"+id);
        var showButton = sectionHeaders.find(".ksap-section-format-toggle");
        var headerText = sectionHeaders.find(".ksap-section-format-title");
        var sectionId = showButton.attr("data-relatedtable");
        var table = jQuery("#"+sectionId);
        var selected = table.find('input:checked');
        var titleLabel = headerText.attr("data-formatName");

        // Update toggle button
        hideShowToggleHideShowInvalidActivities(showButton.attr("id"));

        //Update type section headers
        if(selected.length){
            headerText.find("p").html(titleLabel+ " (Selected)")
            showButton.addClass("ksap-hide");
        }else{
            var invalidActivities = table.find(".ksap-invalid-activity");
            if(!invalidActivities.length){
                var totalActivities = headerText.attr("data-totalactivities");
                headerText.find("p").html(titleLabel+" ("+totalActivities+")")
            }else{
                var totalActivities = headerText.attr("data-totalactivities");
                var numberInvalidActivities = parseInt(invalidActivities.length);
                headerText.find("p").html(titleLabel+" ("+numberInvalidActivities+" of "+totalActivities+")");
            }

        }
    });
}

/**
 * Updates all course offering sections
 */
function updateAllCourseOffering(){
    var courseOfferings = jQuery(".ksap-courseoffering-section");
    courseOfferings.each(function(){
        updateCourseOffering(jQuery(this));
    })
}

/**
 * Javascript setup of the course section page on document ready
 */
function setupCourseSectionPage(){
    registerCourseSectionEvents(jQuery('#coursedetails-page'));
    setupActivityAndFormatOfferings();
    resetCheckBoxes();
    updateAllCourseOffering();
    hideInvalidActivities();
}

