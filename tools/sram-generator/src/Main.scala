package sram.generator

import mainargs.{arg, main, Flag, ParserForMethods}
import circt.stage.ChiselStage

object Main {

  @main(name = "SRAM Macro Generator CLI Interface")
  def run(
    @arg(name = "mode", doc = "SRAM macro compiler mode")
    modeStr: String,
    @arg(name = "macro-conf", doc = "The set of memories to compile")
    macroConfFile: String,
    @arg(name = "macro-library-file", doc = "MDF JSON file containing available memory macros")
    macroJsonFile: Option[String],
    @arg(
      name = "force-synflops",
      doc  = "A list of SRAMs that should be forced to to behavioral SRAM despite --mode=strict",
    )
    forceSynflopsMems: Seq[String],
    @arg(name = "use-compiler", doc = "If enabled, use the SRAM Compiler in the MDF JSON file in 'strict' mode")
    useCompiler: Flag,
    @arg(name = "output-dir", doc = "Output directory")
    outputDir: String,
    @arg(name = "output-base-name", doc = "Base name of output FIRRTL file")
    outputBaseName: String,
    @arg(name = "out-srams-json", doc = "Path to output JSON file which will contain all used SRAM Macros")
    outSramJsonPath: Option[String],
  ) = {
    println("This is the entry point for the SRAM Generator")

    // Grab all of the memory configurations from the *mems.conf file
    val macroConfFiles = scala.io.Source.fromFile(macroConfFile).getLines().map(ConfParser.fromString).toSeq
    val macroJsonFile  = ???

    ChiselStage.emitCHIRRTLFile(
      new SramGeneratorContainer(
        CompilerMode.fromString(modeStr),
        macroConfFiles,
        macroJsonFile,
        forceSynflopsMems,
        useCompiler.value,
        outSramJsonPath,
      ),
      Array(
        "--target-dir",
        outputDir,
        "--chisel-output-file",
        outputBaseName,
      ),
    )

  }

  def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args.toIndexedSeq)

}
