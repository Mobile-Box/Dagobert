package com.svp.svp.Interfaces;

import com.svp.svp.Objects.Navigation.Navigation_Date;

/**
 * Created by Eric Schumacher on 01.02.2018.
 */

public interface Interface_BalanceSheetFragment {
    public void buildBalanceSheetFragment(String type, Navigation_Date date, int id);
    public void buildTransactionShowFragment(int OperationId);

}
