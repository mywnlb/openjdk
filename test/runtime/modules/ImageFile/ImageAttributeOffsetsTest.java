/*
 * Copyright (c) 2015, Oracle and/or its affiliates. All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 */

/*
 * @test ImageAttributeOffsetsTest
 * @summary Unit test for JVM_ImageAttributeOffsets() method
 * @author sergei.pikalev@oracle.com
 * @library /testlibrary /../../test/lib
 * @build ImageAttributeOffsetsTest
 * @run main ClassFileInstaller sun.hotspot.WhiteBox
 *                              sun.hotspot.WhiteBox$WhiteBoxPermission
 * @run main/othervm -Xbootclasspath/a:. -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI -XX:+MemoryMapImage ImageAttributeOffsetsTest +
 * @run main/othervm -Xbootclasspath/a:. -XX:+UnlockDiagnosticVMOptions -XX:+WhiteBoxAPI -XX:-MemoryMapImage ImageAttributeOffsetsTest -
 */

import sun.hotspot.WhiteBox;
import java.nio.ByteOrder;

public class ImageAttributeOffsetsTest {

    public static final WhiteBox wb = WhiteBox.getWhiteBox();

    public static void main(String... args) throws Exception {
        String javaHome = System.getProperty("java.home");
        String imageFile = javaHome + "/lib/modules/bootmodules.jimage";

        boolean isMMap = true;
        for (String arg : args)
            if (arg.equals("-"))
                isMMap = false;

        if (!testImageAttributeOffsets(imageFile, isMMap))
            throw new RuntimeException("Some cases are failed");
    }

    private static boolean testImageAttributeOffsets(String imageFile, boolean isMMap) {
        boolean bigEndian = ByteOrder.nativeOrder() == ByteOrder.BIG_ENDIAN;
        long id = wb.imageOpenImage(imageFile, bigEndian);
        boolean passed = true;
        String mm = isMMap? "-XX:+MemoryMapImage" : "-XX:-MemoryMapImage";
        // Get offsets
        int[] array = wb.imageAttributeOffsets(id);
        if (array != null) {
            System.out.printf("Passed. Offsets\' array retrieved  with %s flag", mm);
        } else {
            System.out.printf("Failed. Could not retrieve offsets\' array with %s flag", mm);
            passed = false;
        }

        wb.imageCloseImage(id);
        return passed;
    }
}
