package activejdbc.examples.kotlin

import org.javalite.activejdbc.CompanionModel
import org.javalite.activejdbc.Model

open class Employee: Model() {
    companion object: CompanionModel<Employee>(Employee::class.java) {
        init {
            validatePresenceOf("first_name", "last_name")
        }
    }
}