package com.veeva.vault.custom.triggers;
  
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.TriggerOrder;
import com.veeva.vault.sdk.api.core.ValueType;
import com.veeva.vault.sdk.api.data.RecordChange;
import com.veeva.vault.sdk.api.data.RecordEvent;
import com.veeva.vault.sdk.api.data.RecordTrigger;
import com.veeva.vault.sdk.api.data.RecordTriggerContext;
import com.veeva.vault.sdk.api.data.RecordTriggerInfo;
  
@RecordTriggerInfo(object = "ccrms_permission_set_map__c", 
                    events = {RecordEvent.BEFORE_INSERT, RecordEvent.BEFORE_UPDATE},
                    order = TriggerOrder.NUMBER_1)
public class ccrms_permission_set_map__c_ownerid implements RecordTrigger  {
    public void execute(RecordTriggerContext recordTriggerContext) {
      RecordEvent recordEvent = recordTriggerContext.getRecordEvent();
    
    if (recordEvent.equals(RecordEvent.BEFORE_INSERT)
      || recordEvent.equals(RecordEvent.BEFORE_UPDATE)) {
        for (RecordChange r : recordTriggerContext.getRecordChanges()) {
          String ownerid = r.getNew().getValue("ownerid__v", ValueType.STRING);
          if (ownerid == null || ownerid.isEmpty())
            r.getNew().setValue("ownerid__v", r.getNew().getValue("created_by__v", ValueType.STRING));
      }
    }
  }
}