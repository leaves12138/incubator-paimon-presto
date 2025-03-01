/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.apache.paimon.presto;

import org.apache.paimon.data.InternalRow;
import org.apache.paimon.fs.Path;
import org.apache.paimon.fs.local.LocalFileIO;
import org.apache.paimon.schema.Schema;
import org.apache.paimon.schema.SchemaManager;
import org.apache.paimon.table.FileStoreTable;
import org.apache.paimon.table.FileStoreTableFactory;
import org.apache.paimon.table.sink.InnerTableCommit;
import org.apache.paimon.table.sink.InnerTableWrite;
import org.apache.paimon.types.RowType;

import java.util.Collections;
import java.util.HashMap;

/** A simple table test helper to write and commit. */
public class SimpleTableTestHelper {

    private final InnerTableWrite writer;
    private final InnerTableCommit commit;

    public SimpleTableTestHelper(Path path, RowType rowType) throws Exception {
        new SchemaManager(LocalFileIO.create(), path)
                .createTable(
                        new Schema(
                                rowType.getFields(),
                                Collections.emptyList(),
                                Collections.emptyList(),
                                new HashMap<>(),
                                ""));
        FileStoreTable table = FileStoreTableFactory.create(LocalFileIO.create(), path);
        String user = "user";
        this.writer = table.newWrite(user);
        this.commit = table.newCommit(user);
    }

    public void write(InternalRow row) throws Exception {
        writer.write(row);
    }

    public void commit() throws Exception {
        commit.commit(0, writer.prepareCommit(true, 0));
    }
}
