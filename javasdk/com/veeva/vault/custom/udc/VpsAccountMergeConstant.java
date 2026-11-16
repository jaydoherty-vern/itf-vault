/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsAccountMergeConstant
 * Author:				natalya.sniff @ Veeva
 * Date:				2026-03-03
 *---------------------------------------------------------------------
 * Description: Helper Service to handle the account and address merge logic
 *---------------------------------------------------------------------
 * Copyright (c) 2025 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.udc;

import com.veeva.vault.sdk.api.core.UserDefinedClassInfo;

@UserDefinedClassInfo
public class VpsAccountMergeConstant {
    //Standard Fields
    public static final String ID_FILED = "id";

    // Exculded FIELDS
    public static final String INCLUDED_FIELD_OBJ = "merge_included_field__c";
    public static final String INCLUDED_FIELD_FIELD_NAME_FIELD = "field_name__c";
    public static final String OPERATION_FAILED = "OPERATION_FAILED";

    //Account type
    public static final String ACCOUNT_OBJ = "account__v";
    //Account Fields
    public static final String ACCOUNT_OBJ_FIELDS = "account__c";
}