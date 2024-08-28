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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.util;

import java.util.Arrays;

/**
 * @author aia
 */
public class DataConverterHelper {

    private static final int SIZE = 256;

    private static final String[] BYTE_TO_STRING_DIRECT_MAP = initByteTostringDirectMap(SIZE);
    private static final char[] HEX_CHAR_ARRAY = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    /**
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @return boolean
     */
    public boolean isMarkedInvalid(final byte[] data, final int offset) {
        // Get the validation bit (MSB) from byte
        final int validationByte = getParamByte(data[offset], 0, 1);

        return (validationByte != 0);
    }

    /**
     * @param byteValue
     *            - byte
     * @param bitPos
     *            - bit position
     * @param bits
     *            - bits
     * @return byte
     */
    public byte getParamByte(final byte byteValue, final int bitPos, final int bits) {

        final int startBit = bitPos % 8;
        // Determine the positions for the first unpacked byte in the parameter
        final int bitsToRead = (bits % 8 == 0 ? 8 : bits % 8);

        int value = byteValue & 0x000000ff;
        value <<= 8;

        // Shift and and out the bits we are interested in
        value >>= 16 - startBit - bitsToRead;
        value &= 0x000000ff >> (8 - bitsToRead);

        return (byte) value;
    }

    /**
     * Returns a byte array segment as a long integer, assumes an unsigned integer
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to convert
     * @return The long representation of the byte array segment
     */
    public long getByteArrayUnsignedInteger(final byte[] data, final int offset, final int bytes) {
        return getByteArrayInteger(data, offset, bytes, false);
    }

    /**
     * Returns a byte array segment as a long integer
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to convert
     * @param signed
     *            if this integer value is signed
     * @return The long representation of the byte array segment
     */
    public long getByteArrayInteger(final byte[] data, final int offset, final int bytes, final boolean signed) {
        long paramValue = 0;

        for (int i = offset; i < offset + bytes; i++) {
            paramValue = paramValue << 8 | 0x00000000000000ff & data[i];
        }

        if (bytes < 8 && signed) {
            if (paramValue >> bytes * 8 - 1 != 0) {
                for (int i = bytes; i < 8; i++) {
                    final long maskValue = (long) 0xff << i * 8;
                    paramValue = paramValue | maskValue;
                }
            }
        }

        return paramValue;
    }

    /**
     * Returns a byte array segment as an IBCD number
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to extract
     * @param bits
     *            the number of bits to extract
     * @return string
     */
    public String getByteArrayIBCD(final byte[] data, final int offset, final int bytes, final int bits) {

        final int[] ibcdValues = new int[bytes * 2];

        for (int i = bytes * 2 - 1, j = offset; j < offset + bytes; i -= 2, j++) {
            ibcdValues[i - 1] = data[j] & 0x0000000f;
            ibcdValues[i] = (data[j] & 0x000000f0) >> 4;
        }

        final StringBuilder builder = new StringBuilder(bytes * 2);

        for (int i = 0; i < bits / 4 && ibcdValues[i] < 10; i++) {
            builder.append(HEX_CHAR_ARRAY[ibcdValues[i]]);
        }

        return builder.toString();
    }

    /**
     * Returns a byte array segment as a TBCD number
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to extract
     * @return The byte array segment as an IBCD number
     */
    public byte[] getByteArrayTBCD(final byte[] data, final int offset, final int bytes) {
        final byte[] tbcdValue = new byte[bytes];

        for (int i = offset, j = 0; i < bytes + offset; i++, j++) {
            int value = (data[i] & 0x0000000f) << 4;

            value |= (data[i] & 0x000000f0) >> 4;

            tbcdValue[j] = (byte) value;
        }

        return tbcdValue;
    }

    /**
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @param bytes
     *            - bytes
     * @return string
     */
    public String getByteArrayHexStringNoF(final byte[] data, final int offset, final int bytes) {

        final StringBuilder builder = new StringBuilder(bytes * 2);

        int value;

        for (int i = 0; i < bytes; i++) {
            value = data[i + offset] & 0xff;

            final int highNibble = value / 16;
            if (highNibble < 15) {
                builder.append(HEX_CHAR_ARRAY[highNibble]);
            }
            final int lowNibble = value % 16;
            if (lowNibble < 15) {
                builder.append(HEX_CHAR_ARRAY[lowNibble]);
            }

        }

        return builder.toString();
    }

    /**
     * Converts an entire byte array to a hex string
     *
     * @param data
     *            the byte array
     * @param offset
     *            - offset
     * @param bytes
     *            - bytes
     * @return the hex string
     */
    public String getByteArrayDottedDecimalString(final byte[] data, final int offset, final int bytes) {
        final StringBuilder builder = new StringBuilder(bytes * 3);

        boolean isSet = false;

        for (int i = offset; i < bytes + offset; i++) {
            if (i != offset) {
                builder.append('.');
            }

            builder.append(BYTE_TO_STRING_DIRECT_MAP[data[i] & 0x00ff]);

            if (!isSet && data[i] != -1) {
                isSet = true;
            }
        }

        if (isSet) {
            return builder.toString();
        }
        return null;
    }

