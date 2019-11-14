package com.gimslab.ktormexam

import me.liuwj.ktorm.database.Database
import me.liuwj.ktorm.dsl.select
import me.liuwj.ktorm.schema.Table
import me.liuwj.ktorm.schema.datetime
import me.liuwj.ktorm.schema.long
import me.liuwj.ktorm.schema.varchar
import org.springframework.stereotype.Service
import java.time.LocalDateTime
import javax.sql.DataSource

@Service
class CpDbExam {
	fun testKtorm(pwd: String) {
		Database.connect("jdbc:mysql://db.seller.coupangdev.com:3306/finance3p", driver = "com.mysql.cj.jdbc.Driver", user = "winter", password = pwd)
//		Database.connect(dataSource())
		for (row in GoodsGenObject.select()) {
//			print(row[GoodsGenObject.id].toString() + "\t")
//			println(row[GoodsGenObject.useType])
			val dto = GoodsGenObjectDto(
					id = row[GoodsGenObject.id],
					useType = row[GoodsGenObject.useType],
					event = row[GoodsGenObject.event],
					referenceKey = row[GoodsGenObject.referenceKey],
					modifiedAt = row[GoodsGenObject.modifiedAt]
			)
			println(dto)
		}
	}
}

data class GoodsGenObjectDto(
		val id: Long?,
		val useType: String?,
		val event: String?,
		val referenceKey: String?,
		val modifiedAt: LocalDateTime?
)

object GoodsGenObject : Table<Nothing>("goods_gen_object") {
	val id by long("goodsGenObjectId").primaryKey()
	val useType by varchar("useType")
	val event by varchar("event")
	val referenceKey by varchar("referenceKey")
	val modifiedAt by datetime("modifiedAt")
}
