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

import java.util.ArrayList;
import java.util.Collection;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class DottedDecimalStructureArrayStringParameterDecoder extends GenericStructureArrayParameterDecoder {

    private final GenericParameterDecoder decimalStringParameterDecoder;

    /**
     * @param eventParameter
     *            - event
     */
    public DottedDecimalStructureArrayStringParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
        decimalStringParameterDecoder = new DottedDecimalStringParameterDecoder(eventParameter);
    }

    @Override
    protected Object decode(final byte[] data, final int offset) {
        return decimalStringParameterDecoder.decode(data, offset);
    }

    @Override
    public Object getDecodeValueForGenericRecord(final byte[] data, final int offset) {
        final Collection<String> result = new ArrayList<String>(eventParameter.getValidStructureArraySize());
        return super.getDecodeValueForGenericRecord(data, offset, result);
    }

    @Override
    protected Object[] initializeArray() {
        return new String[eventParameter.getValidStructureArraySize()];
    }

    @Override
    protected Object getDefaultValueForGenericRecord() {
        return decimalStringParameterDecoder.getDefaultValueForGenericRecord();
    }

}
