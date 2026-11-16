IF
Not(IsBlank(itf_hcp__c )) && IsChanged($itf_hcp__c)
THEN
SendNotification($itf_marf_notification__c, UserFields(ownerid__v));
SendNotification($itf_marf_notification__c, Persons(RecordByUniqueField($person__sys, {$external_id__v: 'MarketAccessTeam'})));