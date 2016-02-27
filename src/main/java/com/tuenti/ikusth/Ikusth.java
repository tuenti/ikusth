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

import com.sonyericsson.chkbugreport.BugReportModule;
import com.sonyericsson.chkbugreport.ExternalPlugin;
import com.sonyericsson.chkbugreport.Module;
import com.sonyericsson.chkbugreport.Plugin;

public class Ikusth extends Plugin implements ExternalPlugin {

    @Override
    public void initExternalPlugin(Module module) {
        if (module instanceof BugReportModule) {
            module.addPlugin(this);
        }
    }

    @Override
    public int getPrio() {
        return 100;
    }

    @Override
    public void reset() {
        System.out.println("reset");
    }

    @Override
    public void load(Module module) {
        System.out.println("load");
    }

    @Override
    public void generate(Module module) {
        System.out.println("generate");
    }
}
