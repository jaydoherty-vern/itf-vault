/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsAccountMergeHelperServiceImpl
 * Author:				natalya.sniff @ Veeva
 * Date:				2026-05-24
 *---------------------------------------------------------------------
 * Description: Helper Service to handle the account and address merge logic
 *---------------------------------------------------------------------
 * Copyright (c) 2025 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.custom.services.interfaces.VpsAccountMergeHelperService;
import com.veeva.vault.custom.services.interfaces.VpsUtilitiesService;
import com.veeva.vault.custom.udc.VpsAccountMergeConstant;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.query.Query;
import com.veeva.vault.sdk.api.query.QueryExecutionRequest;
import com.veeva.vault.sdk.api.query.QueryLogicalOperator;
import com.veeva.vault.sdk.api.query.QueryService;
import com.veeva.vault.sdk.api.token.TokenRequest;
import com.veeva.vault.sdk.api.token.TokenService;
import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public class VpsAccountMergeHelperServiceImpl implements VpsAccountMergeHelperService {

    @Override
    public void processMerge(List<String> accountIds, Map<String, List<String>> mergedIdsMap, String objectName) {
        LogService logService = ServiceLocator.locate(LogService.class);
        if (accountIds == null || accountIds.isEmpty() || mergedIdsMap == null || mergedIdsMap.isEmpty()) {
            if (logService.isInfoEnabled()) {
                logService.info("No Accounts to merge.");
            }
            return;
        }
        //Query merge_rule__c and create a map of object to Merge Rules
        List<String> mergeIncludedFieldList = getIncludedFields(objectName);
        processAccountAddressRecords(accountIds, mergedIdsMap, mergeIncludedFieldList, VpsAccountMergeConstant.ACCOUNT_OBJ);

    }

    /**
     * Process Account Records
     * @param accountIds - Winner and Loser Account ids
     * @param mergedIdsMap - Map of Winner Account Id to Loser Account Ids list
     */
    private void processAccountAddressRecords(List<String> accountIds, Map<String, List<String>> mergedIdsMap,
                                              List<String> fieldsIncludedList, String objectType) {
        LogService logService = ServiceLocator.locate(LogService.class);
        VpsUtilitiesService utilitiesService = ServiceLocator.locate(VpsUtilitiesService.class);
        Map<String, ObjectFieldMetadata> objectFieldMetadata = getFieldValueType(objectType,
                fieldsIncludedList);
        List<String> fieldsList = VaultCollections.newList();
        fieldsList.addAll(objectFieldMetadata.keySet());
        Map<String, Record> accIdToRecordMap = getAccountRecords(accountIds, fieldsList,
                objectFieldMetadata, objectType);
        if (accIdToRecordMap == null || accIdToRecordMap.isEmpty()) {
            if (logService.isInfoEnabled()) {
                logService.info("account records does not exists. ");
            }
            return;
        }


        List<Record> winnerRecordToUpdate = VaultCollections.newList();

         mergedIdsMap.forEach((winner, losers)-> {
            Record winnerRecord = getWinnerRecordUpdate(winner, losers, accIdToRecordMap, objectFieldMetadata);
            if (winnerRecord != null) {
                winnerRecordToUpdate.add(winnerRecord);
            }

        });
        if (winnerRecordToUpdate != null && !winnerRecordToUpdate.isEmpty()) {
            utilitiesService.batchSaveRecords(winnerRecordToUpdate, true,false);
        }
    }
    /**
     * Evaluate the Account rules and update the record associated with the winner record based on rules
     * @param accIdToRecordMap -
     * @return Updated Winner account records
     */
    private Record getWinnerRecordUpdate(String winnerId, List<String> loserIds, Map<String, Record> accIdToRecordMap,  Map<String,
            ObjectFieldMetadata> objectFieldMetadata) {
        LogService logService = ServiceLocator.locate(LogService.class);
        Record winner = accIdToRecordMap.get(winnerId);
        logService.debug("winnerId:{}, loserId:{}",winnerId,loserIds);
        if (winner == null) {
            return null;
        }
        for (String loserId : loserIds) {
            Record loser = accIdToRecordMap.get(loserId);
            if (loser == null) {
                continue;
            }
            processMergeIncludedFields(objectFieldMetadata, winner, loser);
        }
        return winner;

    }

    /**
     * @param winner
     * @param loser
     */
    private void processMergeIncludedFields(Map<String, ObjectFieldMetadata>  objectFieldMetadata, Record winner, Record loser) {
        LogService logService = ServiceLocator.locate(LogService.class);
        logService.info("winnervalue id:{}",  winner.getValue("id",ValueType.STRING));
        objectFieldMetadata.forEach((fieldName, fieldMetaData)->{
            if (fieldMetaData == null) {
                if (logService.isInfoEnabled()) {
                    logService.info("Invalid field name {}. could not retrieve field metadata ", fieldName);
                }
                return;
            }
            ValueType valType = fieldMetaData.getValueType();
            if(valType == ValueType.BOOLEAN ){

                Boolean winnerValue = winner.getValue(fieldName, ValueType.BOOLEAN);
                Boolean loserValue = loser.getValue(fieldName, ValueType.BOOLEAN);
                if((loserValue!= null && loserValue) && (winnerValue== null || !winnerValue)){
                winner.setValue(fieldName, loserValue);

                logService.debug("fieldName:{}, winnerValue:{}:loservalue:{}", fieldName, winnerValue, loserValue);
                }
            }
            else{

                Object winnerValue = winner.getValue(fieldName, valType);
                Object loserValue = loser.getValue(fieldName, valType);
                if(loserValue!= null && winnerValue == null) {
                    winner.setValue(fieldName, loserValue);
                    logService.debug("fieldName:{}, winnerValue:{}:loservalue:{}", fieldName, winnerValue, loserValue);
                }
            }
        });
    }


    private  Map<String, ObjectFieldMetadata> getFieldValueType(String objectName, List<String> fieldApiNames) {
        Map<String, ObjectFieldMetadata> resultMap = VaultCollections.newMap();
        ObjectMetadataService objectMetadataService = ServiceLocator.locate(ObjectMetadataService.class);
        // Get the builder for building the request object of the ObjectFieldCollectionResponse
        ObjectFieldMetadataCollectionRequest.Builder fieldCollectionMetadataRequestBuilder = objectMetadataService.newObjectFieldMetadataCollectionRequestBuilder();
        ObjectFieldMetadataCollectionRequest objectFieldCollectionRequest = fieldCollectionMetadataRequestBuilder
                .withObjectName(objectName)
                .withFieldNames(fieldApiNames)
                .build();

        ObjectFieldMetadataCollectionResponse fieldMetaResponse = objectMetadataService.getObjectFieldMetadataCollection(objectFieldCollectionRequest);
        fieldMetaResponse.getFields().stream().forEach(aField -> {
            if(fieldApiNames.contains(aField.getName())
                    && !aField.getObjectFieldMetadataType().equals( ObjectFieldMetadataType.FORMULA)
                    && !aField.getObjectFieldMetadataType().equals( ObjectFieldMetadataType.ROLLUP)
                    && !aField.getSourceType().equals(SourceType.SYSTEM)) {
                resultMap.put(aField.getName(), aField);
            }
        });
        return resultMap;
    }

    private Map<String, Record> getAccountRecords(List<String> accountIds, List<String> fieldsList, Map<String,
            ObjectFieldMetadata>  objectFieldMetadataCollectionResponse, String objectType) {
        Map<String, Record> resultMap = VaultCollections.newMap();
        TokenService tokenService = ServiceLocator.locate(TokenService.class);
        QueryService queryService = ServiceLocator.locate(QueryService.class);
        LogService logService = ServiceLocator.locate(LogService.class);
        RecordService recordService = ServiceLocator.locate(RecordService.class);

        TokenRequest tokenRequest = tokenService.newTokenRequestBuilder().withValue("Custom.acc_ids", accountIds).build();
        List<String> fieldsListToSubmit = fieldsList;
        fieldsListToSubmit.add(VpsAccountMergeConstant.ID_FILED);
        Query tokenQuery = queryService.newQueryBuilder().withSelect(fieldsListToSubmit).withFrom(objectType).withWhere("status__v = 'active__v'").appendWhere(QueryLogicalOperator.AND, "id contains (${Custom.acc_ids})").build();

        QueryExecutionRequest queryExecutionRequest = queryService.newQueryExecutionRequestBuilder().withQuery(tokenQuery).withTokenRequest(tokenRequest).build();
        queryService.query(queryExecutionRequest).onSuccess(queryExecutionResponse -> {
            queryExecutionResponse.streamResults().forEach(queryExecutionResult -> {

                String id = queryExecutionResult.getValue(VpsAccountMergeConstant.ID_FILED, ValueType.STRING);
                Record account = recordService.newRecordWithId(objectType, id);
                for (String fieldName : fieldsList) {
                    if (!VpsAccountMergeConstant.ID_FILED.equals(fieldName)) {
                        ObjectFieldMetadata fieldMetaData = objectFieldMetadataCollectionResponse.get(fieldName);
                        ValueType valType = fieldMetaData.getValueType();

                        account.setValue(fieldName, queryExecutionResult.getValue(fieldName, valType));
                    }
                }

                resultMap.put(id, account);

            });
        }).onError(queryError -> {
            logService.error("Error Query: {}", queryError.getQueryString());
            throw new RollbackException(VpsAccountMergeConstant.OPERATION_FAILED, "Failed to query : " + queryError.getMessage());
        }).execute();
        return resultMap;
    }

    /**
     * Query Merge Rules
     * @return - Map of object to Rules
     */
    private List<String> getIncludedFields(String objectType) {
        List<String> objectFieldList = VaultCollections.newList();
        QueryService queryService = ServiceLocator.locate(QueryService.class);
        LogService logService = ServiceLocator.locate(LogService.class);

        TokenService tokenService = ServiceLocator.locate(TokenService.class);
        TokenRequest accountTokenRequest = tokenService.newTokenRequestBuilder()
                .withValue("Custom.type", objectType)
                .build();

        Query accountQuery = queryService.newQueryBuilder()
                .withSelect(VaultCollections.asList(VpsAccountMergeConstant.ID_FILED, VpsAccountMergeConstant.INCLUDED_FIELD_FIELD_NAME_FIELD))
                .withFrom(VpsAccountMergeConstant.INCLUDED_FIELD_OBJ)
                .withWhere("object__c = ${Custom.type} and status__v = 'active__v'")
                .build();
        if (logService.isDebugEnabled())
            logService.debug("account query : {}", accountQuery.getQueryString());
        QueryExecutionRequest queryExecutionRequest = queryService.newQueryExecutionRequestBuilder()
                .withQuery(accountQuery)
                .withTokenRequest(accountTokenRequest)
                .build();
        queryService.query(queryExecutionRequest).onSuccess(queryExecutionResponse -> {
            queryExecutionResponse.streamResults().forEach(queryExecutionResult -> {

                String objectField =
                        queryExecutionResult.getValue(VpsAccountMergeConstant.INCLUDED_FIELD_FIELD_NAME_FIELD,
                                ValueType.STRING);
                objectFieldList.add(objectField);
            });
        }).onError(queryError -> {
            logService.error("Error Query: {}", queryError.getQueryString());
            throw new RollbackException(VpsAccountMergeConstant.OPERATION_FAILED, "Failed to query : " + queryError.getMessage());
        }).execute();

        return objectFieldList;
    }
}