package com.teleeza.wallet.teleeza.utils;


import okhttp3.MediaType;

public class Constants {
    public static final String BASIC_AUTH_STRING = "Basic";
    public static final String BEARER_AUTH_STRING = "Bearer";
    public static final String AUTHORIZATION_HEADER_STRING = "Authorization";
    public static final String CACHE_CONTROL_HEADER = "cache-control";
    public static final String CACHE_CONTROL_HEADER_VALUE = "no-cache";
    public static final MediaType JSON
            = MediaType.get("application/json; charset=utf-8");
    public static MediaType JSON_MEDIA_TYPE = MediaType.parse("application/json;");
    public static MediaType FORMDATA_MEDIA_TYPE = MediaType.parse("multipart/form-data;");
    public static final String TRANSACTION_DESCRIPTION = "Teleeza wallet transaction";

    public static final String CUSTOMER_PAYBILL_ONLINE = "CustomerPayBillOnline";
    public static final String ACCOUNT_BALANCE_COMMAND = "AccountBalance";

    public static final String SHORT_CODE_IDENTIFIER = "4";

    // Charges


}
