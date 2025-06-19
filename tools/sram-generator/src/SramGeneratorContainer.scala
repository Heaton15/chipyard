package sram.generator

import chisel3._

class SramGeneratorContainer(
  mode:              CompilerMode,
  confFile:          String,
  macroJsonFile:     Option[String],
  forceSynflopsMems: Seq[String],
  useCompiler:       Boolean,
  outSramJsonPath:   Option[String],
) extends RawModule {}

sealed trait CompilerMode
object CompilerMode {
  def toModeType(mode: String): CompilerMode = {
    mode match {
      case "synflops" => SynFlops
      case "strict"   => Strict
      case _          => throw new Exception(s"*** ERROR *** $mode is an invalid option for --mode.")
    }

  }
}
case object SynFlops extends CompilerMode
case object Strict   extends CompilerMode
