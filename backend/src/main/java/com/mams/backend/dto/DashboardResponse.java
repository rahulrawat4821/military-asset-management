package com.mams.backend.dto;

public class DashboardResponse {

    private Integer openingBalance;
    private Integer closingBalance;
    private Integer netMovement;
    private Integer purchases;
    private Integer transfersIn;
    private Integer transfersOut;
    private Integer assigned;
    private Integer expended;

    public DashboardResponse(Integer openingBalance, Integer closingBalance, Integer netMovement,
                              Integer purchases, Integer transfersIn, Integer transfersOut,
                              Integer assigned, Integer expended) {
        this.openingBalance = openingBalance;
        this.closingBalance = closingBalance;
        this.netMovement = netMovement;
        this.purchases = purchases;
        this.transfersIn = transfersIn;
        this.transfersOut = transfersOut;
        this.assigned = assigned;
        this.expended = expended;
    }

    public Integer getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(Integer openingBalance) {
        this.openingBalance = openingBalance;
    }

    public Integer getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(Integer closingBalance) {
        this.closingBalance = closingBalance;
    }

    public Integer getNetMovement() {
        return netMovement;
    }

    public void setNetMovement(Integer netMovement) {
        this.netMovement = netMovement;
    }

    public Integer getPurchases() {
        return purchases;
    }

    public void setPurchases(Integer purchases) {
        this.purchases = purchases;
    }

    public Integer getTransfersIn() {
        return transfersIn;
    }

    public void setTransfersIn(Integer transfersIn) {
        this.transfersIn = transfersIn;
    }

    public Integer getTransfersOut() {
        return transfersOut;
    }

    public void setTransfersOut(Integer transfersOut) {
        this.transfersOut = transfersOut;
    }

    public Integer getAssigned() {
        return assigned;
    }

    public void setAssigned(Integer assigned) {
        this.assigned = assigned;
    }

    public Integer getExpended() {
        return expended;
    }

    public void setExpended(Integer expended) {
        this.expended = expended;
    }
}
