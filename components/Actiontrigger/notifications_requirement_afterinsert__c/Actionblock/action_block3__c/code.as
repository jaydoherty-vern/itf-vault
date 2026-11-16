IF
Not(IsBlank(status_pmo__c))
THEN
SendNotification($itf_new_requirement_added__c, UserNames(
    "tree.yager@veeva.com",
    "s.dewagan@itftherapeutics.com",
    "tim.obert@itftherapeutics.com",
    "k.branco@itftherapeutics.com"
));