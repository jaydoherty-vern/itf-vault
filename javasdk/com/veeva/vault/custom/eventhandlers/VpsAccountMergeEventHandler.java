/*
 * --------------------------------------------------------------------
 * RecordTrigger:	VpsAccountMergeEventHandler
 * Object:			account__v
 * Author:				natalya.sniff @ Veeva
 * Date:				2026-05-24
 *---------------------------------------------------------------------
 * Description:	handels merge of account__v
 *---------------------------------------------------------------------
 * Copyright (c) 2025 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 *		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.eventhandlers;

import com.veeva.vault.custom.services.interfaces.VpsAccountMergeHelperService;
import com.veeva.vault.custom.udc.VpsAccountMergeConstant;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.core.ServiceLocator;
import com.veeva.vault.sdk.api.core.VaultCollections;
import com.veeva.vault.sdk.api.data.*;

import java.util.List;
import java.util.Map;

@RecordMergeEventHandlerInfo(object = "account__v")
public class VpsAccountMergeEventHandler implements RecordMergeEventHandler {

    @Override
    public void onMergeStart(RecordMergeEventStartContext context) {
        LogService logService = ServiceLocator.locate(LogService.class);
        VpsAccountMergeHelperService mergeHelperService = ServiceLocator.locate(VpsAccountMergeHelperService.class);

        String objectName = context.getObjectName();
        List<RecordMergeSet> mergeSets = context.getRecordMergeSets();
        Map<String, List<String>> mergedIdsMap = VaultCollections.newMap();
        List<String> accountIds = VaultCollections.newList();
        for (RecordMergeSet mergeSet : mergeSets) {
            String dupRecordId = mergeSet.getDuplicateRecordId();
            String mainRecordId = mergeSet.getMainRecordId();
            if (!accountIds.contains(mainRecordId)) accountIds.add(mainRecordId);
            if (!accountIds.contains(dupRecordId)) accountIds.add(dupRecordId);
            if (mergedIdsMap == null || !mergedIdsMap.containsKey(mainRecordId)) {
                mergedIdsMap.put(mainRecordId, VaultCollections.asList(dupRecordId));

            } else {
                mergedIdsMap.get(mainRecordId).add(dupRecordId);
            }
            logService.info(String.format("Started merge of duplicate record [%s] into main record [%s] for object [%s] ", dupRecordId, mainRecordId, objectName));
        }
        if (accountIds != null && !accountIds.isEmpty()) {
            mergeHelperService.processMerge(accountIds, mergedIdsMap, VpsAccountMergeConstant.ACCOUNT_OBJ_FIELDS);//do
            // something
        }
    }
    @Override
    public void onMergeComplete(RecordMergeEventCompletionContext context) {
        LogService logService = ServiceLocator.locate(LogService.class);
        String objectName = context.getObjectName();
        String jobId = context.getJobId();
        List<RecordMergeSetResult> mergeSetResults = context.getRecordMergeSetResults();
        for (RecordMergeSetResult mergeSetResult : mergeSetResults) {
            String dupRecordId = mergeSetResult.getDuplicateRecordId();
            String mainRecordId = mergeSetResult.getMainRecordId();
            RecordMergeCompletionStatus status = mergeSetResult.getStatus();
            if (status == RecordMergeCompletionStatus.SUCCESS) {
                logService.info(String.format("Succeeded in merge of duplicate record [%s] into main record [%s] for " +
                    "object [%s] job [%s]",  dupRecordId, mainRecordId, objectName, jobId));
            } else if (status == RecordMergeCompletionStatus.FAILURE) {
                logService.info(String.format("Failed in merge of duplicate record [%s] into main record [%s] for object %s job %s",  dupRecordId, mainRecordId, objectName, jobId));
            }
        }
    }
}