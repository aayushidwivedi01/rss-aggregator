/*-
 *
 *  This file is part of Oracle Berkeley DB Java Edition
 *  Copyright (C) 2002, 2015 Oracle and/or its affiliates.  All rights reserved.
 *
 *  Oracle Berkeley DB Java Edition is free software: you can redistribute it
 *  and/or modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation, version 3.
 *
 *  Oracle Berkeley DB Java Edition is distributed in the hope that it will be
 *  useful, but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU Affero
 *  General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License in
 *  the LICENSE file along with Oracle Berkeley DB Java Edition.  If not, see
 *  <http://www.gnu.org/licenses/>.
 *
 *  An active Oracle commercial licensing agreement for this product
 *  supercedes this license.
 *
 *  For more information please contact:
 *
 *  Vice President Legal, Development
 *  Oracle America, Inc.
 *  5OP-10
 *  500 Oracle Parkway
 *  Redwood Shores, CA 94065
 *
 *  or
 *
 *  berkeleydb-info_us@oracle.com
 *
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  [This line intentionally left blank.]
 *  EOF
 *
 */

package com.sleepycat.util.test;

import static org.junit.Assert.fail;

import org.junit.Test;

import com.sleepycat.util.PackedInteger;

public class PackedIntegerTest extends TestBase {
    static final long V119 = 119L;
    static final long MAX_1 = 0xFFL;
    static final long MAX_2 = 0xFFFFL;
    static final long MAX_3 = 0xFFFFFFL;
    static final long MAX_4 = 0xFFFFFFFFL;
    static final long MAX_5 = 0xFFFFFFFFFFL;
    static final long MAX_6 = 0xFFFFFFFFFFFFL;
    static final long MAX_7 = 0xFFFFFFFFFFFFFFL;

    @Test
    public void runTest() {

        /* Packed int tests. */

        testIntRange(-V119, V119, 1);

        testIntRange(-MAX_1 - V119, -1 - V119, 2);
        testIntRange(1 + V119, MAX_1 + V119, 2);

        testIntRange(-MAX_2 - V119, -MAX_2 + 99, 3);
        testIntRange(-MAX_1 - V119 - 99, -MAX_1 - V119 - 1, 3);
        testIntRange(MAX_1 + V119 + 1, MAX_1 + V119 + 99, 3);
        testIntRange(MAX_2 - 99, MAX_2 + V119, 3);

        testIntRange(-MAX_3 - V119, -MAX_3 + 99, 4);
        testIntRange(-MAX_2 - V119 - 99, -MAX_2 - V119 - 1, 4);
        testIntRange(MAX_2 + V119 + 1, MAX_2 + V119 + 99, 4);
        testIntRange(MAX_3 - 99, MAX_3 + V119, 4);

        testIntRange(Integer.MIN_VALUE, Integer.MIN_VALUE + 99, 5);
        testIntRange(Integer.MAX_VALUE - 99, Integer.MAX_VALUE, 5);

        /* Packed long tests. */

        testLongRange(-V119, V119, 1);

        testLongRange(-MAX_1 - V119, -1 - V119, 2);
        testLongRange(1 + V119, MAX_1 + V119, 2);

        testLongRange(-MAX_2 - V119, -MAX_2 + 99, 3);
        testLongRange(-MAX_1 - V119 - 99, -MAX_1 - V119 - 1, 3);
        testLongRange(MAX_1 + V119 + 1, MAX_1 + V119 + 99, 3);
        testLongRange(MAX_2 - 99, MAX_2 + V119, 3);

        testLongRange(-MAX_3 - V119, -MAX_3 + 99, 4);
        testLongRange(-MAX_2 - V119 - 99, -MAX_2 - V119 - 1, 4);
        testLongRange(MAX_2 + V119 + 1, MAX_2 + V119 + 99, 4);
        testLongRange(MAX_3 - 99, MAX_3 + V119, 4);

        testLongRange(-MAX_4 - V119, -MAX_4 + 99, 5);
        testLongRange(-MAX_3 - V119 - 99, -MAX_3 - V119 - 1, 5);
        testLongRange(MAX_3 + V119 + 1, MAX_3 + V119 + 99, 5);
        testLongRange(MAX_4 - 99, MAX_4 + V119, 5);

        testLongRange(-MAX_5 - V119, -MAX_5 + 99, 6);
        testLongRange(-MAX_4 - V119 - 99, -MAX_4 - V119 - 1, 6);
        testLongRange(MAX_4 + V119 + 1, MAX_4 + V119 + 99, 6);
        testLongRange(MAX_5 - 99, MAX_5 + V119, 6);

        testLongRange(-MAX_6 - V119, -MAX_6 + 99, 7);
        testLongRange(-MAX_5 - V119 - 99, -MAX_5 - V119 - 1, 7);
        testLongRange(MAX_5 + V119 + 1, MAX_5 + V119 + 99, 7);
        testLongRange(MAX_6 - 99, MAX_6 + V119, 7);

        testLongRange(-MAX_7 - V119, -MAX_7 + 99, 8);
        testLongRange(-MAX_6 - V119 - 99, -MAX_6 - V119 - 1, 8);
        testLongRange(MAX_6 + V119 + 1, MAX_6 + V119 + 99, 8);
        testLongRange(MAX_7 - 99, MAX_7 + V119, 8);

        testLongRange(Long.MIN_VALUE, Long.MIN_VALUE + 99, 9);
        testLongRange(Long.MAX_VALUE - 99, Long.MAX_VALUE - 1, 9);

        /* <!-- begin JE only --> */

        /* Reverse-packed int tests. */

        testReverseIntRange(-V119, V119, 1);

        testReverseIntRange(-MAX_1 - V119, -1 - V119, 2);
        testReverseIntRange(1 + V119, MAX_1 + V119, 2);

        testReverseIntRange(-MAX_2 - V119, -MAX_2 + 99, 3);
        testReverseIntRange(-MAX_1 - V119 - 99, -MAX_1 - V119 - 1, 3);
        testReverseIntRange(MAX_1 + V119 + 1, MAX_1 + V119 + 99, 3);
        testReverseIntRange(MAX_2 - 99, MAX_2 + V119, 3);

        testReverseIntRange(-MAX_3 - V119, -MAX_3 + 99, 4);
        testReverseIntRange(-MAX_2 - V119 - 99, -MAX_2 - V119 - 1, 4);
        testReverseIntRange(MAX_2 + V119 + 1, MAX_2 + V119 + 99, 4);
        testReverseIntRange(MAX_3 - 99, MAX_3 + V119, 4);

        testReverseIntRange(Integer.MIN_VALUE, Integer.MIN_VALUE + 99, 5);
        testReverseIntRange(Integer.MAX_VALUE - 99, Integer.MAX_VALUE, 5);

        /* <!-- end JE only --> */
    }

