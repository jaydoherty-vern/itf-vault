IF
    Not(IsBlank(account__v))
THEN
UpdateCurrentRecord({$account_primary_parent__c: account__vr.primary_parent__v });