/*
 * Copyright (c) 2021. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.oewntk.model.TagCount
import java.io.*
import java.nio.charset.StandardCharsets

/**
 * Sense-to-tag_count parser
 *
 * @property inDir extra WNDB dir
 */
class SenseToTagCountsParser(
	private val inDir: File
) {
	/**
	 * Parse tag counts per sense
	 *
	 * @return collection of sensekey-tag_count pairs
	 * @throws IOException io exception
	 */
	@Throws(IOException::class)
	fun parse(): Collection<Pair<String, TagCount>> {
		val result: MutableCollection<Pair<String, TagCount>> = ArrayList()
		parseTagCounts(File(inDir, "cntlist.rev"), result)
		return result
	}

	companion object {

		/**
		 * Parse tag counts per sense
		 *
		 * @param file    file
		 * @param entries sensekey-tag_count pairs accumulator
		 * @throws IOException io exception
		 */
		@Throws(IOException::class)
		private fun parseTagCounts(file: File, entries: MutableCollection<Pair<String, TagCount>>) {
			// iterate on lines
			BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)).use { reader ->
				var lineCount = 0
				reader.useLines { lines ->
					lines.forEach { line ->
						lineCount++
						if (line.isNotEmpty() && line[0] != ' ') {
							try {
								val fields = line.split("\\s+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
								val sensekey = fields[0]
								// int sensenum = Integer.parseInt(fields[1]);
								val senseNum = fields[1].toInt()
								val tagCnt = fields[2].toInt()

								entries.add(Pair(sensekey, TagCount(senseNum, tagCnt)))
							} catch (e: RuntimeException) {
								Tracing.psErr.println("[E] at line $lineCount $e")
							}
						}
					}
				}
			}
		}
	}
}
