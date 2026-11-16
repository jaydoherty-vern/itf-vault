IF
status_pmo__c = PicklistValue($status_pmo__c, "build_complete__c") && ( Contains(Text(impacted_business__c), "MSL") || Contains(Text(impacted_business__c), "TLL") || Contains(Text(impacted_business__c), "ALL") || Contains(Text(impacted_business__c), "KAM")) &&
(IsChanged($status_pmo__c) || IsChanged($impacted_business__c))
THEN
SendNotification($itf_requirement_ready_for_uat__c, UserNames(
    "tree.yager@veeva.com",
    "s.dewagan@itftherapeutics.com",
    "tim.obert@itftherapeutics.com",
    "k.branco@itftherapeutics.com"
));