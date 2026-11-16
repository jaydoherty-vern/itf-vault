IF
test_status__c = PicklistValue($test_status__c, "differed__c") && status_pmo__c= PicklistValue($status_pmo__c, "build_complete__c")
&& (IsChanged($status_pmo__c) || IsChanged($test_status__c))
THEN
UpdateCurrentRecord({$itf_current_owner_team__c: PicklistValue($itf_current_owner_team__c, "itf__c")});