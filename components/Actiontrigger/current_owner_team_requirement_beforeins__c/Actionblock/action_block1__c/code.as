IF
status__v = PicklistValue($default_status__v, "active__v")
THEN
UpdateCurrentRecord({$itf_current_owner_team__c: PicklistValue($itf_current_owner_team__c, "veeva__c")});