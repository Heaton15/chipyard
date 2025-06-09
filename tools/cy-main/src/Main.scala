package cy.main

import chisel3.RawModule
import circt.stage.ChiselStage

import chipyard.stage.phases.TargetDirKey
import mainargs.{arg, main, Leftover, ParserForMethods}
import org.chipsalliance.cde.config.{Config, Parameters}
import org.chipsalliance.diplomacy.lazymodule.LazyModule

object Main {
  @main(name = "Chipyard CLI")
  def run(
    @arg(name = "output-dir", doc = "Output directory")
    targetDir: String,
    @arg(name = "top", doc = "Top Module/LazyModule class")
    top: String,
    @arg(name = "config", doc = "CDE config class name")
    config: String,
    @arg(name = "output-base-name", doc = "Base name of output files")
    outputBaseName: String,
    @arg(name = "chisel-args", doc = "Additional arguments to pass to Chisel")
    chiselArgs: Leftover[String],
  ) = {
    // Construct the CDE `Config` instance.
    val rawConfigClass =
      try {
        Class
          .forName(config)
          .getDeclaredConstructor()
          .newInstance()
          .asInstanceOf[Config]
      } catch {
        case e: java.lang.ClassNotFoundException =>
          throw new Exception(s"Unable to find CDE class '$config', check for typos", e)
      }

    // Customize `Config`:
    // - Point to the proper output directory.
    val configClass = rawConfigClass.alterPartial { case TargetDirKey => targetDir }

    // This function will generate the Chisel design. The class in question must take a `Parameters` object as the sole
    // argument.
    def gen = Class
      .forName(top)
      .getConstructor(classOf[Parameters])
      .newInstance(configClass) match {
      case m: RawModule   => m
      case lm: LazyModule => LazyModule(lm).module
    }

    // Run the Chisel generator.
    ChiselStage.emitCHIRRTLFile(
      gen,
      Array(
        "--target-dir",
        targetDir,
        "--chisel-output-file",
        outputBaseName,
      ) ++ chiselArgs.value,
    )

    // Write RocketChip `ElaborationArtefacts` to files, if any exist.
    freechips.rocketchip.util.ElaborationArtefacts.files.foreach { case (ext, contents) =>
      os.write.over(os.Path(targetDir) / s"${outputBaseName}.${ext}", contents())
    }

  }

  def main(args: Array[String]): Unit = ParserForMethods(this).runOrExit(args.toIndexedSeq)
}

