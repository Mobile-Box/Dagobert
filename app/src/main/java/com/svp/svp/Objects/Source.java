package com.svp.svp.Objects;

import java.util.ArrayList;

/**
 * Created by Eric on 08.12.2017.
 */

public class Source {

    private String fileName;
    private String fileType;
    private String fileSplit;
    private int codePosition;
    private int namePosition;
    private int typePosition;
    private int detailOnePosition;
    private int detailTwoPosition;
    private int amountPosition;
    private int datePosition;
    private int bankAccountId;

    public Source(String fileName, String fileType, String fileSplit, int codePosition, int namePosition, int typePosition, int detailOnePosition, int detailTwoPosition, int amountPosition, int datePosition, int bankAccountId) {
        this.fileName = fileName;
        this.fileType = fileType;
        this.fileSplit = fileSplit;
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

    public String getFileType() {
        return fileType;
    }

    public String getFileSplit() {
        return fileSplit;
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

    public static String getSpeceficBankAccountName(int id) {
        switch (id) {
            case 1:
                return "Commerzbank 4600";
            case 2:
                return "Commerzbank 9200";
            case 3:
                return "Commerzbank 4500";
            case 4:
                return "Amazon-DE";
            case 5:
                return "Amazon-UK";
            case 6:
                return "Amazon-ES";
            case 7:
                return "Paypal";
            case 8:
                return "GLS Bank";
            default:
                return "no bank";
        }
    }
}
