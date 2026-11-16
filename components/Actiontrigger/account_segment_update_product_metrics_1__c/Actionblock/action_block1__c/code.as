IF
Not(IsBlank(segment__c)) && IsChanged($segment__c)
THEN
UpdateRecords(GetRelatedRecords($account__vr), {$itf_segment__c: Text(segment__c)});