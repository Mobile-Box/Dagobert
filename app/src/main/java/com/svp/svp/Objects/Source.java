package com.svp.svp.Objects;

import java.util.ArrayList;

/**
 * Created by Eric on 08.12.2017.
 */

public class Source {

    private String fileName;
    private int codePosition;
    private int namePosition;
    private int typePosition;
    private int detailOnePosition;
    private int detailTwoPosition;
    private int amountPosition;
    private int datePosition;
    private int bankAccountId;

    public Source(String fileName, int codePosition, int namePosition, int typePosition, int detailOnePosition, int detailTwoPosition, int amountPosition, int datePosition, int bankAccountId) {
        this.fileName = fileName;
        this.codePosition = codePosition;
        this.namePosition = namePosition;
        this.typePosition = typePosition;
        this.detailOnePosition = detailOnePosition;
        this.detailTwoPosition = detailTwoPosition;
        this.amountPosition = amountPosition;
        this.datePosition = datePosition;
        this.bankAccountId = bankAccountId;
    }

    public String getFileName() {
        return fileName;
    }

    public int getCodePosition() {
        return codePosition;
    }

    public String buildCode(ArrayList<String> line, int rowNumber) {
        if (fileName.matches("Commerzbank.*") || fileName.matches("GLS.*") || fileName.matches("Amazon.*") || line.get(codePosition).isEmpty()) {
            String code = Integer.toString(bankAccountId) + line.get(datePosition).replace(".", "") + Integer.toString(rowNumber);
            return code;
        } else {
            return line.get(codePosition);
        }
    }

    public int getNamePosition() {
        return namePosition;
    }

    public int getTypePosition() {
        return typePosition;
    }

    public int getDetailOnePosition() {
        return detailOnePosition;
    }

    public int getDetailTwoPosition() {
        return detailTwoPosition;
    }

    public int getBankAccountId() {
        return bankAccountId;
    }

    public int getAmountPosition() {
        return amountPosition;
    }

    public void setAmountPosition(int amountPosition) {
        this.amountPosition = amountPosition;
    }

    public int getDatePosition() {
        return datePosition;
    }

    public void setDatePosition(int datePosition) {
        this.datePosition = datePosition;
    }
}
