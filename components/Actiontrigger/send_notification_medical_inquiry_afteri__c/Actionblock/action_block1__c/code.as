IF
medical_inquiry_status__v = PicklistValue($medical_inquiry_status__v, "submitted__v") && fulfillment_status__v != PicklistValue($medical_inquiry_fulfillment_status1__v, "completed__v") && fulfillment_status__v != PicklistValue($medical_inquiry_fulfillment_status1__v, "assigned__v")
&& ownerid__vr.security_profile__sysr.name__v != "System Administrator"&& ownerid__vr.security_profile__sysr.name__v != "ITF_IQVIA_INTEGRATION"&& ownerid__vr.security_profile__sysr.name__v != "VSPA_Data Steward_CRM" 
THEN
SendNotification($itf_mirf_notification__c, UserFields(created_by__v));
SendNotification($itf_mirf_notification__c, Persons(RecordByUniqueField($person__sys, {$external_id__v: 'MedicalInquiryTeam'})));