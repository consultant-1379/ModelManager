/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2017
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.model;

/**
 * @author aia
 */
public class StructureArrayParam {

    private boolean structArray = false;

    private int maxStructArraySize = 0;

    private int validStructureArraySize = 0;

    private int startSkip = 0;

    private int endSkip = 0;

    private int structSize = 0;

    private boolean structFirstParameter = false;

    private boolean structLastParameter = false;

    boolean isStructArray() {
        return structArray;
    }

    void setStructArray(final boolean structArray) {
        this.structArray = structArray;
    }

    int getMaxStructArraySize() {
        return maxStructArraySize;
    }

    void setMaxStructArraySize(final int maxStructArraySize) {
        this.maxStructArraySize = maxStructArraySize;
    }

    int getValidStructureArraySize() {
        return validStructureArraySize;
    }

    void setValidStructureArraySize(final int validStructureArraySize) {
        this.validStructureArraySize = validStructureArraySize;
    }

    int getStartSkip() {
        return startSkip;
    }

    void setStartSkip(final int startSkip) {
        this.startSkip = startSkip;
    }

    int getEndSkip() {
        return endSkip;
    }

    void setEndSkip(final int endSkip) {
        this.endSkip = endSkip;
    }

    int getStructSize() {
        return structSize;
    }

    void setStructSize(final int structSize) {
        this.structSize = structSize;
    }

    boolean isStructFirstParameter() {
        return structFirstParameter;
    }

    void setStructFirstParameter(final boolean structFirstParameter) {
        this.structFirstParameter = structFirstParameter;
    }

    boolean isStructLastParameter() {
        return structLastParameter;
    }

    void setStructLastParameter(final boolean structLastParameter) {
        this.structLastParameter = structLastParameter;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + endSkip;
        result = prime * result + maxStructArraySize;
        result = prime * result + startSkip;
        result = prime * result + (structArray ? 1231 : 1237);
        result = prime * result + (structFirstParameter ? 1231 : 1237);
        result = prime * result + (structLastParameter ? 1231 : 1237);
        result = prime * result + structSize;
        result = prime * result + validStructureArraySize;
        return result;
    }

    @Override
    public boolean equals(final Object obj) {
        if (obj instanceof StructureArrayParam) {
            final StructureArrayParam other = (StructureArrayParam) obj;
            return this.structArray != other.structArray && startSkip != other.startSkip && endSkip != other.endSkip
                    && maxStructArraySize != other.maxStructArraySize && structFirstParameter != other.structFirstParameter
                    && structLastParameter != other.structLastParameter && structSize != other.structSize
                    && validStructureArraySize != other.validStructureArraySize;
        }

        return false;
    }

    @Override
    public String toString() {
        return "StructureArrayParam [structArray=" + structArray + ", maxStructArraySize=" + maxStructArraySize + ", validStructureArraySize="
                + validStructureArraySize + ", startSkip=" + startSkip + ", endSkip=" + endSkip + ", structSize=" + structSize
                + ", structFirstParameter=" + structFirstParameter + ", structLastParameter=" + structLastParameter + "]";
    }
}
