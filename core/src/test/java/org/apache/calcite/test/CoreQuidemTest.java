/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to you under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.calcite.test;

import org.apache.calcite.config.CalciteConnectionProperty;

import net.hydromatic.quidem.Quidem;

import java.sql.Connection;
import java.util.Collection;

/**
 * Test that runs every Quidem file in the "core" module as a test.
 */
class CoreQuidemTest extends QuidemTest {
  /** Runs a test from the command line.
   *
   * <p>For example:
   *
   * <blockquote>
   *   <code>java CoreQuidemTest sql/dummy.iq</code>
   * </blockquote> */
  public static void main(String[] args) throws Exception {
    for (String arg : args) {
      new CoreQuidemTest().test(arg);
    }
  }

  /** For {@link QuidemTest#test(String)} parameters. */
  @Override public Collection<String> getPath() {
    // Start with a test file we know exists, then find the directory and list
    // its files.
    final String first = "sql/agg.iq";
    return data(first);
  }

  @Override protected Quidem.ConnectionFactory createConnectionFactory() {
    return new QuidemConnectionFactory() {
      @Override public Connection connect(String name, boolean reference) throws Exception {
        switch (name) {
        case "blank":
          return CalciteAssert.that()
              .with(CalciteConnectionProperty.PARSER_FACTORY,
                  ExtensionDdlExecutor.class.getName() + "#PARSER_FACTORY")
              .with(CalciteAssert.SchemaSpec.BLANK)
              .connect();
        case "scott":
          return CalciteAssert.that()
              .with(CalciteConnectionProperty.PARSER_FACTORY,
                  ExtensionDdlExecutor.class.getName() + "#PARSER_FACTORY")
              .with(CalciteAssert.Config.SCOTT)
              .connect();
        default:
          return super.connect(name, reference);
        }
      }
    };
  }

  /** Override settings for "sql/misc.iq". */
  public void testSqlMisc(String path) throws Exception {
    switch (CalciteAssert.DB) {
    case ORACLE:
      // There are formatting differences (e.g. "4.000" vs "4") when using
      // Oracle as the JDBC data source.
      return;
    }
    checkRun(path);
  }
}
