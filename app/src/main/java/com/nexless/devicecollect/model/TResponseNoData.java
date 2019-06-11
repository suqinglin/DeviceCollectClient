package com.nexless.devicecollect.model;

import com.nexless.devicecollect.AppConstants;

/**
 * Created by Calm on 2017/12/5.
 * TResponseNoData
 */

public class TResponseNoData
{
    public String code;
    public String message;
    public boolean isSuccess()
    {
        return AppConstants.RESPONSE_CODE_SUCCESS.equals(code);
    }
}
