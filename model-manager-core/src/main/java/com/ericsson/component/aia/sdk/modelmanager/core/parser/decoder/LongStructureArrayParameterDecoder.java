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
 *
 */
public class LongStructureArrayParameterDecoder extends GenericStructureArrayParameterDecoder {

    private final GenericParameterDecoder longParameterDecoder;

    /**
     * @param eventParameter
     *            - event
     */
    public LongStructureArrayParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
        longParameterDecoder = new LongParameterDecoder(eventParameter);
    }

    @Override
    protected Object decode(final byte[] data, final int offset) {
        return longParameterDecoder.decode(data, offset);
    }

    @Override
    public Object getDecodeValueForGenericRecord(final byte[] data, final int offset) {
        final Collection<Long> result = new ArrayList<Long>(eventParameter.getValidStructureArraySize());
        return super.getDecodeValueForGenericRecord(data, offset, result);
    }

    @Override
    protected Object[] initializeArray() {
        return new Long[eventParameter.getValidStructureArraySize()];
    }

    @Override
    protected Object getDefaultValueForGenericRecord() {
        return longParameterDecoder.getDefaultValueForGenericRecord();
    }

}
