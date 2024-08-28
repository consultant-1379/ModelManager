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

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.DefaultValues;
import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.EventParameter;

/**
 * @author aia
 */
public class ByteParameterDecoder extends GenericParameterDecoder {

    /**
     * @param eventParameter
     *            - event
     */
    public ByteParameterDecoder(final EventParameter eventParameter) {
        super(eventParameter);
    }

    @Override
    public Byte decode(final byte[] data, final int offset) {
        return (byte) dataConverterHelper.getByteArrayUnsignedInteger(data, offset, eventParameter.getNumberOfBytes());
    }

    /*
     * As there is no byte data type in avro all ByteParameters will be integers in the GenericRecord. Therefore the default value for invalid fields
     * is set to DEFAULT_INT_VALUE for consistency in the output.
     */
    @Override
    protected Object getDefaultValueForGenericRecord() {
        return DefaultValues.DEFAULT_INT_VALUE;
    }

}
