/*
 * --------------------------------------------------------------------
 * RecordTrigger:	VpsUtilitiesService
 * Object:			n/a
 * Author:			Natalya.Sniff @ Veeva
 * Date:			2025-10-22
 *---------------------------------------------------------------------
 * Description:	Service to execute utility functions
 *---------------------------------------------------------------------
 * Copyright (c) 2025 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 *		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services.interfaces;

import com.veeva.vault.sdk.api.core.UserDefinedService;
import com.veeva.vault.sdk.api.core.UserDefinedServiceInfo;
import com.veeva.vault.sdk.api.data.Record;
import com.veeva.vault.sdk.api.query.QueryExecutionRequest;
import com.veeva.vault.sdk.api.query.QueryService;

import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

@UserDefinedServiceInfo
public interface VpsUtilitiesService extends UserDefinedService {
    /**
     * Utility method to save records in batches of 500.
     * This prevents hitting platform limits when updating large numbers of records.
     *
     * @param records The collection of records to save
     * @param logError Boolean flag indicating whether to log errors during save operations
     */
    void batchSaveRecords(Collection<Record> records, Boolean logError, Boolean withObjectType) ;

    /**
     * Utility method to save records in batches of 500.
     * This prevents hitting platform limits when updating large numbers of records.
     *
     * @param records The collection of records to save
     * @param logError Boolean flag indicating whether to log errors during save operations
     */
    Map<String, Boolean>  batchSaveRecordsWithReturn(Collection<Record> records, Boolean logError) ;

    /**
     * Utility method to delete records in batches of 500.
     * This prevents hitting platform limits when updating large numbers of records.
     *
     * @param records The collection of records to delete
     * @param logError Boolean flag indicating whether to log errors during save operations
     */
    Map<Integer, String> batchDeleteRecords(Collection<Record> records, Boolean logError);
    /**
     * Sends notification to identified users
     *
     * @param userIds list of event user id records from the trigger
     * @param param list of parameters, keys as name and value as value
     */
    void sentNotification(String notificationName, List<String> userIds, Map<String, String> param);

    ZonedDateTime getZonedDateTimeFromDate(Date date);

    ZonedDateTime getZonedDateTimeFromLocalDate(LocalDate date);
    /**
     * @param value
     * @param separator
     * @return the list of values that were separated by the specified
     * separator character in the specified string
     * <br/>
     * This implementation was required since split method of
     * Spring objects is not allowed in Vault SDK
     */
    List<String> split (String value, char separator);

    /*
     *forming common QueryExecutionRequest dynamically
     * Specifically for queries with search by specified Ids
     */
    QueryExecutionRequest getQueryExecutionRequest(QueryService queryService,
                                                   List<String> ids,
                                                   String whereClause,
                                                   String selectValue,
                                                   String objectApi);


    /**
     * Getting object types
     *
     * @param objectName object name
     * @param  targetTypeName targeted s name
     */
    String getObjectTypeApiId(String objectName, String targetTypeName);
}