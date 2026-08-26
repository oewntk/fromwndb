/*
 * Copyright (c) 2021-2024. Bernard Bou.
 */
package org.oewntk.wndb.`in`

import org.oewntk.model.SenseKey
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
    fun parse(): List<Pair<SenseKey, List<Int>>> {
        val result: MutableList<Pair<SenseKey, List<Int>>> = ArrayList()
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
        private fun parseVerbTemplates(file: File, entries: MutableList<Pair<SenseKey, List<Int>>>) {
            // iterate on lines
            BufferedReader(InputStreamReader(FileInputStream(file), StandardCharsets.UTF_8)).use { reader ->
                var lineCount = 0
                reader.useLines { lines ->
                    lines.forEach { line ->
                        lineCount++
                        if (line.isNotEmpty() || line[0] != ' ') {
                            try {
                                val fields = line.split("[\\s,]+".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
                                val senseKey = SenseKey(fields[0])
                                val templateIds = fields.drop(1).map(String::toInt).toList()
                                entries.add(Pair(senseKey, templateIds))
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
