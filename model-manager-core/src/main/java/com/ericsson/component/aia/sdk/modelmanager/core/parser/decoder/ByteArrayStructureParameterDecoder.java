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

import java.nio.ByteBuffer;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.DefaultValues;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class ByteArrayStructureParameterDecoder extends GenericParameterDecoder {

    private final GenericParameterDecoder byteParameterDecoder;

    /**
     * @param eventParameter
     *            - event
     */
    public ByteArrayStructureParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
        byteParameterDecoder = new ByteParameterDecoder(eventParameter);
    }

    @Override
    protected Object decode(final byte[] data, final int offset) {
        return byteParameterDecoder.decode(data, offset);
    }

    @Override
    public Object getDecodeValue(final byte[] data, final int offset) {
        final Object[] result = new Byte[eventParameter.getValidStructureArraySize()];
        int offsetToUse = offset;
        for (int i = 0; i < eventParameter.getValidStructureArraySize(); i++) {
            result[i] = super.getDecodeValue(data, offsetToUse);
            offsetToUse = super.adjustOffset(offsetToUse) + eventParameter.getEndSkip() + eventParameter.getStartSkip();
        }
        return result;
    }

    @Override
    public Object getDecodeValueForGenericRecord(final byte[] data, final int offset) {
        final byte[] result = new byte[eventParameter.getValidStructureArraySize()];
        int offsetToUse = offset;
        for (int i = 0; i < eventParameter.getValidStructureArraySize(); i++) {
            result[i] = (byte) super.getDecodeValueForGenericRecord(data, offsetToUse);
            offsetToUse = super.adjustOffset(offsetToUse) + eventParameter.getEndSkip() + eventParameter.getStartSkip();
        }
        return ByteBuffer.wrap(result);
    }

    @Override
    public int adjustOffset(int offset) {
        final int structureArraySize = eventParameter.getValidStructureArraySize();
        if (eventParameter.isStructLastParameter() && structureArraySize > 0) {
            offset += ((structureArraySize - 1) * eventParameter.getStructSize());
        }
        return super.adjustOffset(offset);
    }

    @Override
    protected Object getDefaultValueForGenericRecord() {
        return DefaultValues.DEFAULT_BYTE_VALUE;
    }

}