    /**
     * Converts an entire byte array to a hex string IPv6
     *
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @param bytes
     *            - bytes
     * @return string
     */
    public String getByteArrayIPV6String(final byte[] data, final int offset, final int bytes) {
        final StringBuilder builder = new StringBuilder(bytes * 3);

        for (int i = offset; i < bytes + offset; i += 2) {
            if (i != offset) {
                builder.append(':');
            }
            builder.append(Integer.toHexString(((data[i] << 8) & 0xff00) | (data[i + 1] & 0x00ff)));
        }

        return builder.toString();
    }

    /**
     * Returns a byte array segment as a string
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to extract
     * @return The byte array segment as a string
     */
    public String getString(final byte[] data, final int offset, final int bytes) {
        final StringBuilder builder = new StringBuilder();

        for (int i = offset; i < bytes + offset; i++) {
            if (data[i] != 0) {
                builder.append((char) data[i]);
            }
        }

        return builder.toString();
    }

    /**
     * Returns a byte array segment as a float value
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to convert
     * @return The boolean value
     */
    public double getByteArrayDouble(final byte[] data, final int offset, final int bytes) {
        final byte[] dataset = Arrays.copyOfRange(data, offset, offset + bytes);
        if (dataset.length > 0) {
            return Double.longBitsToDouble(byteArray2Long(dataset));
        }
        return 0;
    }

    /**
     * method to convert a byte array to a long
     *
     * @param data
     *            The byte array
     * @return the long value
     */
    public long byteArray2Long(final byte[] data) {
        if (data == null || data.length != 8) {
            return 0x0;
        }
        return (long) (0xff & data[0]) << 56 | (long) (0xff & data[1]) << 48 | (long) (0xff & data[2]) << 40 | (long) (0xff & data[3]) << 32
                | (long) (0xff & data[4]) << 24 | (long) (0xff & data[5]) << 16 | (long) (0xff & data[6]) << 8 | (long) (0xff & data[7]) << 0;
    }

    /**
     * @param size
     * @return String array of with string numeric values
     */
    private static String[] initByteTostringDirectMap(final int size) {
        final String[] directAddressMap = new String[size];
        for (int j = 0; j < size; j++) {
            directAddressMap[j] = String.valueOf(j);
        }
        return directAddressMap;
    }

    /**
     * Returns a byte array segment as a DNS name string. The DNS name is made up of a string of words, with the word length of each word separating
     * the words. This method uses the word length values to jump through the string and delimits the words with dot characters
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to extract
     * @return The byte array segment as a DNS name
     */
    public String getByteArray3GPPString(final byte[] data, final int offset, final int bytes) {
        int length = bytes;
        if (length == -1) {
            length = (int) getByteArrayInteger(data, offset - 2, 2, DataConverters.UNSIGNED_FLAG);
        }

        if (length < 0) {
            return null;
        }

        final StringBuilder builder = new StringBuilder();

        for (int dnsOffset = offset; dnsOffset < offset + length - 1;) {
            final int wordLength = data[dnsOffset++];

            if (wordLength <= 0 || data[dnsOffset] < 0) {
                break;
            }

            final String dnsWord = new String(data, dnsOffset, wordLength).trim();

            if (dnsWord.length() > 0) {
                if (dnsOffset > offset + 1) {
                    builder.append('.');
                }

                builder.append(dnsWord);
            }

            dnsOffset += wordLength;
        }

        if (builder.length() == 0) {
            return null;
        }
        return builder.toString();
    }

    /**
     * Returns a byte array segment as a separate byte array
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to extract
     * @return The segment of the byte array
     */
    public Byte[] getByteArrayByteArray(final byte[] data, final int offset, int bytes) {
        final int offsetToUse = offset;
        if (bytes == -1) {
            bytes = (int) getByteArrayInteger(data, offsetToUse - 2, 2, DataConverters.UNSIGNED_FLAG);
        }

        if (bytes < 0) {
            return new Byte[] { 0 };
        }

        final Byte[] subByteArray = new Byte[bytes];

        for (int i = 0; i < bytes; i++) {
            subByteArray[i] = data[offsetToUse + i];
        }

        return subByteArray;
    }

    /**
     * Returns a primitive byte array segment as a separate byte array
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to extract
     * @return The segment of the byte array
     */
    public byte[] getPrimitiveByteArray(final byte[] data, final int offset, int bytes) {
        final int offsetToUse = offset;
        if (bytes == -1) {
            bytes = (int) getByteArrayInteger(data, offsetToUse - 2, 2, DataConverters.UNSIGNED_FLAG);
        }

        if (bytes < 0) {
            return new byte[] { 0 };
        }

        final byte[] subByteArray = new byte[bytes];

        System.arraycopy(data, offsetToUse, subByteArray, 0, bytes);

        return subByteArray;
    }
}
