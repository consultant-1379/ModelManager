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
public class Asn1ContentByteArrayParameterDecoder extends GenericParameterDecoder {

    /**
     * @param eventParameter
     *            - event
     */
    public Asn1ContentByteArrayParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
    }

    @Override
    public Object getDecodeValue(final byte[] data, final int offset) {
        final Object decodeValue = super.getDecodeValue(data, offset);
        if (decodeValue == null) {
            eventParameter.setNumberOfBytes(0);
        }
        return decodeValue;
    }

    @Override
    public Object getDecodeValueForGenericRecord(final byte[] data, final int offset) {
        final Object decodeValue = super.getDecodeValueForGenericRecord(data, offset);
        if (decodeValue == null) {
            eventParameter.setNumberOfBytes(0);
            return DefaultValues.DEFAULT_GENERIC_RECORD_BYTEARRAY_VALYE;
        }
        return decodeValue;
    }

    @Override
    public Object decodeValue(final byte[] data, final int offsetToUse, final boolean isGenericRecord) {
        // it assume, previous parameter contains the length of asn1 message
        // i.e. EVENT_PARAM_L3MESSAGE_LENGTH
        final int messageLengthParamOffSet = offsetToUse - 2;
        if (dataConverterHelper.isMarkedInvalid(data, messageLengthParamOffSet)) {
            return null;
        }
        final int asn1Length = (int) dataConverterHelper.getByteArrayInteger(data, messageLengthParamOffSet, 2, false);

        final byte[] decodedData = dataConverterHelper.getPrimitiveByteArray(data, offsetToUse, asn1Length);
        eventParameter.setNumberOfBytes(asn1Length);

        if (isGenericRecord) {
            return ByteBuffer.wrap(decodedData);
        }

        return decodedData;
    }

    @Override
    protected Object decode(final byte[] data, final int offset) {
        return decodeValue(data, offset, false);
    }

    /**
     * Default value is set in override method {@link #getDecodeValueForGenericRecord(byte[], int) getDecodeValueForGenericRecord(byte[], int)} for
     * GenericRecord
     */
    @Override
    protected Object getDefaultValueForGenericRecord() {
        return null;
    }

}
