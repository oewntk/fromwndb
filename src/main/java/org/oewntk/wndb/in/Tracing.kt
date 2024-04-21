/*
 * Copyright (c) 2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import java.io.OutputStream
import java.io.PrintStream

object Tracing {
	@JvmField
	val psInfo: PrintStream = System.out

	@JvmField
	val psErr: PrintStream = System.err

	@JvmField
	val psNull: PrintStream = PrintStream(object : OutputStream(
	) {
		override fun write(i: Int) {
			// do nothing
		}
	})
}
