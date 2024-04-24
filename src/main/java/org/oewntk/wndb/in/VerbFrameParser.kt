/*
 * Copyright (c) 2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.oewntk.model.VerbFrame
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Verb frames parser
 *
 * @property inDir extra WNDB dir
 */
class VerbFrameParser(
	private val inDir: File
) {
	/**
	 * Parse verb frames
	 *
	 * @return collection of verb frames
	 * @throws IOException io exception
	 */
	@Throws(IOException::class)
	fun parse(): Collection<VerbFrame> {
		val result: MutableCollection<VerbFrame> = ArrayList()
		parseVerbFrames(File(inDir, "verbFrames.txt"), result)
		return result
	}

	/**
	 * Parse verb frames
	 *
	 * @param file       file
	 * @param verbFrames accumulator of verb frames
	 * @throws IOException io exception
	 */
	@Throws(IOException::class)
	private fun parseVerbFrames(file: File, verbFrames: MutableCollection<VerbFrame>) {
		// iterate on lines
		BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)).use { reader ->
			var lineCount = 0
			reader.useLines { lines ->
				lines.forEach { line ->
					lineCount++
					if (line.isNotEmpty() || line[0] != ' ') {
						try {
							val fields = line.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
							val field1 = fields[0]
							val field2 = fields[1].trim { it <= ' ' }
							verbFrames.add(VerbFrame(field1, field2))
						} catch (e: RuntimeException) {
							Tracing.psErr.println("[E] verb frame at line $lineCount $e")
						}
					}
				}
			}
		}
	}
}
