/*
 * Copyright (c) 2009-2016 Dmytro Pishchukhin (http://knowhowlab.org)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package org.knowhowlab.osgi.niis.utils;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

/**
 * @author dpishchukhin
 */
public class CIDR {
    private InetAddress inetAddress;
    private InetAddress startAddress;
    private InetAddress endAddress;
    private final int prefixLength;

    /**
     * @param cidr
     * @return
     * @throws UnknownHostException
     * @throws IllegalArgumentException
     * @throws NullPointerException
     */
    public static CIDR of(String cidr) throws UnknownHostException {
        if (cidr == null) {
            throw new NullPointerException("CIDR is null");
        } else if (!cidr.contains("/")) {
            throw new IllegalArgumentException("Invalid CIDR format");
        } else {
            return new CIDR(cidr);
        }
    }

    private CIDR(String cidr) throws UnknownHostException {
        /* split CIDR to address and prefix part */
        int index = cidr.indexOf("/");
        String addressPart = cidr.substring(0, index);
        String networkPart = cidr.substring(index + 1);

        inetAddress = InetAddress.getByName(addressPart);
        prefixLength = Integer.parseInt(networkPart);

        calculate();
    }

    private void calculate() throws UnknownHostException {
        int length = inetAddress.getAddress().length;

        ByteBuffer maskBuffer = ByteBuffer.allocate(length);

        if (length == 4) {
            maskBuffer.putInt(-1);
        } else {
            maskBuffer.putLong(-1L).putLong(-1L);
        }

        BigInteger mask = (new BigInteger(1, maskBuffer.array())).not().shiftRight(prefixLength);

        ByteBuffer buffer = ByteBuffer.wrap(inetAddress.getAddress());
        BigInteger ipVal = new BigInteger(1, buffer.array());

        BigInteger startIp = ipVal.and(mask);
        BigInteger endIp = startIp.add(mask.not());

        byte[] startIpArr = toBytes(startIp.toByteArray(), length);
        byte[] endIpArr = toBytes(endIp.toByteArray(), length);

        this.startAddress = InetAddress.getByAddress(startIpArr);
        this.endAddress = InetAddress.getByAddress(endIpArr);

    }

    private byte[] toBytes(byte[] array, int targetSize) {
        int counter = 0;
        List<Byte> newArr = new ArrayList<>();
        while (counter < targetSize && (array.length - 1 - counter >= 0)) {
            newArr.add(0, array[array.length - 1 - counter]);
            counter++;
        }

        int size = newArr.size();
        for (int i = 0; i < (targetSize - size); i++) {

            newArr.add(0, (byte) 0);
        }

        byte[] ret = new byte[newArr.size()];
        for (int i = 0; i < newArr.size(); i++) {
            ret[i] = newArr.get(i);
        }
        return ret;
    }

    public InetAddress getNetworkAddress() {
        return this.startAddress;
    }

    public InetAddress getBroadcastAddress() {
        return this.endAddress;
    }

    public boolean contains(InetAddress ipAddress) {
        BigInteger start = new BigInteger(1, this.startAddress.getAddress());
        BigInteger end = new BigInteger(1, this.endAddress.getAddress());
        BigInteger target = new BigInteger(1, ipAddress.getAddress());

        int st = start.compareTo(target);
        int te = target.compareTo(end);

        return (st == -1 || st == 0) && (te == -1 || te == 0);
    }
}
