/*
 * Copyright (c) 2021-2024. Bernard Bou.
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
    private val inDir: File,
) {

    /**
     * Parse verb templates per sense
     *
     * @return collection of sensekey-verb_templates_ids pairs
     * @throws IOException io exception
     */
    @Throws(IOException::class)
    fun parse(): Collection<Pair<String, Array<Int>>> {
        val result: MutableCollection<Pair<String, Array<Int>>> = ArrayList()
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
        private fun parseVerbTemplates(file: File, entries: MutableCollection<Pair<String, Array<Int>>>) {
            // iterate on lines
            BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)).use { reader ->
                var lineCount = 0
                reader.useLines { lines ->
                    lines.forEach { line ->
                        lineCount++
                        if (line.isNotEmpty() || line[0] != ' ') {
                            try {
                                val fields = line.split("[\\s,]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                val sensekey = fields[0]
                                val templateIds = Array(fields.size - 1) {
                                    fields[it + 1].toInt()
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
    }
}
