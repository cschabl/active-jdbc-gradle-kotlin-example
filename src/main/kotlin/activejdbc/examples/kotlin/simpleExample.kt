package activejdbc.examples.kotlin

import org.javalite.activejdbc.Base
import org.slf4j.LoggerFactory

val logger = LoggerFactory.getLogger("activejdbc.examples.kotlin.simpleExample")

fun main(args: Array<String>) {

    Base.open("org.h2.Driver", "jdbc:h2:./build/activejdbc-sample", "sa", "")

    try {
        createDbSchema()
        logger.info("=========> DB schema created")
        createEmployee()
        logger.info("=========> Created employee:")
        selectEmployee()
        updateEmployee()
        logger.info("=========> Updated employee:")
        deleteEmployee()
        logger.info("=========> Deleted employee:")

    } finally {
        Base.close()
    }
}

fun createDbSchema()
{
    Base.exec("DROP TABLE IF EXISTS employees")
    Base.exec("CREATE TABLE employees (\n" +
            "      id  int(11) NOT NULL  auto_increment PRIMARY KEY,\n" +
            "      first_name VARCHAR(56),\n" +
            "      last_name VARCHAR(56))")
}

fun createEmployee() {
    val e = Employee()
    e.set<Employee>("first_name", "John")
    e.set<Employee>("last_name", "Doe")
    e.saveIt()
}

fun selectEmployee() {
    val e = Employee.findFirst("first_name = ?", "John")
    logger.info(e!!.toString())
}

fun updateEmployee() {
    val e: Employee? = Employee.findFirst("first_name = ?", "John")

    e!!.set<Employee>("last_name", "Steinbeck").saveIt()
}

fun deleteEmployee() {
    val e: Employee? = Employee.findFirst("first_name = ?", "John")

    e!!.delete()
}