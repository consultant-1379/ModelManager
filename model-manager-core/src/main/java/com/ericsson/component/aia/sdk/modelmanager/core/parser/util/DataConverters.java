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
package com.ericsson.component.aia.sdk.modelmanager.core.parser.util;

import com.ericsson.component.aia.sdk.modelmanager.core.parser.model.DefaultValues;

/**
 * This class provides static utility methods for handling SQL and tables
 *
 */
public class DataConverters {

    static {
        dataConverterHelper = new DataConverterHelper();
    }

    public final static boolean UNSIGNED_FLAG = false;

    public final static boolean SIGNED_FLAG = true;

    private static final DataConverterHelper dataConverterHelper;

    private DataConverters() {

    }

    /**
     * This method is used to control commas in string output Returns first string if the boolean value is true, second if false
     *
     * @param flag
     *            the flag that controls the return value
     * @param trueString
     *            the string returned on true
     * @param falseString
     *            the string returned on false
     * @return the true or false string
     */
    public static String flagString(final boolean flag, final String trueString, final String falseString) {
        if (flag) {
            return trueString;
        }
        return falseString;
    }

    /**
     * Converts a long value to a hex string with leading zeros
     *
     * @param value
     *            the long value
     * @param digits
     *            number of digits that should be in the hex string
     * @return the hex string
     */
    public static String getLongHexString(final long value, final int digits) {
        String longHexString = Long.toHexString(value);

        for (int i = longHexString.length(); i < digits; i++) {
            longHexString = '0' + longHexString;
        }

        if (longHexString.length() > digits) {
            return longHexString.substring(longHexString.length() - digits, longHexString.length());
        }
        return longHexString;
    }

