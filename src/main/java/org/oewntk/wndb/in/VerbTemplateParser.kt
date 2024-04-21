/*
 * Copyright (c) 2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.oewntk.model.VerbTemplate
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Verb templates parser
 */
class VerbTemplateParser(
	private val inDir: File
) {
	/**
	 * Parse verb templates
	 *
	 * @return collection of verb templates
	 * @throws IOException io exception
	 */
	@Throws(IOException::class)
	fun parse(): Collection<VerbTemplate> {
		val result: MutableCollection<VerbTemplate> = ArrayList()
		parseVerbTemplates(File(inDir, "verbTemplates.txt"), result)
		return result
	}

	companion object {
		/**
		 * Parse verb templates
		 *
		 * @param file          file
		 * @param verbTemplates accumulator of verb templates
		 * @throws IOException io exception
		 */
		@Throws(IOException::class)
		private fun parseVerbTemplates(file: File, verbTemplates: MutableCollection<VerbTemplate>) {
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
						val fields = line.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
						val field1 = fields[0]
						val field2 = fields[1].trim { it <= ' ' }
						val id = field1.toInt()
						verbTemplates.add(VerbTemplate(id, field2))
					} catch (e: RuntimeException) {
						Tracing.psErr.println("[E] verb templates at line $lineCount $e")
					}
				}
			}
		}
	}
}
