package com.gimslab.ktormexam.ktormexam

import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.*
import me.liuwj.ktorm.schema.*
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import java.lang.System.*
import java.time.LocalDate

@SpringBootApplication
class KtormExamApplication {
	constructor() {
		println("start Ktorm Test")
		testktorm()
	}

	private fun testktorm() {
		Database.connect("jdbc:mysql://localhost:3306/ktormtest", driver = "com.mysql.jdbc.Driver", user = "exam", password = "exam")
		for (row in Employees.select())
			println(row[Employees.name])

		println("---- filter")
		val names = Employees
				.select(Employees.name)
				.where { (Employees.departmentId eq 1) and (Employees.name like "%Man%") }
				.map { row -> row[Employees.name] }
		println(names)

		println("---- dynamic query based on conditions")
		val names2 = Employees
				.select(Employees.name)
				.whereWithConditions {
					if (currentTimeMillis().rem(5) < 4L)
						it += Employees.managerId.isNull()
				}
				.map { it.getString(1) }
		println(names2)

		println("---- aggregation")
		val t = Employees
		val salaries = t
				.select(t.departmentId, avg(t.salary))
				.groupBy(t.departmentId)
				.having { avg(t.salary) greater 100.0 }
				.associate { it.getInt(1) to it.getDouble(2) }
		println("salaries=$salaries")

		println("---- joining")
		data class Names(val name: String, val managerName: String?, val departmentName: String)

		val emp = Employees.aliased("emp")
		val mgr = Employees.aliased("mgr")
		val dept = Employees.aliased("dept")
//		val results = emp
//				.leftJoin(dept, on = emp.departmentId eq dept.id)
//				.leftJoin(mgr, on = emp.managerId eq mgr.id)
//				.select(emp.name, mgr.name, dept.name)
//				.orderBy(emp.id.asc())
//				.map{
//					Names(
//							name = it.getString(1),
//							managerName = it.getString(2),
//							departmentName = it.getString(3)
//					)
//				}
//		for (r in results)
//			println(r);

		println("---- insert")
		Employees.insert {
			it.name to "jerry"
			it.job to "trainee"
			it.managerId to 1
			it.hireDate to LocalDate.now()
			it.salary to 50
			it.departmentId to 1
		}

		println("---- update")
		Employees.update {
			it.job to "engineer"
			it.managerId to null
			it.salary to 100

			where {
				it.id eq 2
			}
		}
	}
}

fun main(args: Array<String>) {
	runApplication<KtormExamApplication>(*args)
}

object Departments : Table<Nothing>("t_department") {
	val id by int("id").primaryKey()
	val name by varchar("name")
	val location by varchar("location")
}

object Employees : Table<Nothing>("t_employee") {
	val id by int("id").primaryKey()
	val name by varchar("name")
	val job by varchar("job")
	val managerId by int("manager_id")
	val hireDate by date("hire_date")
	val salary by long("salary")
	val departmentId by int("department_id")
}