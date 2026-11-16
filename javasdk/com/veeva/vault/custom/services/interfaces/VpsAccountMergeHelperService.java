/*
 * --------------------------------------------------------------------
 * UserDefinedService:	VpsAccountMergeHelperService
 * Author:				natalya.sniff @ Veeva
 * Date:				2026-03-04
 *---------------------------------------------------------------------
 * Description:	This method handles the account and address merge
 *---------------------------------------------------------------------
 * Copyright (c) 2025 Veeva Systems Inc.  All Rights Reserved.
 *		This code is based on pre-existing content developed and
 * 		owned by Veeva Systems Inc. and may only be used in connection
 *		with the deliverable with which it was provided to Customer.
 *---------------------------------------------------------------------
 */
package com.veeva.vault.custom.services.interfaces;

import com.veeva.vault.sdk.api.core.UserDefinedService;
import com.veeva.vault.sdk.api.core.UserDefinedServiceInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

@UserDefinedServiceInfo
public interface VpsAccountMergeHelperService extends UserDefinedService {

    void processMerge(List<String> accountIds, Map<String, List<String>> mergedIdsMap, String objectName);
}