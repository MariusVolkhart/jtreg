/*
 * Copyright (c) 2012, 2017, Oracle and/or its affiliates. All rights reserved.
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

import java.applet.Applet;
import java.awt.*;
import java.io.*;
import java.util.*;

public class AppletTest extends Applet {

    public void start() {
        super.start();

        try {
            ref = new Properties();
            InputStream in = new FileInputStream(System.getenv("refprops"));
            try {
                ref.load(in);
            } finally {
                in.close();
            }
        } catch (IOException e) {
            throw new Error(e);
        }

        String lib = getParameter("LIB");
        run(lib == null ? new String[0] : new String[] { lib });
    }

    public void paint(Graphics g) {
        g.drawString("Trivial Pass", 50, 25);
    }

    public Dimension getPreferredSize() {
        return new Dimension(100, 300);
    }

    void run(String[] args) {
        String lib = (args.length == 0) ? null : args[0];
        check("test.src", testSrc("applet"));
        check("test.src.path", path(testSrc("applet"), testSrc(lib)));
        check("test.classes", testClasses("applet"));
        check("test.class.path", path(testClasses("applet"), testLibClasses(lib)));
        check("test.vm.opts");
        check("test.tool.vm.opts");
        check("test.compiler.opts");
        check("test.java.opts");
        check("test.jdk");
        check("compile.jdk");

        if (errors > 0)
            throw new Error(errors + " errors occurred");
    }

    void check(String name) {
        check(name, ref.getProperty(name));
    }

    void check(String name, String value) {
        System.err.println("check: " + name + ": " + value);
        String v = System.getProperty(name);
        if (v == null || !v.equals(value)) {
            error(name + ": unexpected value"
                + "\n     found: " + v
                + "\n  expected: " + value);
        }
    }

    String testSrc(String p) {
        return (p == null) ? null : file(ref.getProperty("testRoot"), p);
    }

    String testClasses(String p) {
        return file(ref.getProperty("classRoot"), p, getParameter("name") + ".d");
    }

    String testLibClasses(String p) {
        return (p == null) ? null : file(ref.getProperty("classRoot"), p);
    }

    String file(String... list) {
       return join(list, File.separator);
    }

    String path(String... list) {
        return join(list, File.pathSeparator);
    }

    String join(String[] list, String sep) {
        StringBuilder sb = new StringBuilder();
        for (String item: list) {
            if (item != null) {
                if (sb.length() > 0) sb.append(sep);
                sb.append(item);
            }
        }
        return sb.toString();
    }

    void error(String msg) {
        System.err.println("Error: " + msg);
        errors++;
    }

    Properties ref;
    int errors;
}
