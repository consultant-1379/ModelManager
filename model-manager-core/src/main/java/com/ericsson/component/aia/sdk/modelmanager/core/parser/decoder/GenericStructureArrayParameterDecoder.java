/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.decoder;

import java.util.Collection;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public abstract class GenericStructureArrayParameterDecoder extends GenericParameterDecoder {

    /**
     * @param eventParameter
     *            - event
     */
    public GenericStructureArrayParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
    }

    @Override
    protected abstract Object decode(final byte[] data, final int offset);

    /**
     * @return object array
     */
    protected abstract Object[] initializeArray();

    @Override
    public Object getDecodeValue(final byte[] data, final int offset) {
        final Object[] result = initializeArray();
        int offsetToUse = offset;
        for (int i = 0; i < eventParameter.getValidStructureArraySize(); i++) {
            final Object decodeValue = super.getDecodeValue(data, offsetToUse);
            result[i] = decodeValue;
            offsetToUse = super.adjustOffset(offsetToUse) + eventParameter.getEndSkip() + eventParameter.getStartSkip();
        }
        return result;
    }

    /**
     * @param <T>
     *            - <T>
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @param result
     *            - result
     * @return object
     */
    public <T> Object getDecodeValueForGenericRecord(final byte[] data, final int offset, final Collection<T> result) {
        int offsetToUse = offset;
        for (int i = 0; i < eventParameter.getValidStructureArraySize(); i++) {
            final T decodeValue = (T) super.getDecodeValueForGenericRecord(data, offsetToUse);
            result.add(decodeValue);
            offsetToUse = super.adjustOffset(offsetToUse) + eventParameter.getEndSkip() + eventParameter.getStartSkip();
        }
        return result;
    }

    @Override
    public int adjustOffset(int offset) {
        final int structureArraySize = eventParameter.getValidStructureArraySize();
        if (eventParameter.isStructLastParameter() && structureArraySize > 0) {
            offset += ((structureArraySize - 1) * eventParameter.getStructSize());
        }
        return super.adjustOffset(offset);
    }
}
