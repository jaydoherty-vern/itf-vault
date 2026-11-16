IF
((test_status__c = PicklistValue($test_status__c, "differed__c") && status_pmo__c= PicklistValue($status_pmo__c, "build_complete__c")) ||
(test_status__c = PicklistValue($test_status__c, "completed__c") && (status_pmo__c = PicklistValue($status_pmo__c, "build_complete__c")  || status_pmo__c = PicklistValue($status_pmo__c, "testing_completed__c"))))
&& (IsChanged($status_pmo__c) || IsChanged($test_status__c))
THEN
SendNotification($requirement_uat_results__c, UserNames(
    "tree.yager@veeva.com",
    "s.dewagan@itftherapeutics.com",
    "tim.obert@itftherapeutics.com",
    "k.branco@itftherapeutics.com"
));