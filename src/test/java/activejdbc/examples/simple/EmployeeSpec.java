/*
Copyright 2009-2010 Igor Polevoy 

Licensed under the Apache License, Version 2.0 (the "License"); 
you may not use this file except in compliance with the License. 
You may obtain a copy of the License at 

http://www.apache.org/licenses/LICENSE-2.0 

Unless required by applicable law or agreed to in writing, software 
distributed under the License is distributed on an "AS IS" BASIS, 
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. 
See the License for the specific language governing permissions and 
limitations under the License. 
*/

package activejdbc.examples.simple;

import activejdbc.examples.kotlin.Employee;
import org.apache.commons.io.IOUtils;
import org.javalite.activejdbc.Base;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static org.javalite.test.jspec.JSpec.the;

/**
 * @author Igor Polevoy
 */
public class EmployeeSpec {

    @BeforeClass
    public static void createSchema() throws IOException, SQLException {
        openConnection();

        try (InputStream rsrcStream = EmployeeSpec.class.getResourceAsStream("/createSchema.sql")) {
            List<String> lines = IOUtils.readLines(rsrcStream, StandardCharsets.UTF_8);
            List<String> stmtLines = new ArrayList<>();

            for (String next : lines) {
                String trimmedLine = next.trim();

                if (trimmedLine.equals("")) {
                    continue;
                }
                if (!trimmedLine.endsWith(";")){
                    stmtLines.add(trimmedLine);
                }
                else {
                    stmtLines.add(trimmedLine.substring(0, trimmedLine.length() - 1));

                    String statement = String.join("\n", stmtLines);
                    PreparedStatement sql = Base.connection().prepareStatement(statement);
                    stmtLines.clear();
                    sql.execute();
                    sql.close();
                }
            }
        }
        finally {
            Base.close();
        }
    }

    @Before
    public void before() throws IOException {
        openConnection();
        Base.openTransaction();
    }

    @After
    public void after(){
        Base.rollbackTransaction();
        Base.close();
    }

    @Test
    public void shouldValidateMandatoryFields(){

        Employee employee = new Employee();

        //check errors
        the(employee).shouldNotBe("valid");
        the(employee.errors().get("first_name")).shouldBeEqual("value is missing");
        the(employee.errors().get("last_name")).shouldBeEqual("value is missing");

        //set missing values
        employee.set("first_name", "John", "last_name", "Doe");
        
        //all is good:
        the(employee).shouldBe("valid");
    }

    private static void openConnection() throws IOException {
        Properties jdbcProps = new Properties();

        try (InputStream is = EmployeeSpec.class.getResourceAsStream("/jdbc.properties"))
        {
            jdbcProps.load(is);
        }
        Base.open(jdbcProps.getProperty("DRIVER"), jdbcProps.getProperty("URL"), jdbcProps.getProperty("USER"),
                jdbcProps.getProperty("PASSWORD"));
    }
}
