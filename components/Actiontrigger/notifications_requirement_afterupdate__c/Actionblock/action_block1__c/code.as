IF
status_pmo__c = PicklistValue($status_pmo__c, "deployed__c") && IsChanged($status_pmo__c)
THEN
SendNotification($itf_requirement_deployed__c, UserNames(
    "tree.yager@veeva.com",
    "s.dewagan@itftherapeutics.com",
    "tim.obert@itftherapeutics.com",
    "k.branco@itftherapeutics.com"
));