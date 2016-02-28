/*
 * Copyright (C) 2016 Tuenti Technologies
 *
 * This file is part of Ikusth.
 *
 * ChkBugReport is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 2 of the License, or
 * (at your option) any later version.
 *
 * ChkBugReport is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with ChkBugReport.  If not, see <http://www.gnu.org/licenses/>.
 */
package com.tuenti.ikusth;

import java.io.File;
import java.io.FileOutputStream;

public class ThreadsGraphOutputWriter {
    private static final String OUTPUT_FILENAME = "ikusth";

    private final byte[] img;
    private final String type;

    public ThreadsGraphOutputWriter(byte[] img, String type) {
        this.img = img;
        this.type = type;
    }

    public String run() {
        return writeGraphToFile(img);
    }

    private String writeGraphToFile(byte[] img) {
        String outputPathName = getGraphOutputPathName();
        File file = new File(outputPathName);
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            fileOutputStream.write(img);
            fileOutputStream.close();
        } catch (java.io.IOException ioe) {
            ioe.printStackTrace();
        }
        return outputPathName;
    }

    private String getGraphOutputPathName() {
        return System.getProperty("java.io.tmpdir") + OUTPUT_FILENAME + "_" + System.currentTimeMillis() + "." + type;
    }
}
