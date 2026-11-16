IF
status_pmo__c = PicklistValue($status_pmo__c, "build_complete__c") && ( Contains(Text(impacted_business__c), "MSL") || Contains(Text(impacted_business__c), "TLL") || Contains(Text(impacted_business__c), "ALL") || Contains(Text(impacted_business__c), "KAM")) &&
(IsChanged($status_pmo__c) || IsChanged($impacted_business__c))
THEN
UpdateCurrentRecord({$itf_current_owner_team__c: PicklistValue($itf_current_owner_team__c, "uat_testers__c")});