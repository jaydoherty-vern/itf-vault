IF
status_pmo__c = PicklistValue($status_pmo__c, "closed__c")
THEN
SendNotification($itf_ticket_closed__c, UserNames(
    "tree.yager@veeva.com",
    "s.dewagan@itftherapeutics.com",
    "tim.obert@itftherapeutics.com",
    "k.branco@itftherapeutics.com"
));