    private void testIntRange(long firstValue,
                              long lastValue,
                              int bytesExpected) {

        byte[] buf = new byte[1000];
        int off = 0;

        for (long longI = firstValue; longI <= lastValue; longI += 1) {
            int i = (int) longI;
            int before = off;
            off = PackedInteger.writeInt(buf, off, i);
            int bytes = off - before;
            if (bytes != bytesExpected) {
                fail("output of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
            bytes = PackedInteger.getWriteIntLength(i);
            if (bytes != bytesExpected) {
                fail("count of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
        }

        off = 0;

        for (long longI = firstValue; longI <= lastValue; longI += 1) {
            int i = (int) longI;
            int bytes = PackedInteger.getReadIntLength(buf, off);
            if (bytes != bytesExpected) {
                fail("count of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
            int value = PackedInteger.readInt(buf, off);
            if (value != i) {
                fail("input of value=" + i + " but got=" + value);
            }
            off += bytes;
        }
    }

    private void testLongRange(long firstValue,
                               long lastValue,
                               int bytesExpected) {

        byte[] buf = new byte[2000];
        int off = 0;

        for (long longI = firstValue; longI <= lastValue; longI += 1) {
            long i = longI;
            int before = off;
            off = PackedInteger.writeLong(buf, off, i);
            int bytes = off - before;
            if (bytes != bytesExpected) {
                fail("output of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
            bytes = PackedInteger.getWriteLongLength(i);
            if (bytes != bytesExpected) {
                fail("count of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
        }

        off = 0;

        for (long longI = firstValue; longI <= lastValue; longI += 1) {
            long i = longI;
            int bytes = PackedInteger.getReadLongLength(buf, off);
            if (bytes != bytesExpected) {
                fail("count of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
            long value = PackedInteger.readLong(buf, off);
            if (value != i) {
                fail("input of value=" + i + " but got=" + value);
            }
            off += bytes;
        }
    }

    /* <!-- begin JE only --> */

    private void testReverseIntRange(long firstValue,
                                     long lastValue,
                                     int bytesExpected) {

        byte[] buf = new byte[1000];
        int off = 0;

        for (long longI = firstValue; longI <= lastValue; longI += 1) {
            int i = (int) longI;
            int before = off;
            off = PackedInteger.writeReverseInt(buf, off, i);
            int bytes = off - before;
            if (bytes != bytesExpected) {
                fail("output of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
            bytes = PackedInteger.getWriteIntLength(i);
            if (bytes != bytesExpected) {
                fail("count of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
        }

        off -= 1;

        for (long longI = lastValue; longI >= firstValue; longI -= 1) {
            int i = (int) longI;
            int bytes = PackedInteger.getReadIntLength(buf, off);
            if (bytes != bytesExpected) {
                fail("count of value=" + i + " bytes=" + bytes +
                     " bytesExpected=" + bytesExpected);
            }
            int value = PackedInteger.readReverseInt(buf, off);
            if (value != i) {
                fail("input of value=" + i + " but got=" + value);
            }
            off -= bytes;
        }
    }

    /* <!-- end JE only --> */
}
