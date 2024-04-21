/*
 * Copyright (c) 2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Sense-to-verb_templates parser
 *
 * @property inDir extra WNDB dir
 */
class SenseToVerbTemplatesParser(
	private val inDir: File
) {
	/**
	 * Parse verb templates per sense
	 *
	 * @return collection of sensekey-verb_templates_ids pairs
	 * @throws IOException io exception
	 */
	@Throws(IOException::class)
	fun parse(): Collection<Pair<String, Array<Int?>>> {
		val result: MutableCollection<Pair<String, Array<Int?>>> = ArrayList()
		parseVerbTemplates(File(inDir, "sentidx.vrb"), result)
		return result
	}

	companion object {
		/**
		 * Parse verb templates per sense
		 *
		 * @param file    file
		 * @param entries accumulator of sensekey-verb_templates_ids pairs
		 * @throws IOException io exception
		 */
		@Throws(IOException::class)
		private fun parseVerbTemplates(file: File, entries: MutableCollection<Pair<String, Array<Int?>>>) {
			// iterate on lines
			BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)).use { reader ->
				var lineCount = 0
				var line: String
				while ((reader.readLine().also { line = it }) != null) {
					lineCount++
					if (line.isEmpty() || line[0] == ' ') {
						continue
					}

					try {
						val fields = line.split("[\\s,]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
						val sensekey = fields[0]
						val templateIds = arrayOfNulls<Int>(fields.size - 1)
						for (i in 1 until fields.size) {
							templateIds[i - 1] = fields[i].toInt()
						}
						entries.add(Pair(sensekey, templateIds))
					} catch (e: RuntimeException) {
						Tracing.psErr.println("[E] verb templates at line $lineCount $e")
					}
				}
			}
		}
	}
}
