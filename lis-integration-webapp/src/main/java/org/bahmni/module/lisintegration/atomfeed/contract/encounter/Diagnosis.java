package org.bahmni.module.lisintegration.atomfeed.contract.encounter;

import java.util.Date;

public class Diagnosis {
    private String code;
    private String name;
    private String type;
    private Date date;
    private String codeMethode;

    /**
     * This method gets the code of the diagnosis.
     *
     * @return String
     */
    public String getCode() {
        return this.code;
    }

    /**
     * This method sets the code of the diagnosis.
     *
     * @return String
     */
    public void setCode(String code) {
        this.code = code;
    }

    /**
     * This method gets the name of the diagnosis.
     *
     * @return String
     */
    public String getName() {
        return this.name;
    }

    /**
     * This method sets the name of the diagnosis.
     *
     * @return String
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * This method gets the type of the diagnosis.
     *
     * @return String
     */
    public String getType() {
        return this.type;
    }

    /**
     * This method sets the type of the diagnosis.
     *
     * @return String
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * This method gets the date createing of the diagnosis.
     *
     * @return String
     */
    public Date getDate() {
        return this.date;
    }

    /**
     * This method sets the date createing of the diagnosis.
     *
     * @return String
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * This method gets the code method of the diagnosis.
     *
     * @return String
     */
    public String getCodeMethode() {
        return this.codeMethode;
    }

    /**
     * This method sets the code method of the diagnosis.
     *
     * @return String
     */
    public void setCodeMethode(String codeMethode) {
        this.codeMethode = codeMethode;
    }
}