    /**
     * Returns a byte array segment as a long integer, assumes a signed integer
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            the number of bytes to convert
     * @return The long representation of the byte array segment
     */
    public static long getByteArraySignedInteger(final byte[] data, final int offset, final int bytes) {
        return dataConverterHelper.getByteArrayInteger(data, offset, bytes, true);
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
    public static long getByteArrayUnsignedInteger(final byte[] data, final int offset, final int bytes) {
        return dataConverterHelper.getByteArrayUnsignedInteger(data, offset, bytes);
    }

    /**
     * Returns a byte array segment as a boolean value
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return boolean value
     */
    public static boolean getByteArrayBoolean(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                              final boolean validLTEembeddedbitFlag, final int bytes) {
        int offsetToUse = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_BOOLEAN_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetToUse = offset + 1;
            }
        }
        return dataConverterHelper.getByteArrayInteger(data, offsetToUse, bytes, false) == 0 ? true : false;
    }

    /**
     * Returns a byte array segment as a float value
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return The float value
     */
    public static float getByteArrayFloat(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                          final boolean validLTEembeddedbitFlag, final int bytes) {
        return 0;
    }

    /**
     * Returns a byte array segment as a float value
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return The double value
     */
    public static double getByteArrayDouble(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                            final boolean validLTEembeddedbitFlag, final int bytes) {
        int offsetToUse = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_DOUBLE_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetToUse = offset + 1;
            }
        }
        return dataConverterHelper.getByteArrayDouble(data, offsetToUse, bytes);
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
    public static long getByteArrayInteger(final byte[] data, final int offset, final int bytes, final boolean signed) {
        return dataConverterHelper.getByteArrayInteger(data, offset, bytes, signed);
    }

    /**
     * Converts a byte array segment to a hex string
     *
     * @param data
     *            the byte array
     * @param offset
     *            the offset in the byte array to start at
     * @param bytes
     *            the number of bytes to convert
     * @return the hex string
     */
    public static String getByteArrayHexString(final byte[] data, final int offset, final int bytes) {
        final char[] hexCharArray = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

        final char[] hexChars = new char[bytes * 2];

        int value;

        for (int i = 0; i < bytes; i++) {
            value = data[i + offset] & 0xff;

            hexChars[i * 2] = hexCharArray[value / 16];
            hexChars[i * 2 + 1] = hexCharArray[value % 16];
        }

        return new String(hexChars);
    }

    /**
     * Returns a byte array segment as a separate byte array
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return The segment of the byte array
     */
    public static byte[] getByteArrayByteArray(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                               final boolean validLTEembeddedbitFlag, int bytes) {

        int offsetToUse = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_BYTE_ARRAY_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetToUse = offset + 1;
            }
        }
        if (bytes == -1) {
            bytes = (int) dataConverterHelper.getByteArrayInteger(data, offsetToUse - 2, 2, DataConverters.UNSIGNED_FLAG);
        }

        if (bytes < 0) {
            return new byte[] { 0 };
        }

        final byte[] subByteArray = new byte[bytes];

        for (int i = 0; i < bytes; i++) {
            subByteArray[i] = data[offsetToUse + i];
        }

        return subByteArray;
    }

    private static boolean isMarkedInvalid(final byte[] data, final int offset) {
        // Get the validation bit (MSB) from byte
        final int validationByte = getParamByte(data[offset], 0, 1);

        return (validationByte != 0);
    }

    /**
     * Returns a byte array segment as a DNS name string. The DNS name is made up of a string of words, with the word length of each word separating
     * the words. This method uses the word length values to jump through the string and delimits the words with dot characters
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return String : The byte array segment as a DNS name
     */
    public static String getByteArray3GPPString(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                                final boolean validLTEembeddedbitFlag, final int bytes) {
        int offsetToUse = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_STRING_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetToUse = offset + 1;
            }
        }

        return dataConverterHelper.getByteArray3GPPString(data, offsetToUse, bytes);
    }

    /**
     * Returns a byte array segment in C++ format ( a string of characters terminated by '\0') as a string.
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return The byte array segment as string
     */
    public static String getByteArrayCCString(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                              final boolean validLTEembeddedbitFlag, final int bytes) {
        int length = bytes;
        if (length == -1) {
            length = (int) dataConverterHelper.getByteArrayInteger(data, offset - 2, 2, DataConverters.UNSIGNED_FLAG);
        }

        if (length < 0) {
            return null;
        }

        return new String(data, offset, length);
    }

    /**
     * Returns a byte array segment as an IBCD number
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @param bits
     *            - bits
     * @return The byte array segment as an IBCD number
     */
    public static String getByteArrayIBCD(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                          final boolean validLTEembeddedbitFlag, final int bytes, final int bits) {
        int offsetToUse = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_STRING_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetToUse = offset + 1;
            }
        }

        return dataConverterHelper.getByteArrayIBCD(data, offsetToUse, bytes, bits);
    }

    /**
     * Returns a byte array segment as a TBCD number
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param bytes
     *            number of bytes
     * @return The byte array segment as an TBCD number
     */
    public static long getByteArrayTBCDInteger(final byte[] data, final int offset, final int bytes) {
        final byte[] tbcdValue = dataConverterHelper.getByteArrayTBCD(data, offset, bytes);

        long tbcd = 0;

        for (int i = 0; i < tbcdValue.length; i++) {
            tbcd = tbcd * 10 + ((tbcdValue[i] & 0x000000f0) >> 4);

            if ((tbcdValue[i] & 0x0000000f) != 0x0000000f) {
                tbcd = tbcd * 10 + (tbcdValue[i] & 0x0000000f);
            }
        }

        return tbcd;
    }

    /**
     * Returns a byte array segment as a TBCD string
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return The byte array segment as an TBCD number string
     */
    public static String getByteArrayTBCDString(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                                final boolean validLTEembeddedbitFlag, final int bytes) {
        int offsetForData = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_STRING_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetForData = offset + 1;
            }
        }
        final byte[] tbcdValue = dataConverterHelper.getByteArrayTBCD(data, offsetForData, bytes);

        return dataConverterHelper.getByteArrayHexStringNoF(tbcdValue, 0, tbcdValue.length);
    }

    /**
     * Returns a byte array segment as a string
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return The byte array segment as a string
     */
    public static String getString(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                   final boolean validLTEembeddedbitFlag, final int bytes) {
        int offsetForData = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_STRING_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetForData = offset + 1;
            }
        }
        return dataConverterHelper.getString(data, offsetForData, bytes);
    }

    /**
     * Converts an entire byte array to a hex string
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return dotted decimal String i.e. IPV4 127.0.0.1
     */
    public static String getByteArrayDottedDecimalString(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                                         final boolean validLTEembeddedbitFlag, final int bytes) {
        int offsetForData = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_STRING_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetForData = offset + 1;
            }
        }

        return dataConverterHelper.getByteArrayDottedDecimalString(data, offsetForData, bytes);

    }

    /**
     * Converts an entire byte array to a hex string IPv6
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param bytes
     *            number of bytes
     * @return IPV6 String value
     */
    public static String getByteArrayIPV6String(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                                final boolean validLTEembeddedbitFlag, final int bytes) {
        int offsetForData = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_STRING_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetForData = offset + 1;
            }
        }
        return dataConverterHelper.getByteArrayIPV6String(data, offsetForData, bytes);
    }

    /**
     * Converts an entire byte array to a hex string
     *
     * @param data
     *            the byte array
     * @return the hex string
     */
    public static String byteArray2String(final byte[] data) {
        return getByteArrayHexString(data, 0, data.length);
    }

    /**
     * Returns a string as a byte array
     *
     * @param hexString
     *            the byte array to use
     * @return The byte array
     */
    public static byte[] string2ByteArray(final String hexString) {
        final byte[] byteArray = new byte[hexString.length() / 2];

        for (int i = 0; i < hexString.length(); i += 2) {
            final short shortValue = Short.valueOf(hexString.substring(i, i + 2), 16);
            byteArray[i / 2] = (byte) shortValue;
        }
        return byteArray;
    }

    /**
     * Method to compare two byte arrays
     *
     * @param byteArray1
     *            The first byte array
     * @param byteArray2
     *            The second byte array
     * @return the comparison result as an integer
     */
    public static int compareByteArrays(final byte[] byteArray1, final byte[] byteArray2) {
        if (byteArray1.length != byteArray2.length) {
            return byteArray1.length - byteArray2.length;
        }

        for (int i = 0; i < byteArray1.length; i++) {
            if (byteArray1[i] != byteArray2[i]) {
                return byteArray1[i] - byteArray2[i];
            }
        }

        return 0;
    }

    private static byte getParamByte(final byte byteValue, final int bitPos, final int bits) {

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
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @param isUseValid
     *            - isUsedValid
     * @param isOptional
     *            - isOptional
     * @param validLTEembeddedbitFlag
     *            - validLTEembeddedbitFlag
     * @param numberOfBytes
     *            -numberOfBytes
     * @return bytes
     */
    public static byte getByte(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                               final boolean validLTEembeddedbitFlag, final int numberOfBytes) {
        int offsetToUse = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_BYTE_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetToUse = offset + 1;
            }
        }
        return (byte) getByteArrayUnsignedInteger(data, offsetToUse, numberOfBytes);
    }

    /**
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @param isUseValid
     *            - isUseValid
     * @param isOptional
     *            - isOptional
     * @param validLTEembeddedbitFlag
     *            -validLTEembeddedbitFlag
     * @param numberOfBytes
     *            - numberOfBytes
     * @return short
     */
    public static short getShort(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                 final boolean validLTEembeddedbitFlag, final int numberOfBytes) {
        int offsetToUse = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_SHORT_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetToUse = offset + 1;
            }
        }
        return (short) getByteArrayUnsignedInteger(data, offsetToUse, numberOfBytes);
    }

    /**
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @param isUseValid
     *            - isUseValid
     * @param isOptional
     *            - isOptional
     * @param validLTEembeddedbitFlag
     *            -validLTEembeddedbitFlag
     * @param numberOfBytes
     *            - numberOfBytes
     * @return long
     */
    public static long getLong(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                               final boolean validLTEembeddedbitFlag, final int numberOfBytes) {
        int offsetToUse = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_LONG_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetToUse = offset + 1;
            }
        }
        return getByteArrayUnsignedInteger(data, offsetToUse, numberOfBytes);
    }

    /**
     * Returns a byte array segment as a long integer, assumes an unsigned integer
     *
     * @param data
     *            the byte array to use
     * @param offset
     *            the offset to start at
     * @param isUseValid
     *            true if contain validity byte
     * @param isOptional
     *            true if contain optional byte
     * @param validLTEembeddedbitFlag
     *            true if contain LTE validity bit in MSB
     * @param numberOfBytesToConvert
     *            -numberOfBytesToConvert
     * @return The long representation of the byte array segment
     */
    public static int getUnsignedIntegerAsInteger(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                                  final boolean validLTEembeddedbitFlag, final int numberOfBytesToConvert) {
        int offsetForData = offset;
        if (isUseValid || isOptional) {
            if (isMarkedInvalid(data, offset)) {
                return DefaultValues.DEFAULT_INT_VALUE;
            }
            if (validLTEembeddedbitFlag) {
                // get rest of 7 bit from bytes and replace original byte i.e. result byte has no validity bit
                data[offset] = getParamByte(data[offset], 1, 7);
            } else {
                offsetForData = offset + 1;
            }
        }
        return (int) dataConverterHelper.getByteArrayInteger(data, offsetForData, numberOfBytesToConvert, false);
    }

    /**
     * @param data
     *            - data
     * @param offset
     *            - offset
     * @param isUseValid
     *            - isUseValid
     * @param isOptional
     *            - isOptional
     * @param validLTEembeddedbitFlag
     *            -validLTEembeddedbitFlag
     * @param numberOfBytesToConvert
     *            -numberOfBytesToConvert
     * @return short array
     */
    public static short[] getShortArray(final byte[] data, final int offset, final boolean isUseValid, final boolean isOptional,
                                        final boolean validLTEembeddedbitFlag, final int numberOfBytesToConvert) {
        //not supported in decoding ... only used in correlation pojos
        return DefaultValues.DEFAULT_SHORT_ARRAY_VALUE;
    }
}
