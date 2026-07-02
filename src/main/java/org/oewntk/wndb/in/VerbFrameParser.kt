/*
 * Copyright (c) 2021-2024. Bernard Bou.
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
    private val inDir: File,
) {

    /**
     * Parse verb frames
     *
     * @return collection of verb frames
     * @throws IOException io exception
     */
    @Throws(IOException::class)
    fun parse(): List<VerbFrame> {
        val result: MutableList<VerbFrame> = ArrayList()
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
            reader.useLines { lineSeq ->
                val lines = lineSeq.toList()
                val nFields = detectFields(lines[0])
                lines.forEach { line ->
                    lineCount++
                    if (line.isNotEmpty() || line[0] != ' ') {
                        try {
                            val fields = line.split("\\s+".toRegex(), limit = nFields).dropLastWhile { it.isEmpty() }.toTypedArray()
                            val id = fields[0]
                            val frame = fields[if (nFields == 2) 1 else 2].trim { it <= ' ' }
                            verbFrames.add(VerbFrame(id, frame))
                        } catch (e: RuntimeException) {
                            Tracing.psErr.println("[E] verb frame at line $lineCount $e")
                        }
                    }
                }
            }
        }
    }

    private fun detectFields(firstLine: String): Int {
        val field1 = firstLine.split(" ".toRegex())[0]
        return try {
            field1.toInt()
            2
        } catch (_: NumberFormatException) {
            3
        }
    }
}