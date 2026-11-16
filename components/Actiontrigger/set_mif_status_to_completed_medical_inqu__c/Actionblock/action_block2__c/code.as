IF
state__v = 'closed_state__v'
THEN
UpdateCurrentRecord({$fulfillment_status__v: PicklistValue($medical_inquiry_fulfillment_status1__v, 'completed__v')});