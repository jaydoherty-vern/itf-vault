IF
IsBlank(account_primary_parent__c) 
THEN
UpdateCurrentRecord({$account_primary_parent__c: account__vr.primary_parent__v });