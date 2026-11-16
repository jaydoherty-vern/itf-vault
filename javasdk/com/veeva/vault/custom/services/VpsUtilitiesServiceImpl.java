/*
 * --------------------------------------------------------------------
 * RecordTrigger:	VpsUtilitiesServiceImpl
 * Object:			n/a
 * Author:			natalya.sniff@veeva.com
 * Date:			2026-02-22
 *---------------------------------------------------------------------
 * Description:	Service to execute utility functions
 *---------------------------------------------------------------------
 * Copyright (c) 2025 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 *		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services;

import com.veeva.vault.custom.services.interfaces.VpsUtilitiesService;
import com.veeva.vault.sdk.api.core.*;
import com.veeva.vault.sdk.api.data.*;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.notification.NotificationParameters;
import com.veeva.vault.sdk.api.notification.NotificationService;
import com.veeva.vault.sdk.api.notification.NotificationTemplate;
import com.veeva.vault.sdk.api.query.Query;
import com.veeva.vault.sdk.api.query.QueryExecutionRequest;
import com.veeva.vault.sdk.api.query.QueryService;
import com.veeva.vault.sdk.api.token.TokenRequest;
import com.veeva.vault.sdk.api.token.TokenService;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public class VpsUtilitiesServiceImpl implements VpsUtilitiesService {

    /**
     * Utility method to save records in batches of 500.
     * This prevents hitting platform limits when updating large numbers of records.
     *
     * @param records The collection of records to save
     * @param logError Boolean flag indicating whether to log errors during save operations
     */
    public void batchSaveRecords(Collection<Record> records, Boolean logError, Boolean withObjectType) {
        if (!records.isEmpty()) {
            final RecordService recordService = ServiceLocator.locate(RecordService.class);
            final LogService logService = ServiceLocator.locate(LogService.class);
            int iterations = records.size() % 500 == 0 ? Math.floorDiv(records.size(), 500) : Math.floorDiv(records.size(), 500) + 1;
            for (int i = 0; i < iterations; i++) {
                int skip = i * 500;
                int limit = Math.min(500, records.size() - skip);
                logService.info("Saving record iteration {}", i);
                List<Record> localRecords = records.stream().skip(skip).limit(limit).collect(VaultCollectors.toList());
                RecordBatchSaveRequest.Builder builder = withObjectType
                        ? recordService.newRecordBatchSaveRequestBuilder().withRecords(localRecords).withAllowObjectTypeChange()
                        : recordService.newRecordBatchSaveRequestBuilder().withRecords(localRecords);
                recordService.batchSaveRecords(builder.build())
                        .onSuccesses(positionalRecordIds -> {
                            if (logError && logService.isDebugEnabled()) logService.debug("saved successfully");
                        }).onErrors(batchOperationErrors -> {
                            batchOperationErrors.stream().forEach(batchOperationError -> {
                                String errorMsg = new StringBuilder("Unable to create: Record [").append(batchOperationError.getInputPosition()).append("] has error ").append(batchOperationError.getError().getMessage()).toString();
                                if (logError && logService.isDebugEnabled()) logService.error(errorMsg);
                            });
                        })
                        .execute();
            }
        }
    }
    /**
     * Utility method to save records in batches of 500.
     * This prevents hitting platform limits when updating large numbers of records.
     *
     * @param records The collection of records to save
     * @param logError Boolean flag indicating whether to log errors during save operations
     */
    public  Map<String, Boolean> batchSaveRecordsWithReturn(Collection<Record> records, Boolean logError) {
        Map<String, Boolean> response = VaultCollections.newMap();
        if (!records.isEmpty()) {
            final RecordService recordService = ServiceLocator.locate(RecordService.class);
            final LogService logService = ServiceLocator.locate(LogService.class);
            int iterations = records.size() % 500 == 0 ? Math.floorDiv(records.size(), 500) : Math.floorDiv(records.size(), 500) + 1;
            for (int i = 0; i < iterations; i++) {
                int skip = i * 500;
                int limit = Math.min(500, records.size() - skip);
                logService.info("Saving record iteration {}", i);
                List<Record> localRecords = records.stream().skip(skip).limit(limit).collect(VaultCollectors.toList());
                RecordBatchSaveRequest.Builder builder =
                        recordService.newRecordBatchSaveRequestBuilder().withRecords(localRecords);
                recordService.batchSaveRecords(builder.build())
                        .onSuccesses(positionalRecordIds -> {
                            positionalRecordIds.stream().forEach(positionalRecordId -> response.put(positionalRecordId.getRecordId(), true));
                            if (logError && logService.isDebugEnabled()) logService.debug("saved successfully");
                        }).onErrors(batchOperationErrors -> {
                            batchOperationErrors.stream().forEach(batchOperationError -> {
                                String errorMsg = new StringBuilder("Unable to create: Record [").append(batchOperationError.getInputPosition()).append("] has error ").append(batchOperationError.getError().getMessage()).toString();
                                response.put(batchOperationError.getError().toString(), false);
                                if (logError && logService.isDebugEnabled()) logService.error(errorMsg);
                            });
                        })
                        .execute();
            }
        }
        return response;
    }
    /**
     * Utility method to delete records in batches of 500.
     * This prevents hitting platform limits when updating large numbers of records.
     *
     * @param records The collection of records to delete
     * @param logError Boolean flag indicating whether to log errors during save operations
     */

    public Map<Integer, String> batchDeleteRecords(Collection<Record> records, Boolean logError) {
        final RecordService recordService = ServiceLocator.locate(RecordService.class);
        final LogService logService = ServiceLocator.locate(LogService.class);
        Map<Integer, String> response = VaultCollections.newMap();
        if(!records.isEmpty()){
            int iterations = records.size() % 500 == 0? Math.floorDiv(records.size(), 500) : Math.floorDiv(records.size(), 500) + 1;
            for(int i=0; i<iterations; i++){
                int skip = i*500;
                int limit = Math.min(500, records.size() - skip);
                logService.info("Delete record iteration {}", i);
                List<Record> localRecords =  records.stream().skip(skip).limit(limit).collect(VaultCollectors.toList());
                RecordBatchDeleteRequest.Builder builder = recordService.newRecordBatchDeleteRequestBuilder().withRecords(localRecords);
                recordService.batchDeleteRecords(builder.build())
                        .onSuccesses(positionalRecordIds -> {
                            positionalRecordIds.stream().forEach(positionalRecordId -> response.put(positionalRecordId.getInputPosition()+skip, "Successful"));
                        }).onErrors(batchOperationErrors -> {
                            if(logError && logService.isErrorEnabled()) {
                                batchOperationErrors.stream().forEach(batchOperationError -> {
                                    String errorMsg = new StringBuilder("Unable to delete: Record [").append(batchOperationError.getInputPosition()).append("] has error ").append(batchOperationError.getError().getMessage()).toString();
                                    response.put(batchOperationError.getInputPosition()+skip, errorMsg);
                                    logService.error(errorMsg);
                                    logService.error("has error, Record id: {}", localRecords.get(batchOperationError.getInputPosition()).getValue("id", ValueType.STRING));
                                });
                            }
                        })
                        .execute();
            }
        }
        return response;
    }
    /**
     * Sends notification to identified users
     *
     * @param userIds list of event user id records from the trigger
     * @param param list of parameters, keys as name and value as value
     */
    public void sentNotification(String notificationName, List<String> userIds, Map<String, String> param) {
        // Initialize services
        LogService logService = ServiceLocator.locate(LogService.class);

        //NotificationService
        NotificationService notificationService = ServiceLocator.locate(NotificationService.class);

        //NotificationParameters
        NotificationParameters notificationParameters = notificationService.newNotificationParameters();
        notificationParameters.setRecipientsByPersons(userIds);

        if (logService.isDebugEnabled())
        {
            logService.debug("Got  users : {}", userIds);
            for (String i : param.keySet()) {
                logService.debug("Got  {} : {}", i, param.get(i));
            }
        }
        //NotificationTemplate
        NotificationTemplate template = notificationService.newNotificationTemplate()
                .setTemplateName(notificationName);
        for (String i : param.keySet()) {
            template.setTokenValue(i,param.get(i) );
        }
        //Send Notification with a NotificationTemplate
        notificationService.send(notificationParameters, template);
    }

   public ZonedDateTime getZonedDateTimeFromDate(Date date){
        if(date!=null){
            return  getZonedDateTimeFromLocalDate(LocalDate.parse(date.toString()));
        }
        else return null;
   }
    public ZonedDateTime getZonedDateTimeFromLocalDate(LocalDate date){
        if(date!=null) {
            int hour = 0;
            int min = 0;
            int sec = 0;int millis = 100;
            LocalTime timeWithMicros = LocalTime.of(hour, min, sec, millis * 1_000_000);
            return getZonedDateTimeFromLocalDateTime(date.atTime(timeWithMicros));
        }
        return null;
    }
   protected ZonedDateTime getZonedDateTimeFromLocalDateTime(LocalDateTime date){
       if(date!=null) {
           String dateString = date.toString() + "Z";
           DateTimeFormatter flexibleFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
           return ZonedDateTime.parse(dateString, flexibleFormatter.withZone(ZoneId.of("America/New_York")));
       }
       return null;
   }

    /**
     * @param value
     * @param separator
     * @return the list of values that were separated by the specified
     * separator character in the specified string
     * <br/>
     * This implementation was required since split method of
     * Spring objects is not allowed in Vault SDK
     */
    public  List<String> split (String value, char separator) {
        if (null == value) {
            return null;
        }
        List<String> parts = VaultCollections.newList();
        char[] chArray = value.toCharArray();
        int lastIndex = 0;
        for(int nCnt = 0; nCnt <chArray.length; nCnt++) {
            if(value.charAt(nCnt) == separator) {
                parts.add(value.substring(lastIndex, nCnt).trim());
                lastIndex = nCnt+1;
            }else if (nCnt == chArray.length-1) {
                parts.add(value.substring(lastIndex).trim());
            }
        }
        return parts;
    }
    /*
     *forming common QueryExecutionRequest dynamically
     */
    public QueryExecutionRequest getQueryExecutionRequest(QueryService queryService,
                                                             List<String> ids,
                                                             String whereClause,
                                                             String selectValue,
                                                             String objectApi) {
        LogService logService = ServiceLocator.locate(LogService.class);
        TokenService tokenService = ServiceLocator.locate(TokenService.class);

        if (ids != null && !ids.isEmpty()) {
            TokenRequest accountTokenRequest = tokenService.newTokenRequestBuilder()
                    .withValue("Custom.ids", ids)
                    .build();
            Query contentAcknowledgementQuery = queryService.newQueryBuilder()
                    .withSelect(VaultCollections.asList(selectValue))
                    .withFrom(objectApi)
                    .withWhere(whereClause)
                    .build();
            return queryService.newQueryExecutionRequestBuilder()
                    .withQuery(contentAcknowledgementQuery)
                    .withTokenRequest(accountTokenRequest)
                    .build();
        }
        return null;
    }
    /**
     * Getting object types
     *
     * @param objectName object name
     * @param  targetTypeName targeted s name
     */
    public String getObjectTypeApiId(String objectName, String targetTypeName) {
        // 1. Initialize the Object Metadata Service
        ObjectMetadataService objectMetadataService = ServiceLocator.locate(ObjectMetadataService.class);
        ObjectTypeMetadataCollectionRequest objectTypeMetadataRequest =
                objectMetadataService.newObjectTypeMetadataCollectionRequestBuilder()
                        .withObjectName(objectName)
                        .withActiveOnly()
                        .build();

        ObjectTypeMetadataCollectionResponse objectTypeMetadata =
                objectMetadataService.getObjectTypes(objectTypeMetadataRequest);
        if (objectTypeMetadata != null && !objectTypeMetadata.getObjectTypes().isEmpty()) {
            for (ObjectTypeMetadata objectType : objectTypeMetadata.getObjectTypes()) {
                if (objectType.getName().equals(targetTypeName)) {
                    return objectType.getId();
                }
            }
        }
        return  null;
    }
}