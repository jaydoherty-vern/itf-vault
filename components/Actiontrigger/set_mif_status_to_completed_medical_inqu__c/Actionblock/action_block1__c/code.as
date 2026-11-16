IF
fulfillment_status__v = PicklistValue($medical_inquiry_fulfillment_status1__v, "new__v") && medinquiry_transfer_status__v = PicklistValue($medinquiry_transfer_status__v, "success__v")
THEN
UpdateCurrentRecord({$fulfillment_status__v: PicklistValue($medical_inquiry_fulfillment_status1__v, 'assigned__v')});