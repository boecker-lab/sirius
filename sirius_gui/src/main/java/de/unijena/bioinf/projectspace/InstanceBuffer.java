/*
 *  This file is part of the SIRIUS Software for analyzing MS and MS/MS data
 *
 *  Copyright (C) 2013-2020 Kai Dührkop, Markus Fleischauer, Marcus Ludwig, Martin A. Hoffman, Fleming Kretschmer, Marvin Meusel and Sebastian Böcker,
 *  Chair of Bioinformatics, Friedrich-Schiller University.
 *
 *  This program is free software; you can redistribute it and/or
 *  modify it under the terms of the GNU Affero General Public License
 *  as published by the Free Software Foundation; either
 *  version 3 of the License, or (at your option) any later version.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 *  Affero General Public License for more details.
 *
 *  You should have received a copy of the GNU Affero General Public License along with SIRIUS.  If not, see <https://www.gnu.org/licenses/agpl-3.0.txt>
 */

package de.unijena.bioinf.projectspace;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class InstanceBuffer {
    private final Lock lock = new ReentrantLock();
    private final int maxSize;
    private final LinkedHashSet<Instance> buffer;

    public InstanceBuffer(int maxSize) {
        this.maxSize = maxSize;
        buffer = new LinkedHashSet<>(maxSize + 1);
    }

    public void add(Instance instanceBean) {
        lock.lock();
        try {
            buffer.remove(instanceBean);
            buffer.add(instanceBean);
            if (buffer.size() > maxSize) //remove the oldest instance
                remove(buffer.iterator().next());
        } finally {
            lock.unlock();
        }
    }

    public boolean remove(Instance toRemove) {
        lock.lock();
        try {
            if (buffer.remove(toRemove)) {
                if (!toRemove.isComputing()) {
                    toRemove.clearFormulaResultsCache();
                    toRemove.clearCompoundCache();
                }
                return true;
            }
            return false;
        } finally {
            lock.unlock();
        }
    }

    public void removeAllLazy(Collection<Instance> insts) {
        lock.lock();
        try {
            buffer.removeAll(insts);
        } finally {
            lock.unlock();
        }
    }
}
