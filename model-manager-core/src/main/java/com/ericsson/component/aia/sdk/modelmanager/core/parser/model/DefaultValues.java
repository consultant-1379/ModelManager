/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2014
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.ericsson.component.aia.sdk.modelmanager.core.parser.model;

import java.nio.ByteBuffer;

/**
 * Default values for fields in ap event beans
 */
public class DefaultValues {

    public static final float DEFAULT_FLOAT_VALUE = Float.MIN_VALUE;

    public static final int DEFAULT_INT_VALUE = Integer.MIN_VALUE;

    public static final short DEFAULT_SHORT_VALUE = Short.MIN_VALUE;

    public static final byte DEFAULT_BYTE_VALUE = Byte.MIN_VALUE;

    public static final byte[] DEFAULT_BYTE_ARRAY_VALUE = new byte[] {};

    public static final ByteBuffer DEFAULT_GENERIC_RECORD_BYTEARRAY_VALYE = ByteBuffer.wrap(DefaultValues.DEFAULT_BYTE_ARRAY_VALUE);

    public static final short[] DEFAULT_SHORT_ARRAY_VALUE = new short[] {};

    public static final String DEFAULT_STRING_VALUE = null;

    public static final boolean DEFAULT_BOOLEAN_VALUE = false;

    public static final long DEFAULT_LONG_VALUE = Long.MIN_VALUE;

    public static final double DEFAULT_DOUBLE_VALUE = Double.MIN_VALUE;

    public static final double[] DEFAULT_DOUBLE_ARRAY_VALUE = new double[] {};

    public static final boolean[] DEFAULT_BOOLEAN_ARRAY_VALUE = null;

    public static final long[] DEFAULT_LONG_ARRAY_VALUE = new long[] {};

    public static final String[] DEFAULT_STRING_ARRAY_VALUE = null;

    public static final int[] DEFAULT_INT_ARRAY_VALUE = new int[] {};

    private DefaultValues() {

    }

}
